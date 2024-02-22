package fr.njj.galaxion.endtoendtesting.domain.enumeration;

public enum ConfigurationStatus {
    NEW,

    IN_PROGRESS,

    SUCCESS,

    SKIPPED,
    PARTIAL_SKIPPED, // Only one test of a suite can be skipped

    FAILED,
    SYSTEM_ERROR,
    NO_CORRESPONDING_TEST,
    NO_REPORT_ERROR,
    UNKNOWN,

    CANCELED
}
