package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.converter;

import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.worker.WorkerUnitFilterDto;
import jakarta.persistence.Converter;

@Converter
public class WorkerUnitFilterConverter extends JsonConverter<WorkerUnitFilterDto> {
  public WorkerUnitFilterConverter() {
    super(WorkerUnitFilterDto.class);
  }
}
