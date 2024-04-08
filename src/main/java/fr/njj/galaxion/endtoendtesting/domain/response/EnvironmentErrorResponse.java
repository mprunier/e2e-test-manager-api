package fr.njj.galaxion.endtoendtesting.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class EnvironmentErrorResponse {

    private String file;

    private String error;

    private ZonedDateTime at;
}
