package fr.njj.galaxion.endtoendtesting.events.queue;

import fr.njj.galaxion.endtoendtesting.domain.event.internal.PipelineCompletedEvent;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PipelineCompletedEventQueueManager
    extends AbstractEventQueueManager<PipelineCompletedEvent> {}
