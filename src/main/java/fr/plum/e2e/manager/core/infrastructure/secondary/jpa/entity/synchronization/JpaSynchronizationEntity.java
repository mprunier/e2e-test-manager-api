package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.synchronization;

import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.AbstractAuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "synchronization")
public class JpaSynchronizationEntity extends AbstractAuditableEntity {

  @Id
  @Column(name = "environment_id", nullable = false)
  private UUID environmentId;

  @Column(name = "is_in_progress", nullable = false)
  @Setter
  private boolean isInProgress;

  @Setter
  @Fetch(value = FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "synchronization",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<JpaSynchronizationErrorEntity> errors;
}
