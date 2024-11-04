package fr.plum.e2e.OLD.events.queue;

import fr.plum.e2e.OLD.domain.event.internal.PipelineCompletedEvent;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PipelineCompletedEventQueueManager
    extends AbstractEventQueueManager<PipelineCompletedEvent> {}
