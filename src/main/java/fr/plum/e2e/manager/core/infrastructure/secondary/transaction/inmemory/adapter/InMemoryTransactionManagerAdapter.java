package fr.plum.e2e.manager.core.infrastructure.secondary.transaction.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.model.exception.TransactionException;
import fr.plum.e2e.manager.sharedkernel.domain.model.transaction.TransactionalOperation;
import fr.plum.e2e.manager.sharedkernel.domain.port.TransactionManagerPort;

public class InMemoryTransactionManagerAdapter implements TransactionManagerPort {
  @Override
  public void beginTransaction() {
    // No-op for in-memory implementation
  }

  @Override
  public void commitTransaction() {
    // No-op for in-memory implementation
  }

  @Override
  public void rollbackTransaction() {
    // No-op for in-memory implementation
  }

  @Override
  public void registerAfterCommit(Runnable operation) {
    // Execute immediately for in-memory implementation
    operation.run();
  }

  @Override
  public void executeInTransaction(TransactionalOperation operation) {
    try {
      beginTransaction();
      operation.execute();
      commitTransaction();
    } catch (Exception e) {
      rollbackTransaction();
      throw new TransactionException(e);
    }
  }
}
