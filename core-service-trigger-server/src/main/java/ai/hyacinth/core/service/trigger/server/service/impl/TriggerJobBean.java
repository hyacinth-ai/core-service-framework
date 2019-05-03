package ai.hyacinth.core.service.trigger.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TriggerJobBean extends QuartzJobBean {
  @Autowired private TriggerClientService triggerService;

  public TriggerJobBean() {
    super();
  }

  public void executeInternal(JobExecutionContext context) {
    JobDetail jobDetail = context.getJobDetail();
    JobKey jobKey = jobDetail.getKey();
    Long triggerId =
        jobDetail.getJobDataMap().getLong(TriggerServiceConstants.JOB_DATA_KEY_TRIGGER_ID);

    log.info("TriggerJob started, job-key: {}, triggerId: {}", jobKey, triggerId);

    triggerService.trigger(triggerId);
  }
}
