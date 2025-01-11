package fr.plum.e2e.manager.sharedkernel.application.query;

@FunctionalInterface
public interface NoParamQueryHandler<R> {
  R execute();
}
