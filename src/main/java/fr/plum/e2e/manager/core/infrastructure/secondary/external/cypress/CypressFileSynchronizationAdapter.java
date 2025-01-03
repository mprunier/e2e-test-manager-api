package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress;

import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.constant.CypressConstant.END_TEST_JS_PATH;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.constant.CypressConstant.END_TEST_TS_PATH;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.constant.CypressConstant.START_PATH;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.exception.SynchronizationCommonException;
import fr.plum.e2e.manager.core.domain.port.FileSynchronizationPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.mapper.CypressFileConfigurationMapper;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CypressFileSynchronizationAdapter implements FileSynchronizationPort {

  @Override
  public Map<SynchronizationFileName, SynchronizationFileContent> listFiles(
      SourceCodeProject sourceCodeProject) {

    Path cypressPath = Paths.get(sourceCodeProject.project().getAbsolutePath(), START_PATH);

    if (!Files.exists(cypressPath) || !Files.isDirectory(cypressPath)) {
      return Map.of();
    }

    try (Stream<Path> paths = Files.walk(cypressPath)) {
      return paths
          .filter(Files::isRegularFile)
          .filter(
              path ->
                  path.toString().endsWith(END_TEST_JS_PATH)
                      || path.toString().endsWith(END_TEST_TS_PATH))
          .collect(
              Collectors.toMap(
                  path -> new SynchronizationFileName(cypressPath.relativize(path).toString()),
                  path -> {
                    try {
                      return new SynchronizationFileContent(Files.readString(path));
                    } catch (IOException e) {
                      throw new SynchronizationCommonException(
                          "Read File Error : " + e.getMessage());
                    }
                  }));
    } catch (IOException exception) {
      throw new SynchronizationCommonException("List Files Error : " + exception.getMessage());
    }
  }

  @Override
  public FileConfiguration buildFileConfiguration(
      SynchronizationFileName fileName, SynchronizationFileContent content) {
    return CypressFileConfigurationMapper.build(fileName.value(), content.value());
  }
}
