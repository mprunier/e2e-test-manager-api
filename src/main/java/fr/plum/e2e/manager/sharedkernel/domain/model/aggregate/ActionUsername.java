package fr.plum.e2e.manager.sharedkernel.domain.model.aggregate;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record ActionUsername(String value) {
  public ActionUsername {
    Assert.notBlank("action username", value);
  }
}
