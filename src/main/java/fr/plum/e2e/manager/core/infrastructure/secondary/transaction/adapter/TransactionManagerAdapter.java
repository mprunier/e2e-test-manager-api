package fr.plum.e2e.manager.core.infrastructure.secondary.transaction.adapter;

import fr.plum.e2e.manager.core.domain.model.exception.TransactionException;
import fr.plum.e2e.manager.sharedkernel.domain.model.transaction.TransactionalOperation;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;

@ApplicationScoped
public class TransactionManagerAdapter implements TransactionManagerPort {

  @Inject UserTransaction userTransaction;

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
