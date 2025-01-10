package fr.plum.e2e.manager.core.domain.model.dto.report;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import java.util.List;
import lombok.Builder;

@Builder
public record Report(FileName fileName, List<ReportTest> tests, List<ReportSuite> suites) {}
