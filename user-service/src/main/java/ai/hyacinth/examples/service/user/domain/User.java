package ai.hyacinth.examples.service.user.domain;

import ai.hyacinth.core.service.jpa.domain.BaseAuditingEntity;
import java.util.Date;
import java.util.List;
import javax.persistence.ElementCollection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;

@Audited
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "user",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User extends BaseAuditingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false) // unique = true
  @NaturalId
  private String username;

  // security fields
  @Column(nullable = false)
  private String password;

  @Column private Date accountExpirationDate;

  @Column private Date credentialExpirationDate;

  @Column private boolean locked;

  @Column private boolean disabled;

  @ElementCollection
  private List<String> roles; // GrantedAuthority

  // additional information fields
  @Column private LocalDate birthDate;

  @Column(nullable = false)
  private UserType userType;
}
