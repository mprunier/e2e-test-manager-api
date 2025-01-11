package fr.plum.e2e.manager.core.domain.model.query;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import lombok.Builder;

@Builder
public record DownloadVideoQuery(TestResultVideoId testResultVideoId) {}
