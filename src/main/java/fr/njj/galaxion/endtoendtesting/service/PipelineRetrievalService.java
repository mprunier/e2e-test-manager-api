package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.exception.JobNotFoundException;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.PipelineRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PipelineRetrievalService {

    private final PipelineRepository pipelineRepository;

    @Transactional
    public List<PipelineEntity> getOldInProgress(Integer oldMinutes) {
        return pipelineRepository.getOldInProgress(oldMinutes);
    }

    @Transactional
    public PipelineEntity get(String id) {
        return pipelineRepository.findByIdOptional(id)
                                 .orElseThrow(() -> new JobNotFoundException(id));

    }

    @Transactional
    public long countInProgress() {
        return pipelineRepository.countInProgress();
    }
}
