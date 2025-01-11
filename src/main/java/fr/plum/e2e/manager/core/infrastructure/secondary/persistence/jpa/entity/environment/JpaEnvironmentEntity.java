package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.environment;

import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.AbstractAuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "environment")
public class JpaEnvironmentEntity extends AbstractAuditableEntity {

  @Id private UUID id;

  @Column(name = "description", nullable = false, unique = true)
  private String description;

  @Column(name = "branch", nullable = false)
  private String branch;

  @Column(name = "project_id", nullable = false)
  private String projectId;

  @Column(name = "token", nullable = false)
  private String token;

  @Column(name = "max_parallel_test_number", nullable = false)
  private int maxParallelTestNumber;

  @Column(name = "is_enabled", nullable = false)
  private boolean isEnabled;

  @Setter
  @OrderBy("name ASC")
  @Fetch(value = FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "environment",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<JpaEnvironmentVariableEntity> variables;
}
