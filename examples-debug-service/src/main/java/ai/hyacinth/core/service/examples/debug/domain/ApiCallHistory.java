package ai.hyacinth.core.service.examples.debug.domain;

import ai.hyacinth.core.service.jpa.converter.Map2JsonConverter;
import ai.hyacinth.core.service.jpa.domain.BaseAuditingEntity;
import java.util.Date;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiCallHistory extends BaseAuditingEntity {
  @Id
  @GeneratedValue
  private Long id;

  @Column
  String path;

  @Column
  String requestMethod;

  @Column
  Date requestTime;

  @Convert(converter = Map2JsonConverter.class)
  @Column(columnDefinition = "CLOB")
  Map<String, ?> requestParameters;

  @Convert(converter = Map2JsonConverter.class)
  @Column(columnDefinition = "CLOB")
  Object requestBody;

  @Convert(converter = Map2JsonConverter.class)
  @Column(columnDefinition = "CLOB")
  Object requestHeaders;
}
