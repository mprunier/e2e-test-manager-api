package fr.njj.galaxion.endtoendtesting.domain.response;

import java.time.ZonedDateTime;
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
public class SyncEnvironmentErrorResponse {

  private String file;

  private String error;

  private ZonedDateTime at;
}
