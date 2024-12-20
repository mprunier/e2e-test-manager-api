package fr.plum.e2e.manager.sharedkernel.domain.port.in;

@FunctionalInterface
public interface QueryHandler<T, R> {
  R execute(T query);
}
