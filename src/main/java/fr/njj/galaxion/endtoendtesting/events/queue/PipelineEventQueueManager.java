package fr.njj.galaxion.endtoendtesting.events.queue;

import fr.njj.galaxion.endtoendtesting.domain.event.PipelineCompletedEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PipelineEventQueueManager {

  private static final long CLEANUP_DELAY_MINUTES = 5;
  private static final long SHUTDOWN_TIMEOUT_SECONDS = 10;
  private static final long PROCESSOR_POLL_TIMEOUT_SECONDS = 5;

  private final Map<Long, BlockingQueue<PipelineCompletedEvent>> environmentQueues =
      new ConcurrentHashMap<>();
  private final Map<Long, ExecutorService> environmentProcessors = new ConcurrentHashMap<>();
  private final Map<Long, Instant> lastActivityTimes = new ConcurrentHashMap<>();
  private volatile boolean isRunning = true;

  @Setter private Consumer<PipelineCompletedEvent> eventProcessor;

  @PostConstruct
  public void init() {
    log.info("Pipeline event queue manager initialized.");
  }

  @PreDestroy
  public void shutdown() {
    log.info("Starting pipeline event queue manager shutdown...");
    isRunning = false;

    // Arrêt gracieux de tous les processeurs
    for (Map.Entry<Long, ExecutorService> entry : environmentProcessors.entrySet()) {
      Long envId = entry.getKey();
      ExecutorService processor = entry.getValue();
      try {
        log.debug("Shutting down processor for environment: {}", envId);
        shutdownProcessor(processor);
      } catch (Exception e) {
        log.warn("Error during shutdown of processor for environment: {}", envId, e);
      }
    }

    environmentProcessors.clear();
    environmentQueues.clear();
    lastActivityTimes.clear();
    log.info("Pipeline event queue manager shutdown completed.");
  }

  public void submitEvent(Long environmentId, PipelineCompletedEvent event) {
    try {
      lastActivityTimes.put(environmentId, Instant.now());

      BlockingQueue<PipelineCompletedEvent> queue =
          environmentQueues.computeIfAbsent(environmentId, this::createQueueForEnvironment);
      queue.put(event);

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Failed to queue pipeline event for environment: {}", environmentId, e);
    }
  }

  private BlockingQueue<PipelineCompletedEvent> createQueueForEnvironment(Long environmentId) {
    log.trace("Creating new queue for environment: {}", environmentId);
    BlockingQueue<PipelineCompletedEvent> queue = new LinkedBlockingQueue<>();
    startProcessorForEnvironment(environmentId, queue);
    return queue;
  }

  private void startProcessorForEnvironment(
      Long environmentId, BlockingQueue<PipelineCompletedEvent> queue) {
    ExecutorService processor =
        Executors.newSingleThreadExecutor(
            r -> {
              Thread t = new Thread(r);
              t.setName("pipeline-processor-" + environmentId);
              return t;
            });

    environmentProcessors.put(environmentId, processor);
    processor.submit(() -> processEventsForEnvironment(environmentId, queue));
  }

  private void processEventsForEnvironment(
      Long environmentId, BlockingQueue<PipelineCompletedEvent> queue) {
    log.trace("Starting pipeline event processor for environment: {}", environmentId);

    while (isRunning) {
      try {
        PipelineCompletedEvent event = queue.poll(PROCESSOR_POLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (event == null) {
          if (shouldCleanup(environmentId)) {
            cleanup(environmentId);
            break;
          }
          continue;
        }

        processEvent(event);

      } catch (InterruptedException e) {
        // Gestion plus propre de l'interruption pendant le shutdown
        if (!isRunning) {
          log.debug("Processor shutdown requested for environment: {}", environmentId);
        } else {
          log.error("Pipeline event processing interrupted for environment: {}", environmentId, e);
        }
        Thread.currentThread().interrupt();
        break;
      } catch (Exception e) {
        log.error("Error processing pipeline event for environment: {}", environmentId, e);
      }
    }

    log.debug("Event processor terminated for environment: {}", environmentId);
  }

  private void processEvent(PipelineCompletedEvent event) {
    if (eventProcessor != null) {
      eventProcessor.accept(event);
    }
  }

  private boolean shouldCleanup(Long environmentId) {
    Instant lastActivity = lastActivityTimes.get(environmentId);
    if (lastActivity == null) {
      return true;
    }

    long minutesSinceLastActivity = Duration.between(lastActivity, Instant.now()).toMinutes();
    boolean shouldClean = minutesSinceLastActivity >= CLEANUP_DELAY_MINUTES;

    if (shouldClean) {
      log.trace(
          "Environment {} has been inactive for {} minutes, marking for cleanup.",
          environmentId,
          minutesSinceLastActivity);
    }

    return shouldClean;
  }

  private void cleanup(Long environmentId) {
    log.trace("Cleaning up resources for inactive environment: {}.", environmentId);

    ExecutorService processor = environmentProcessors.remove(environmentId);
    if (processor != null) {
      shutdownProcessor(processor);
    }

    environmentQueues.remove(environmentId);
    lastActivityTimes.remove(environmentId);

    log.info("Cleanup completed for environment: {}.", environmentId);
  }

  private void shutdownProcessor(ExecutorService processor) {
    processor.shutdown();
    try {
      if (!processor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS / 2, TimeUnit.SECONDS)) {
        log.warn("Processor did not terminate in time, forcing shutdown...");
        processor.shutdownNow();
        if (!processor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS / 2, TimeUnit.SECONDS)) {
          log.error("Processor did not terminate after forced shutdown");
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      processor.shutdownNow();
    }
  }
}
