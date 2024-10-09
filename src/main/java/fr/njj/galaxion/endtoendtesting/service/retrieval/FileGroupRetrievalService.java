package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.model.entity.FileGroupEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.FileGroupRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class FileGroupRetrievalService {

  private final FileGroupRepository fileGroupRepository;

  @Transactional
  public Optional<FileGroupEntity> getByFileAndEnv(long environmentId, String file) {
    return fileGroupRepository.findByFileAndEnv(file, environmentId);
  }
}
