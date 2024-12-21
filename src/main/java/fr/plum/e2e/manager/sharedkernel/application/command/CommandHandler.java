package fr.plum.e2e.manager.sharedkernel.application.command;

@FunctionalInterface
public interface CommandHandler<T> {
  void execute(T command);
}
