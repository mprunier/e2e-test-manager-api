package fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import java.util.List;
import lombok.Builder;

@Builder
public record ReportResult(
    FileName fileName, List<ReportResultTest> tests, List<ReportResultSuite> suites) {}
