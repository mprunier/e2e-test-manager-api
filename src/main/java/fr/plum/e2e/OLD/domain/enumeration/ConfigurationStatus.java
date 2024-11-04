package fr.plum.e2e.OLD.domain.enumeration;

public enum ConfigurationStatus {
  NEW,

  IN_PROGRESS,

  SUCCESS,

  SKIPPED,
  PARTIAL_SKIPPED, // Only one test of a configuration can be skipped

  FAILED,
  SYSTEM_ERROR,
  NO_CORRESPONDING_TEST,
  NO_REPORT_ERROR,
  UNKNOWN,

  CANCELED
}
