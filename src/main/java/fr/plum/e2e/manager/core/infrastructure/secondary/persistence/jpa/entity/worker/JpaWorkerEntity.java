package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.converter.StringMapConverter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.AbstractAuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Table(name = "worker")
public class JpaWorkerEntity extends AbstractAuditableEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "environment_id", nullable = false)
  private UUID environmentId;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private WorkerType type;

  @Convert(converter = StringMapConverter.class)
  @Column(name = "variables")
  @Builder.Default
  private Map<String, String> variables = new HashMap<>();

  @Setter
  @Fetch(value = FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "worker",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<JpaWorkerUnitEntity> units;
}
