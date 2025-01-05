package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.AbstractAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "metrics")
public class JpaMetricsEntity extends AbstractAuditableEntity {

  @Id private UUID id;

  @Column(name = "environment_id", nullable = false)
  private UUID environmentId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private MetricsType type;

  @Column(name = "suites")
  private Integer suites;

  @Column(name = "tests")
  private Integer tests;

  @Column(name = "passes")
  private Integer passes;

  @Column(name = "failures")
  private Integer failures;

  @Column(name = "skipped")
  private Integer skipped;

  @Column(name = "pass_percent")
  private Integer passPercent;
}
