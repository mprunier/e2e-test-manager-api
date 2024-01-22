package fr.njj.galaxion.endtoendtesting.client.gitlab.exception;

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
