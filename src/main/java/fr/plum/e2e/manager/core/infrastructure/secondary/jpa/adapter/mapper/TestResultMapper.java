package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVariable;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestResultEntity;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestResultMapper {

  public static JpaTestResultEntity toEntity(TestResult domain) {
    var variables =
        domain.getVariables().stream()
            .collect(
                Collectors.toMap(TestResultVariable::name, TestResultVariable::value, (a, b) -> b));
    var testResult =
        JpaTestResultEntity.builder()
            .id(domain.getId().value())
            .workerId(domain.getWorkerId().value())
            .configurationTestId(domain.getTestConfigurationId().value())
            .status(domain.getStatus())
            .reference(domain.getReference() != null ? domain.getReference().value() : null)
            .errorUrl(domain.getErrorUrl() != null ? domain.getErrorUrl().value() : null)
            .errorMessage(
                domain.getErrorMessage() != null ? domain.getErrorMessage().value() : null)
            .errorStacktrace(
                domain.getErrorStacktrace() != null ? domain.getErrorStacktrace().value() : null)
            .code(domain.getCode() != null ? domain.getCode().value() : null)
            .duration(domain.getDuration() != null ? domain.getDuration().value() : null)
            .variables(variables)
            .build();
    testResult.setAuditFields(domain.getAuditInfo());
    return testResult;
  }
}
