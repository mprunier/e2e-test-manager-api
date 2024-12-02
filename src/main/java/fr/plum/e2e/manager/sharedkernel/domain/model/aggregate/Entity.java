package fr.plum.e2e.manager.sharedkernel.domain.model.aggregate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class Entity<ID> {
  protected ID id;
}
