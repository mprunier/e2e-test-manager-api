package fr.plum.e2e.OLD.service.retrieval;

import fr.plum.e2e.OLD.model.entity.FileGroupEntity;
import fr.plum.e2e.OLD.model.repository.FileGroupRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

  @CacheResult(cacheName = "allFilesByGroupMap")
  public Map<String, List<String>> getAllFilesByGroup(long environmentId) {
    return fileGroupRepository.findAllByEnv(environmentId).stream()
        .collect(
            Collectors.groupingBy(
                FileGroupEntity::getGroup,
                Collectors.mapping(FileGroupEntity::getFile, Collectors.toList())));
  }

  @CacheResult(cacheName = "fileByGroupMap")
  public Map<String, String> getFileByGroupMap(long environmentId) {
    return fileGroupRepository.findAllByEnv(environmentId).stream()
        .collect(
            Collectors.toMap(FileGroupEntity::getFile, FileGroupEntity::getGroup, (a, b) -> a));
  }

  public Set<String> getAllGroups(long environmentId) {
    return fileGroupRepository.findAllByEnv(environmentId).stream()
        .map(FileGroupEntity::getGroup)
        .collect(Collectors.toSet());
  }

  public Set<String> getAllFiles(long environmentId, String group) {
    return fileGroupRepository.findAllFilesByGroup(environmentId, group);
  }
}
