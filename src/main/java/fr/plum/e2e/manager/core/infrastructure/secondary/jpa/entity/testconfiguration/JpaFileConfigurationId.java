package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testconfiguration;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JpaFileConfigurationId implements Serializable {
  private String fileName;
  private UUID environmentId;
}
