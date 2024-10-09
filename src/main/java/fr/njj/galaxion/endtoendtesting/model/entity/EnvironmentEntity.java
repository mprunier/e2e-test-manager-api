package fr.njj.galaxion.endtoendtesting.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
public class EnvironmentEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "description", nullable = false, unique = true)
  private String description;

  @Setter
  @Column(name = "branch", nullable = false)
  private String branch;

  @Setter
  @Column(name = "project_id", nullable = false)
  private String projectId;

  @Setter
  @Column(name = "token", nullable = false)
  private String token;

  @Builder.Default
  @Setter
  @Column(name = "is_enabled", nullable = false)
  private Boolean isEnabled = true;

  @Fetch(value = FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "environment",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ConfigurationSuiteEntity> configurationSuites;

  @OrderBy("name ASC")
  @Fetch(value = FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "environment",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<EnvironmentVariableEntity> variables;

  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();

  @Setter
  @Column(name = "updated_at")
  private ZonedDateTime updatedAt;

  @Setter
  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Setter
  @Column(name = "updated_by")
  private String updatedBy;

  @Builder.Default
  @Setter
  @Column(name = "is_locked", nullable = false)
  private Boolean isLocked = false;

  @Setter
  @Column(name = "max_parallel_test_number", nullable = false)
  private Integer maxParallelTestNumber;

  public Set<String> getFiles() {
    return configurationSuites.stream()
        .map(ConfigurationSuiteEntity::getFile)
        .collect(Collectors.toSet());
  }
}
