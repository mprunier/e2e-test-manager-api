package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.response.SchedulerResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.SchedulerEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchedulerResponseMapper {

    public static SchedulerResponse buildSchedulerResponse(SchedulerEntity entity) {
        return SchedulerResponse
                .builder()
                .id(entity.getId())
                .pipelineId(entity.getPipelineId())
                .status(entity.getStatus())
                .suites(entity.getSuites())
                .tests(entity.getTests())
                .passes(entity.getPasses())
                .failures(entity.getFailures())
                .skipped(entity.getSkipped())
                .passPercent(entity.getPassPercent())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();

    }

    public static List<SchedulerResponse> buildSchedulerResponses(List<SchedulerEntity> entities) {
        return entities.stream()
                       .map(SchedulerResponseMapper::buildSchedulerResponse)
                       .toList();
    }
}
