package fr.njj.galaxion.endtoendtesting.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metrics")
public class MetricsEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(
      name = "environment_id",
      foreignKey = @ForeignKey(name = "fk__configuration__environment_id"))
  private EnvironmentEntity environment;

  @Setter
  @Column(name = "suites")
  private Integer suites;

  @Setter
  @Column(name = "tests")
  private Integer tests;

  @Setter
  @Column(name = "passes")
  private Integer passes;

  @Setter
  @Column(name = "failures")
  private Integer failures;

  @Setter
  @Column(name = "skipped")
  private Integer skipped;

  @Setter
  @Column(name = "pass_percent")
  private Integer passPercent;

  @Setter
  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();

  @Column(name = "is_all_tests_run")
  private boolean isAllTestsRun;
}
