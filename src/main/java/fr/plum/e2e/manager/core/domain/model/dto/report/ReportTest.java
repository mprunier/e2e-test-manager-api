package fr.plum.e2e.manager.core.domain.model.dto.report;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultScreenshot;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultCode;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultDuration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultErrorMessage;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultErrorStackTrace;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultReference;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultUrlError;
import java.util.List;
import lombok.Builder;

@Builder
public record ReportTest(
    TestTitle title,
    TestResultDuration duration,
    TestResultStatus status,
    TestResultCode code,
    TestResultErrorMessage errorMessage,
    TestResultErrorStackTrace errorStackTrace,
    TestResultReference reference,
    TestResultUrlError urlError,
    List<TestResultScreenshot> screenshots,
    TestResultVideo video) {}
