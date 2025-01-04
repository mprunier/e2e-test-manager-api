package fr.plum.e2e.manager.sharedkernel.domain.model.aggregate;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import lombok.Getter;

@Getter
public abstract class Entity<ID> {

  protected ID id;

  protected Entity(ID id) {
    Assert.notNull("Entity ID must not be null for class " + getClass().getSimpleName(), id);
    this.id = id;
  }
}
