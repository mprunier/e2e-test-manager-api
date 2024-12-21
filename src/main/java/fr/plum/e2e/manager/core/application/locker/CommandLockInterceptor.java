package fr.plum.e2e.manager.core.application.locker;

import fr.plum.e2e.manager.core.domain.port.LockManagerPort;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Interceptor
@CommandLock
@Priority(Interceptor.Priority.APPLICATION)
@RequiredArgsConstructor
public class CommandLockInterceptor {

  private final LockManagerPort lockManager;

  @AroundInvoke
  public Object lock(InvocationContext context) throws Exception {
    CommandLock annotation = context.getMethod().getAnnotation(CommandLock.class);
    if (annotation == null) {
      return context.proceed();
    }

    Object[] parameters = context.getParameters();
    if (parameters.length == 0 || !(parameters[0] instanceof Record command)) {
      log.warn("CommandLock can only be used with Record commands");
      return context.proceed();
    }

    String useCaseName = context.getTarget().getClass().getSimpleName();
    String commandName = command.getClass().getSimpleName();
    String resourceType = useCaseName + "." + commandName;
    String resourceId = command.toString();

    boolean locked = false;
    try {
      locked = lockManager.acquireLock(resourceType, resourceId);
      if (!locked) {
        log.debug(
            "Command already locked: {} for resource {}.{}", command, resourceType, resourceId);
        return null;
      }
      return context.proceed();
    } finally {
      if (locked) {
        lockManager.releaseLock(resourceType, resourceId);
      }
    }
  }
}
