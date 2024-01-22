package fr.njj.galaxion.endtoendtesting.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CreateUpdateEnvironmentVariableRequest {

  @NotBlank private String name;

  @Setter @NotBlank private String defaultValue;

  private String description;

  private Boolean isHidden;
}
