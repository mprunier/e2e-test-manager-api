package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.locker;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class JpaLockManagerId implements Serializable {
  private String resourceType;
  private String resourceId;
}
