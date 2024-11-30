package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.converter.StringMapConverter;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.AbstractAuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
  @Column(name = "variables", columnDefinition = "jsonb")
  @Builder.Default
  private Map<String, String> variables = new HashMap<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "worker_id", nullable = false)
  @Builder.Default
  private List<JpaWorkerUnitEntity> units = new ArrayList<>();
}
