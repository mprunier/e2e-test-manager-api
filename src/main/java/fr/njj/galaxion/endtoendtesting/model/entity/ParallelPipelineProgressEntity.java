package fr.njj.galaxion.endtoendtesting.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "parallel_pipeline_progress")
public class ParallelPipelineProgressEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "environment_id", nullable = false)
  private EnvironmentEntity environment;

  @Column(name = "total_pipelines", nullable = false)
  private int totalPipelines;

  @Builder.Default
  @Column(name = "completed_pipelines", nullable = false)
  private int completedPipelines = 0;

  public boolean isCompleted() {
    return totalPipelines == completedPipelines;
  }
}
