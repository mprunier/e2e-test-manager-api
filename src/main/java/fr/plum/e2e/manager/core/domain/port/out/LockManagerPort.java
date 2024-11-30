package fr.plum.e2e.manager.core.domain.port.out;

public interface LockManagerPort {
  /**
   * Acquires a lock for a given resource
   *
   * @param resourceType The type of resource (e.g., "worker_unit")
   * @param resourceId The identifier of the resource
   * @return true if the lock was acquired, false otherwise
   */
  boolean acquireLock(String resourceType, String resourceId);

  /**
   * Releases the lock for a given resource
   *
   * @param resourceType The type of resource
   * @param resourceId The identifier of the resource
   */
  void releaseLock(String resourceType, String resourceId);
}
