package fr.njj.galaxion.endtoendtesting.lib.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class ExceptionResponse {

  private Integer status;
  private String title;
  private String detail;
  private String description;
  private Object violations;
}
