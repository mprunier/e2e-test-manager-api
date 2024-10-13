package fr.njj.galaxion.endtoendtesting.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "parallelPipelineProgress",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<PipelineEntity> pipelines;

  public boolean isAllCompleted() {
    return totalPipelines == completedPipelines;
  }

  public void incrementCompletedPipelines() {
    completedPipelines++;
  }
}
