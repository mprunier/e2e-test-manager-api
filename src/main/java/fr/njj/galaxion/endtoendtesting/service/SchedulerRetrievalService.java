package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.exception.SchedulerInProgressException;
import fr.njj.galaxion.endtoendtesting.domain.exception.SchedulerNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.exception.SchedulerPipelineIdNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.response.SchedulerResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.SchedulerEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.SchedulerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.njj.galaxion.endtoendtesting.mapper.SchedulerResponseMapper.buildSchedulerResponse;
import static fr.njj.galaxion.endtoendtesting.mapper.SchedulerResponseMapper.buildSchedulerResponses;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SchedulerRetrievalService {

    private final SchedulerRepository schedulerRepository;

    @Transactional
    public SchedulerEntity getScheduler(Long id) {
        return schedulerRepository.findByIdOptional(id)
                                  .orElseThrow(() -> new SchedulerNotFoundException(id));
    }

    @Transactional
    public SchedulerEntity getSchedulerByPipelineId(String pipelineId) {
        return schedulerRepository.findByPipelineId(pipelineId)
                                  .orElseThrow(() -> new SchedulerPipelineIdNotFoundException(pipelineId));
    }

    @Transactional
    public List<SchedulerResponse> getSchedulerResponses(long environmentId) {
        return buildSchedulerResponses(schedulerRepository.findAllByEnvironment(environmentId));
    }

    @Transactional
    public SchedulerResponse getSchedulerResponse(long schedulerId) {
        var scheduler = getScheduler(schedulerId);
        return buildSchedulerResponse(scheduler);
    }

    @Transactional
    public void assertExistInProgressByEnvironment(Long environmentId) {
        var exist = schedulerRepository.assertExistInProgressByEnvironment(environmentId);
        if (exist) {
            throw new SchedulerInProgressException();
        }
    }
}

