package fr.plum.e2e.manager.sharedkernel.domain.model.aggregate;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class Entity<ID> {
  protected ID id;

  protected Entity(ID id) {
    Assert.notNull("entity id", id);
    this.id = id;
  }
}
