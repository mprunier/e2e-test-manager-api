package fr.plum.e2e.manager.core.domain.model.projection;

import java.util.List;

public record SearchCriteriaProjection(
    List<CriteriaOptionProjection> suites,
    List<CriteriaOptionProjection> tests,
    List<CriteriaOptionProjection> files,
    List<CriteriaOptionProjection> tags) {}
