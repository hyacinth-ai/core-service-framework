package ai.hyacinth.core.service.jpa.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import javax.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@MappedSuperclass
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonStringType.class),
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
    @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class)
})
public class BaseAuditingEntity { // implements Auditable<Long, Long, Date> {
  @CreatedDate
  @Temporal(TIMESTAMP)
  @Column(name = "created_date")
  protected Date createdDate;

  @LastModifiedDate
  @Temporal(TIMESTAMP)
  @Column(name = "last_modified_date")
  protected Date lastModifiedDate;

  @Version
  protected Integer version;
}
