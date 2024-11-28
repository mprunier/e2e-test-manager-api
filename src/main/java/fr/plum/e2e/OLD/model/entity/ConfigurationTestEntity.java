package fr.plum.e2e.OLD.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "old_configuration_test")
public class ConfigurationTestEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(
      name = "environment_id",
      foreignKey = @ForeignKey(name = "fk__configuration_test__environment_id"),
      nullable = false)
  private EnvironmentEntity environment;

  //  @Builder.Default
  //  @Enumerated(EnumType.STRING)
  //  @Column(name = "status", nullable = false)
  //  private ConfigurationStatus status = ConfigurationStatus.NEW;

  @Setter
  @Column(name = "file", nullable = false)
  private String file;

  @Setter
  @Column(name = "title", nullable = false)
  private String title;

  @ManyToOne
  @JoinColumn(
      name = "configuration_suite_id",
      foreignKey = @ForeignKey(name = "fk__configuration_test__configuration_suite_id"),
      nullable = false)
  private ConfigurationSuiteEntity configurationSuite;

  //  @Setter
  //  @Convert(converter = StringListConverter.class)
  //  @Column(name = "variables")
  //  private List<String> variables;

  @Setter
  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "configurationTest",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<TestEntity> tests;

  @Setter
  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "configurationTest",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ConfigurationTestTagEntity> configurationTags;

  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();

  @Setter
  @Column(name = "updated_at")
  private ZonedDateTime updatedAt;

  @Setter
  @Column(name = "last_played_at")
  private ZonedDateTime lastPlayedAt;

  @Setter
  @Column(name = "position")
  private Integer position;
}
