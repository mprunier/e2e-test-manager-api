package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.SchedulerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SchedulerService {

    @Transactional
    public void create(EnvironmentEntity environment, String createdBy, String pipelineId) {
        SchedulerEntity.builder()
                       .environment(environment)
                       .createdBy(createdBy)
                       .pipelineId(pipelineId)
                       .build()
                       .persist();

    }
}

