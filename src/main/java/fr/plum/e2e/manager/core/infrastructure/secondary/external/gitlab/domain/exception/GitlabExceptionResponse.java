package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.domain.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class GitlabExceptionResponse {

  private Object message;
}
