package fr.plum.e2e.manager.sharedkernel.domain.port;

import fr.plum.e2e.manager.sharedkernel.domain.model.transaction.TransactionalOperation;

public interface TransactionManagerPort {
  void beginTransaction();

  void commitTransaction();

  void rollbackTransaction();

  void executeInTransaction(TransactionalOperation operation);

  void registerAfterCommit(Runnable operation);
}
