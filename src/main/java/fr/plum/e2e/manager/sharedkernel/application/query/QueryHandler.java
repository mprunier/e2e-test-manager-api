package fr.plum.e2e.manager.sharedkernel.application.query;

@FunctionalInterface
public interface QueryHandler<T, R> {
  R execute(T query);
}
