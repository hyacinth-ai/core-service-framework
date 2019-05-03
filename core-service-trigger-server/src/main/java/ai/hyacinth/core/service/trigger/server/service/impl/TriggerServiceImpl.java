package ai.hyacinth.core.service.trigger.server.service.impl;

import ai.hyacinth.core.service.trigger.server.domain.ServiceTrigger;
import ai.hyacinth.core.service.trigger.server.dto.TriggerChangeResult;
import ai.hyacinth.core.service.trigger.server.dto.TriggerCreationRequest;
import ai.hyacinth.core.service.trigger.server.dto.TriggerInfo;
import ai.hyacinth.core.service.trigger.server.dto.TriggerQueryRequest;
import ai.hyacinth.core.service.trigger.server.dto.TriggerUpdateRequest;
import ai.hyacinth.core.service.trigger.server.dto.type.ServiceTriggerMethodType;
import ai.hyacinth.core.service.trigger.server.error.TriggerServiceErrorCode;
import ai.hyacinth.core.service.trigger.server.repo.ServiceTriggerRepository;
import ai.hyacinth.core.service.trigger.server.service.TriggerService;
import ai.hyacinth.core.service.web.common.ServiceApiException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.domain.Example;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class TriggerServiceImpl implements TriggerService {
  @Autowired private Scheduler scheduler;
  @Autowired private ServiceTriggerRepository triggerRepo;

  private Supplier<ServiceApiException> TRIGGER_NOT_FOUND =
      () -> new ServiceApiException(TriggerServiceErrorCode.TRIGGER_NOT_FOUND);

  @Override
  @Transactional
  @NonNull
  public TriggerChangeResult removeTrigger(Long triggerId) {
    return triggerRepo
        .findById(triggerId)
        .map(this::removeQuartzJob)
        .map(
            (completed) -> {
              if (completed) {
                triggerRepo.deleteById(triggerId);
              }
              return new TriggerChangeResult(triggerId, completed);
            })
        .orElseThrow(TRIGGER_NOT_FOUND);
  }

  @Override
  @Transactional
  @NonNull
  public TriggerInfo createTrigger(TriggerCreationRequest request) {
    ServiceTriggerMethodType triggerMethod = request.getTriggerMethod();
    if (triggerMethod == null) {
      triggerMethod = detectTriggerMethodType(request);
    }

    String service = request.getService();
    if (StringUtils.isEmpty(service)) {
      service = TriggerServiceConstants.SERVICE_GLOBAL;
    }

    Optional<ServiceTrigger> st = triggerRepo.findByServiceAndName(service, request.getName());

    if (st.isPresent()) {
      throw new ServiceApiException(TriggerServiceErrorCode.TRIGGER_EXISTS);
    }

    ServiceTrigger serviceTrigger =
        ServiceTrigger.builder()
            .name(request.getName())
            .cron(request.getCron())
            .service(service)
            .url(request.getUrl())
            .triggerMethod(triggerMethod)
            .httpMethod(request.getHttpMethod())
            .params(request.getParams())
            .enabled(true)
            .timeout(request.getTimeout())
            .build();

    triggerRepo.save(serviceTrigger);

    addQuartzJob(serviceTrigger);

    log.info("service trigger created: {}", serviceTrigger);

    return toDto(serviceTrigger);
  }

  private ServiceTriggerMethodType detectTriggerMethodType(TriggerCreationRequest request) {
    ServiceTriggerMethodType triggerMethod = null;
    boolean serviceEmtpy = StringUtils.isEmpty(request.getService());
    boolean urlEmpty = StringUtils.isEmpty(request.getUrl());
    if (serviceEmtpy && !urlEmpty) {
      triggerMethod = ServiceTriggerMethodType.URL;
    }
    if (!serviceEmtpy && urlEmpty) {
      triggerMethod = ServiceTriggerMethodType.BUS_EVENT;
    }
    if (!serviceEmtpy && !urlEmpty) {
      triggerMethod = ServiceTriggerMethodType.SERVICE_URL;
    }
    return triggerMethod;
  }

  @Override
  @Transactional(readOnly = true)
  public TriggerInfo findTriggerById(Long id) {
    return triggerRepo.findById(id).map(this::toDto).orElseThrow(TRIGGER_NOT_FOUND);
  }

  @Override
  @Transactional(readOnly = true)
  public TriggerInfo findTriggerByServiceAndName(String service, String name) {
    return triggerRepo
        .findByServiceAndName(service, name)
        .map(this::toDto)
        .orElseThrow(TRIGGER_NOT_FOUND);
  }

  @Override
  @Transactional(readOnly = true)
  @NonNull
  public List<TriggerInfo> findAllTriggers(TriggerQueryRequest queryRequest) {
    ServiceTrigger stExample =
        ServiceTrigger.builder()
            .name(queryRequest.getName())
            .service(queryRequest.getService())
            .id(queryRequest.getId())
            .build();
    return triggerRepo.findAll(Example.of(stExample)).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public TriggerInfo updateTrigger(Long triggerId, TriggerUpdateRequest updateRequest) {
    return triggerRepo
        .findById(triggerId)
        .map(
            (st) -> {
              boolean cronChanged = false;
              if (updateRequest.getCron() != null
                  && !updateRequest.getCron().equals(st.getCron())) {
                cronChanged = true;
              }

              PropertyMapper mapper = PropertyMapper.get();
              mapper.from(updateRequest::getCron).whenNonNull().to(st::setCron);
              mapper.from(updateRequest::getEnabled).whenNonNull().to(st::setEnabled);
              mapper.from(updateRequest::getTimeout).whenNonNull().to(st::setTimeout);
              triggerRepo.save(st);

              if (cronChanged) {
                updateQuartzJob(st);
              }

              return st;
            })
        .map(this::toDto)
        .orElseThrow(TRIGGER_NOT_FOUND);
  }

  private TriggerInfo toDto(ServiceTrigger serviceTrigger) {
    return TriggerInfo.builder()
        .id(serviceTrigger.getId())
        .name(serviceTrigger.getName())
        .timeout(serviceTrigger.getTimeout())
        .service(serviceTrigger.getService())
        .cron(serviceTrigger.getCron())
        .triggerMethod(serviceTrigger.getTriggerMethod())
        .url(serviceTrigger.getUrl())
        .httpMethod(serviceTrigger.getHttpMethod())
        .params(serviceTrigger.getParams())
        .enabled(serviceTrigger.getEnabled())
        .build();
  }

  private boolean updateQuartzJob(ServiceTrigger serviceTrigger) {
    try {
      scheduler.rescheduleJob(
          TriggerKey.triggerKey(serviceTrigger.getName(), serviceTrigger.getService()),
          createQuartzTrigger(serviceTrigger));
      return true;
    } catch (SchedulerException ex) {
      throw new ServiceApiException(TriggerServiceErrorCode.QUARTZ_ERROR, ex);
    }
  }

  private boolean removeQuartzJob(ServiceTrigger st) {
    JobKey key = JobKey.jobKey(st.getName(), st.getService());
    try {
      return scheduler.deleteJob(key);
    } catch (SchedulerException ex) {
      throw new ServiceApiException(TriggerServiceErrorCode.QUARTZ_ERROR, ex);
    }
  }

  private void addQuartzJob(ServiceTrigger serviceTrigger) {
    final Class<? extends Job> quartzJobClass = TriggerJobBean.class;

    JobDetail jobDetail =
        JobBuilder.newJob()
            .withIdentity(serviceTrigger.getName(), serviceTrigger.getService())
            .usingJobData(TriggerServiceConstants.JOB_DATA_KEY_TRIGGER_ID, serviceTrigger.getId())
            .ofType(quartzJobClass)
            .build();

    CronTrigger trigger = createQuartzTrigger(serviceTrigger);

    try {
      scheduler.scheduleJob(jobDetail, trigger);
      log.info(
          "scheduler status, is shutdown: {}, is standby: {}",
          scheduler.isShutdown(),
          scheduler.isInStandbyMode());
    } catch (SchedulerException ex) {
      throw new ServiceApiException(TriggerServiceErrorCode.QUARTZ_ERROR, ex);
    }
  }

  private CronTrigger createQuartzTrigger(ServiceTrigger serviceTrigger) {
    return TriggerBuilder.newTrigger()
        .withIdentity(serviceTrigger.getName(), serviceTrigger.getService())
        .withSchedule(CronScheduleBuilder.cronSchedule(serviceTrigger.getCron()))
        .startNow()
        .build();
  }
}
