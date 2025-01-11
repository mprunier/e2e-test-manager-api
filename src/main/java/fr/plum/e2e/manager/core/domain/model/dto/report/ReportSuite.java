package fr.plum.e2e.manager.core.domain.model.dto.report;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import java.util.List;
import lombok.Builder;

@Builder
public record ReportSuite(SuiteTitle title, List<ReportTest> tests) {}
