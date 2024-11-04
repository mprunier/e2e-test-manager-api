package fr.plum.e2e.manager.core.domain.model.view;

import java.util.List;

public record SearchCriteriaView(
    List<CriteriaOptionView> suites,
    List<CriteriaOptionView> tests,
    List<CriteriaOptionView> files,
    List<CriteriaOptionView> tags) {}
