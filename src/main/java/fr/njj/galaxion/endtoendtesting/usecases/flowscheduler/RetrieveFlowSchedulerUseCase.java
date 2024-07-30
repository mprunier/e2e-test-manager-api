package fr.njj.galaxion.endtoendtesting.usecases.flowscheduler;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSchedulerResponse;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSchedulerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveFlowSchedulerUseCase {

    private final ConfigurationSchedulerRepository configurationSchedulerRepository;

    public ConfigurationSchedulerResponse execute(
            Long environmentId) {

        var configurationScheduler = configurationSchedulerRepository.findBy(environmentId);
        return ConfigurationSchedulerResponse
                .builder()
                .isEnabled(configurationScheduler.isEnabled())
                .scheduledTime(configurationScheduler.getScheduledTime())
                .daysOfWeek(configurationScheduler.getDaysOfWeek())
                .build();
    }
}

