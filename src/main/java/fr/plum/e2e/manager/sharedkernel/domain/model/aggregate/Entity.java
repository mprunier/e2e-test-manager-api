package fr.plum.e2e.manager.sharedkernel.domain.model.aggregate;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import lombok.Getter;

@Getter
public abstract class Entity<ID> {

  protected ID id;

  protected Entity(ID id) {
    Assert.notNull(String.format("Entity ID on class [%s]", getClass().getSimpleName()), id);
    this.id = id;
  }
}
