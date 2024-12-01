package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

public enum ConfigurationStatus {
  NEW,

  IN_PROGRESS,

  SUCCESS,

  SKIPPED,
  PARTIAL_SKIPPED, // Only one testFilter of a configuration can be skipped

  FAILED,
  SYSTEM_ERROR,
  NO_CORRESPONDING_TEST,
  NO_REPORT_ERROR,
  UNKNOWN,

  CANCELED;

  public static ConfigurationStatus defaultStatus() {
    return NEW;
  }
}