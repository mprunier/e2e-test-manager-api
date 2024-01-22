package fr.njj.galaxion.endtoendtesting.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CreateUpdateEnvironmentRequest {

    @NotBlank
    private String description;

    @NotBlank
    private String projectId;

    @NotBlank
    private String token;

    @NotBlank
    private String branch;

    @Builder.Default
    private List<CreateUpdateEnvironmentVariableRequest> variables = new ArrayList<>();
}
