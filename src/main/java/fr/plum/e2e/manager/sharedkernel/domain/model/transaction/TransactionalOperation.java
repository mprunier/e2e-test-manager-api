package fr.plum.e2e.manager.sharedkernel.domain.model.transaction;

@FunctionalInterface
public interface TransactionalOperation {
  void execute() throws Exception;
}
