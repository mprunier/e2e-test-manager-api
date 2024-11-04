package fr.plum.e2e.manager.sharedkernel.domain.port.in;

@FunctionalInterface
public interface QueryUseCase<T, R> {
  R execute(T query);
}
