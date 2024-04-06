package fr.njj.galaxion.endtoendtesting.domain.request.webhook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GitlabWebHookCommitRequest {

    private List<String> added;
    private List<String> modified;
    private List<String> removed;
}
