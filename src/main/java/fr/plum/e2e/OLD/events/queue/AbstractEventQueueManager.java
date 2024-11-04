package fr.plum.e2e.OLD.events.queue;

import fr.plum.e2e.OLD.domain.event.AbstractEvent;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractEventQueueManager<T extends AbstractEvent> {

  private final Map<Long, BlockingQueue<T>> queues = new ConcurrentHashMap<>();
  @Setter private Consumer<T> eventProcessor;

  public void submitEvent(T event) {
    try {
      BlockingQueue<T> queue =
          queues.computeIfAbsent(
              event.getEnvironmentId(),
              k -> {
                var newQueue = new LinkedBlockingQueue<T>();
                startProcessor(k, newQueue);
                return newQueue;
              });

      queue.put(event);
    } catch (InterruptedException e) {
      log.error("Failed to queue consumer for environment: {}", event.getEnvironmentId(), e);
    }
  }

  private void startProcessor(Long environmentId, BlockingQueue<T> queue) {
    Thread processorThread =
        new Thread(
            () -> {
              while (true) {
                try {
                  T event = queue.take();
                  if (eventProcessor != null) {
                    eventProcessor.accept(event);
                  }
                } catch (InterruptedException e) {
                  break;
                }
              }
            },
            "consumer-processor-" + environmentId);

    processorThread.start();
  }
}
