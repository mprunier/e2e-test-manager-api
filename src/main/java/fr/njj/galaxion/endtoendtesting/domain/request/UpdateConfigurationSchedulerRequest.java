package fr.njj.galaxion.endtoendtesting.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UpdateConfigurationSchedulerRequest {

    @NotNull
    private Boolean isEnabled;

    @NotNull
    private ZonedDateTime scheduledTime;

    @NotNull
    private List<DayOfWeek> daysOfWeek;
}
