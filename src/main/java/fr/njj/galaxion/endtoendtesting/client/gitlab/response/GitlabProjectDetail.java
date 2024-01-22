package fr.njj.galaxion.endtoendtesting.client.gitlab.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GitlabProjectDetail {

    @JsonProperty("http_url_to_repo")
    public String repoUrl;
}
