package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.respository;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

import fr.plum.e2e.manager.core.domain.port.LockManagerPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.locker.JpaLockManagerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class JpaLockManagerAdapter implements LockManagerPort {

  @Transactional(REQUIRES_NEW)
  @Override
  public boolean acquireLock(String resourceType, String resourceId) {
    var existingLock =
        JpaLockManagerEntity.find("resourceType = ?1 and resourceId = ?2", resourceType, resourceId)
            .withLock(LockModeType.PESSIMISTIC_WRITE)
            .firstResult();

    if (existingLock != null) {
      return false;
    }

    var lock =
        JpaLockManagerEntity.builder().resourceType(resourceType).resourceId(resourceId).build();

    lock.persist();
    return true;
  }

  @Transactional(REQUIRES_NEW)
  @Override
  public void releaseLock(String resourceType, String resourceId) {
    JpaLockManagerEntity.delete("resourceType = ?1 and resourceId = ?2", resourceType, resourceId);
  }

  @Override
  public void releaseAllLocks() {
    JpaLockManagerEntity.deleteAll();
  }
}
