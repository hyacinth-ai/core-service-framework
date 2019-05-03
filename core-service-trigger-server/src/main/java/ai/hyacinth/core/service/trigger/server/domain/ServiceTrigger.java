package ai.hyacinth.core.service.trigger.server.domain;

import ai.hyacinth.core.service.jpa.domain.BaseAuditingEntity;
import ai.hyacinth.core.service.trigger.server.dto.type.ServiceTriggerMethodType;
import java.time.Duration;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.springframework.http.HttpMethod;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"service", "name"})})
@EqualsAndHashCode(callSuper = false)
public class ServiceTrigger extends BaseAuditingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NaturalId
  @NotNull private String service;

  @NaturalId
  @NotNull private String name;

  @NotNull private ServiceTriggerMethodType triggerMethod;

  private HttpMethod httpMethod;
  private String url;

  @Type(type = "json")
  @Column(columnDefinition = "varchar(500)")
  private Map<String, Object> params;

  @NotNull private String cron;
  private Duration timeout;

  @NotNull private Boolean enabled;
}
