package fr.plum.e2e.manager.core.domain.model.aggregate.shared;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class Entity<ID> {
  protected ID id;
}
