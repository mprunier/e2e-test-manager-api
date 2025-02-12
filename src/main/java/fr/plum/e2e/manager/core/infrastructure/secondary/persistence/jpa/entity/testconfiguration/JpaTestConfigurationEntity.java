package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.converter.StringListConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
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
@Table(name = "test_configuration")
public class JpaTestConfigurationEntity extends PanacheEntityBase {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "title", nullable = false)
  private String title;

  // Status update in SQL view after test result creation
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ConfigurationStatus status = ConfigurationStatus.NEW;

  @ManyToOne
  @JoinColumn(name = "suite_id")
  private JpaSuiteConfigurationEntity suiteConfiguration;

  @Convert(converter = StringListConverter.class)
  @Column(name = "tags", columnDefinition = "_varchar")
  private List<String> tags;

  @Convert(converter = StringListConverter.class)
  @Column(name = "variables", columnDefinition = "_varchar")
  private List<String> variables;

  @Column(name = "last_played_at")
  private ZonedDateTime lastPlayedAt;

  @Column(name = "position")
  private Integer position;
}
