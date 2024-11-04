package fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestCode;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestErrorMessage;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestErrorScreenshotName;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestErrorStackTrace;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestErrorUrl;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestIsFailed;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestIsSkipped;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestIsSuccess;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestReference;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.TestDuration;
import lombok.Builder;

@Builder
public record ReportResultTest(
    TestTitle title,
    TestDuration duration,
    ResultTestIsSuccess isSuccess,
    ResultTestIsFailed isFailed,
    ResultTestIsSkipped isSkipped,
    ResultTestCode code,
    ResultTestErrorMessage errorMessage,
    ResultTestErrorStackTrace errorStacktrace,
    ResultTestReference reference,
    ResultTestErrorUrl urlError,
    ResultTestErrorScreenshotName screenshotError) {}
