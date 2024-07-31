package fr.njj.galaxion.endtoendtesting.usecases.flowscheduler;

import fr.njj.galaxion.endtoendtesting.domain.request.UpdateConfigurationSchedulerRequest;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSchedulerRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateFlowSchedulerUseCase {

    private final ConfigurationSchedulerRetrievalService configurationSchedulerRetrievalService;

    public void execute(
            Long environmentId,
            UpdateConfigurationSchedulerRequest request) {

        var configurationScheduler = configurationSchedulerRetrievalService.getByEnvironment(environmentId);
        configurationScheduler.setEnabled(request.getIsEnabled());
        configurationScheduler.setScheduledTime(request.getScheduledTime());
        configurationScheduler.setDaysOfWeek(request.getDaysOfWeek());
    }
}

