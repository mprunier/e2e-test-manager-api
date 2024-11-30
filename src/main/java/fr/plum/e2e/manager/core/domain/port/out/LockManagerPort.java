package fr.plum.e2e.manager.core.domain.port.out;

public interface LockManagerPort {
  boolean acquireLock(String resourceType, String resourceId);

  void releaseLock(String resourceType, String resourceId);

  void releaseAllLocks();
}
