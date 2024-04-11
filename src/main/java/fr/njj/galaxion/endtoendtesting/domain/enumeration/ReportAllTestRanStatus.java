package fr.njj.galaxion.endtoendtesting.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportAllTestRanStatus {
    CANCELED("The last execution of all tests was cancelled."),
    SYSTEM_ERROR("The last execution of all tests has failed due to an internal error in the tool. Contact an administrator."),
    NO_REPORT_ERROR("The last execution of all tests has failed because there was no test report.");

    private final String errorMessage;
}
