package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.environment;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class JpaEnvironmentVariableId implements Serializable {
  private UUID environment;
  private String name;
}
