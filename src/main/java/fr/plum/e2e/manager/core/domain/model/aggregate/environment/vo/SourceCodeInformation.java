package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeBranch;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeProjectId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeToken;

public record SourceCodeInformation(
    SourceCodeProjectId sourceCodeProjectId,
    SourceCodeToken sourceCodeToken,
    SourceCodeBranch sourceCodeBranch) {}
