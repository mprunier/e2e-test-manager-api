package fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo;

import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record SynchronizationError(
    SynchronizationFileName file, SynchronizationErrorValue error, ZonedDateTime at) {}
