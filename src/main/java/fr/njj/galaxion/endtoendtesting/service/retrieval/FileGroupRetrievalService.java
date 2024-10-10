package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.model.entity.FileGroupEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.FileGroupRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class FileGroupRetrievalService {

  private final FileGroupRepository fileGroupRepository;

  @Transactional
  public Optional<FileGroupEntity> getOptionalFileGroup(long environmentId, String file) {
    return fileGroupRepository.findByFileAndEnv(file, environmentId);
  }

  @Transactional
  public Map<String, List<String>> getAllFilesByGroup(long environmentId) {
    return fileGroupRepository.findAllByEnv(environmentId).stream()
        .collect(
            Collectors.groupingBy(
                FileGroupEntity::getGroup,
                Collectors.mapping(FileGroupEntity::getFile, Collectors.toList())));
  }
}
