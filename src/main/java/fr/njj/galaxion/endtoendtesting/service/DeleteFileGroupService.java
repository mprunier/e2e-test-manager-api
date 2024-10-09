package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.model.repository.FileGroupRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DeleteFileGroupService {

  private final FileGroupRepository fileGroupRepository;

  @Transactional
  public void deleteByEnvAndFile(long environmentId, String file) {
    fileGroupRepository.deleteByFileAndEnv(file, environmentId);
  }
}
