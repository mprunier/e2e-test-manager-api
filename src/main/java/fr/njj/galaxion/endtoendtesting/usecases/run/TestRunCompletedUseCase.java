package fr.njj.galaxion.endtoendtesting.usecases.run;

import fr.njj.galaxion.endtoendtesting.domain.event.TestRunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestRunCompletedUseCase {

    private final Event<TestRunCompletedEvent> testRunCompletedEvent;

    @Monitored(logExit = false)
    @Transactional
    public void execute(
            long environmentId) {

        testRunCompletedEvent.fire(TestRunCompletedEvent.builder().environmentId(environmentId).build());
    }

}

