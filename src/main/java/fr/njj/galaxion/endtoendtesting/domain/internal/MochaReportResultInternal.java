package fr.njj.galaxion.endtoendtesting.domain.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MochaReportResultInternal {

    private String file;
    private List<MochaReportTestInternal> tests;
    private List<MochaReportSuiteInternal> suites;
    private Integer duration;
}
