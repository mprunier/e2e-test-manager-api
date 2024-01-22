package fr.njj.galaxion.endtoendtesting.client.gitlab.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PipelineRequest {

    private String ref;

    @Singular
    private List<VariableRequest> variables;
}
