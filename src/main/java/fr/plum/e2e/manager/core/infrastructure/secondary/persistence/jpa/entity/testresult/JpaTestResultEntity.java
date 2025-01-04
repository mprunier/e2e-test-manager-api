package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testresult;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.converter.StringMapConverter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.AbstractAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Map;
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
@Table(name = "test_result")
public class JpaTestResultEntity extends AbstractAuditableEntity {

  @Id private UUID id;

  @Column(name = "worker_id")
  private UUID workerId;

  @Column(name = "configuration_test_id", nullable = false)
  private UUID configurationTestId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TestResultStatus status;

  @Column(name = "reference")
  private String reference;

  @Column(name = "error_url")
  private String errorUrl;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "error_stacktrace")
  private String errorStacktrace;

  @Column(name = "code")
  private String code;

  @Column(name = "duration")
  private Integer duration;

  @Convert(converter = StringMapConverter.class)
  @Column(name = "variables")
  private Map<String, String> variables;
}
