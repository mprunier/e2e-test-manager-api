package fr.plum.e2e.manager.core.application.command.worker.dto;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record WorkerFiles(List<FileName> files) {
  public WorkerFiles() {
    this(new ArrayList<>());
  }

  public void add(FileName file) {
    files.add(file);
  }

  public void addAll(Collection<FileName> filesToAdd) {
    files.addAll(filesToAdd);
  }

  public boolean isEmpty() {
    return files.isEmpty();
  }

  public int size() {
    return files.size();
  }

  public WorkerUnitFilter toWorkerFilter() {
    return WorkerUnitFilter.builder().fileNames(files).build();
  }
}
