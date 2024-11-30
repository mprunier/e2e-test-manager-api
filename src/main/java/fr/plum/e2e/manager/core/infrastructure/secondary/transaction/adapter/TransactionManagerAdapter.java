package fr.plum.e2e.manager.core.infrastructure.secondary.transaction.adapter;

import fr.plum.e2e.manager.core.domain.model.exception.TransactionException;
import fr.plum.e2e.manager.sharedkernel.domain.model.transaction.TransactionalOperation;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.UserTransaction;

@ApplicationScoped
public class TransactionManagerAdapter implements TransactionManagerPort {

  @Inject UserTransaction userTransaction;

  @Inject TransactionSynchronizationRegistry syncRegistry;

  @Override
  public void beginTransaction() {
    try {
      userTransaction.begin();
    } catch (Exception e) {
      throw new TransactionException(e);
    }
  }

  @Override
  public void commitTransaction() {
    try {
      userTransaction.commit();
    } catch (Exception e) {
      throw new TransactionException(e);
    }
  }

  @Override
  public void rollbackTransaction() {
    try {
      userTransaction.rollback();
    } catch (Exception e) {
      throw new TransactionException(e);
    }
  }

  @Override
  public void registerAfterCommit(Runnable operation) {
    try {
      syncRegistry.registerInterposedSynchronization(
          new Synchronization() {
            @Override
            public void beforeCompletion() {}

            @Override
            public void afterCompletion(int status) {
              if (status == Status.STATUS_COMMITTED) {
                operation.run();
              }
            }
          });
    } catch (Exception e) {
      throw new TransactionException(e);
    }
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
