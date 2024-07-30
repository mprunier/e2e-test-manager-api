package fr.njj.galaxion.endtoendtesting.usecases.flowscheduler;

import fr.njj.galaxion.endtoendtesting.domain.request.UpdateConfigurationSchedulerRequest;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSchedulerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateFlowSchedulerUseCase {

    private final ConfigurationSchedulerRepository configurationSchedulerRepository;

    public void execute(
            Long environmentId,
            UpdateConfigurationSchedulerRequest request) {

        var configurationScheduler = configurationSchedulerRepository.findBy(environmentId);
        configurationScheduler.setEnabled(request.getIsEnabled());
        configurationScheduler.setScheduledTime(request.getScheduledTime());
        configurationScheduler.setDaysOfWeek(request.getDaysOfWeek());
    }
}

