package fr.njj.galaxion.endtoendtesting.model.entity;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.model.converter.StringListConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
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
@Table(name = "configuration_suite")
public class ConfigurationSuiteEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(
      name = "environment_id",
      foreignKey = @ForeignKey(name = "fk__configuration_suite__environment_id"),
      nullable = false)
  private EnvironmentEntity environment;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ConfigurationStatus status = ConfigurationStatus.NEW;

  @Setter
  @Column(name = "file", nullable = false)
  private String file;

  @Setter
  @Column(name = "title", nullable = false)
  private String title;

  @ManyToOne
  @JoinColumn(
      name = "parent_configuration_suite_id",
      foreignKey = @ForeignKey(name = "fk__configuration_suite__parent_configuration_suite_id"))
  private ConfigurationSuiteEntity parentSuite;

  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "parentSuite",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ConfigurationSuiteEntity> subSuites;

  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "configurationSuite",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @OrderBy("position ASC")
  private List<ConfigurationTestEntity> configurationTests;

  @Setter
  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "configurationSuite",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ConfigurationSuiteTagEntity> configurationTags;

  @Setter
  @Convert(converter = StringListConverter.class)
  @Column(name = "variables")
  private List<String> variables;

  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();

  @Setter
  @Column(name = "updated_at")
  private ZonedDateTime updatedAt;

  @Setter
  @Column(name = "last_played_at")
  private ZonedDateTime lastPlayedAt;

  //    public void setStatus(ConfigurationStatus status) {
  //        this.lastPlayedAt = ZonedDateTime.now();
  //        this.status = status;
  //    }
}
