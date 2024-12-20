package fr.plum.e2e.manager.sharedkernel.domain.port.in;

@FunctionalInterface
public interface NoParamQueryUseCase<R> {
  R execute();
}
