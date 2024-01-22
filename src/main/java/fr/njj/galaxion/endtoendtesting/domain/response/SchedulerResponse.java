package fr.njj.galaxion.endtoendtesting.domain.response;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.SchedulerStatus;
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
public class SchedulerResponse {

    private Long id;

    private String pipelineId;

    private SchedulerStatus status;

    private Integer suites;

    private Integer tests;

    private Integer passes;

    private Integer failures;

    private Integer skipped;

    private Integer passPercent;

    private ZonedDateTime createdAt;

    private String createdBy;
}
