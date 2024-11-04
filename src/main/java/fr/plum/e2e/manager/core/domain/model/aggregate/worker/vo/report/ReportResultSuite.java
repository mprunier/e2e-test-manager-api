package fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.SuiteDuration;
import java.util.List;
import lombok.Builder;

@Builder
public record ReportResultSuite(
    SuiteTitle title,
    List<ReportResultTest> tests,
    List<ReportResultSuite> suites,
    SuiteDuration duration) {}
