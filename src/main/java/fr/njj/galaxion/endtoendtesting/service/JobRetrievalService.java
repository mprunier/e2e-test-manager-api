package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.JobType;
import fr.njj.galaxion.endtoendtesting.domain.exception.JobNotFoundException;
import fr.njj.galaxion.endtoendtesting.model.entity.JobEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.JobRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class JobRetrievalService {

    private final JobRepository jobRepository;

    @Transactional
    public List<JobEntity> getInProgress(JobType type) {
        return jobRepository.getInProgress(type);
    }

    @Transactional
    public List<JobEntity> getOldInProgress(JobType type, Integer oldMinutes) {
        return jobRepository.getOldInProgress(type, oldMinutes);
    }

    @Transactional
    public JobEntity get(long id) {
        return jobRepository.findByIdOptional(id)
                            .orElseThrow(() -> new JobNotFoundException(id));

    }

    @Transactional
    public long countInProgress() {
        return jobRepository.countInProgress();
    }
}
