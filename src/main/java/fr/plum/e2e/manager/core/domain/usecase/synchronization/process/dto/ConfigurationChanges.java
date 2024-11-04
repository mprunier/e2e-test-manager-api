package fr.plum.e2e.manager.core.domain.usecase.synchronization.process.dto;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import java.util.List;

public record ConfigurationChanges(
    List<FileConfiguration> toDelete,
    List<FileConfiguration> toCreate,
    List<FileConfiguration> toUpdate) {}
