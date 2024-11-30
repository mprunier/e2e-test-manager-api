package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.converter.WorkerUnitFilterConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "worker_unit")
public class JpaWorkerUnitEntity {

  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private WorkerUnitStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "worker_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_worker_unit_worker"))
  private JpaWorkerEntity worker;

  @Column(name = "filter")
  @Convert(converter = WorkerUnitFilterConverter.class)
  private WorkerUnitFilterDto filter;
}
