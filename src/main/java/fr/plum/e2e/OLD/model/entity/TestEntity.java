package fr.plum.e2e.OLD.model.entity;

import fr.plum.e2e.OLD.domain.enumeration.ConfigurationStatus;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.converter.StringMapConverter;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
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
@Table(name = "old_test")
public class TestEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "pipeline_id")
  private String pipelineId;

  @Setter
  @Builder.Default
  @Column(name = "is_waiting", nullable = false)
  private boolean isWaiting = true;

  @ManyToOne
  @JoinColumn(
      name = "configuration_test_id",
      foreignKey = @ForeignKey(name = "fk__test__configuration_test_id"),
      nullable = false)
  private ConfigurationTestEntity configurationTest;

  @Convert(converter = StringMapConverter.class)
  @Column(name = "variables")
  private Map<String, String> variables;

  @Setter
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ConfigurationStatus status;

  @Setter
  @Column(name = "reference")
  private String reference;

  @Setter
  @Column(name = "error_url")
  private String errorUrl;

  @Setter
  @Column(name = "error_message")
  private String errorMessage;

  @Setter
  @Column(name = "error_stacktrace")
  private String errorStacktrace;

  @Setter
  @Column(name = "code")
  private String code;

  @Setter
  @Column(name = "duration")
  private Integer duration;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "video_id")
  private TestVideoEntity video;

  @Setter
  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(
      mappedBy = "test",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<TestScreenshotEntity> screenshots;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "created_by", nullable = false)
  private String createdBy;
}
