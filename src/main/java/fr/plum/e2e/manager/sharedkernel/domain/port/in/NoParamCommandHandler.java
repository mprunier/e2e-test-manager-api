package fr.plum.e2e.manager.sharedkernel.domain.port.in;

@FunctionalInterface
public interface NoParamCommandHandler {
  void execute();
}