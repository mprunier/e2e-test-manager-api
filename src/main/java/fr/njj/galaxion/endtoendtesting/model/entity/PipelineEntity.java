package fr.njj.galaxion.endtoendtesting.model.entity;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.model.converter.StringListConverter;
import fr.njj.galaxion.endtoendtesting.model.converter.StringMapConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
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
@Table(name = "pipeline")
public class PipelineEntity extends PanacheEntityBase {

  @Id private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private PipelineType type;

  @ManyToOne
  @JoinColumn(
      name = "environment_id",
      foreignKey = @ForeignKey(name = "fk__pipeline__environment_id"))
  private EnvironmentEntity environment;

  @Setter
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PipelineStatus status = PipelineStatus.IN_PROGRESS;

  @Setter
  @Convert(converter = StringListConverter.class)
  @Column(name = "configuration_test_ids_filter")
  private List<String> configurationTestIdsFilter;

  @Setter
  @Convert(converter = StringListConverter.class)
  @Column(name = "files_filter")
  private List<String> filesFilter;

  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();

  @Setter
  @Column(name = "updated_at")
  private ZonedDateTime updatedAt;

  @ManyToOne
  @JoinColumn(
      name = "pipeline_group_id",
      foreignKey = @ForeignKey(name = "fk__pipeline__pipeline_group_id"))
  private PipelineGroupEntity pipelineGroup;

  @Convert(converter = StringMapConverter.class)
  @Column(name = "variables")
  private Map<String, String> variables;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  public boolean hasTestIdsFilter() {
    return configurationTestIdsFilter != null && !configurationTestIdsFilter.isEmpty();
  }

  public boolean hasFilesFilter() {
    return filesFilter != null && !filesFilter.isEmpty();
  }
}
