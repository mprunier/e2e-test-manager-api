package fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record Report(
    Map<String, byte[]> videos, Map<String, byte[]> screenshots, List<ReportResult> results) {}
