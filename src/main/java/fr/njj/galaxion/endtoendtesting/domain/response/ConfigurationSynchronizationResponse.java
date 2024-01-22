package fr.njj.galaxion.endtoendtesting.domain.response;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.SynchronizationStatus;
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
public class ConfigurationSynchronizationResponse {

    private SynchronizationStatus status;

    private String error;

    private ZonedDateTime syncDate;
}
