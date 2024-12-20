package fr.plum.e2e.manager.core.application.command.synchronization.process;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.END_TEST_TS_PATH;
import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.ERROR_ES6_TRANSPILATION;
import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.ERROR_TYPESCRIPT_TRANSPILATION;

import fr.plum.e2e.manager.core.application.command.synchronization.process.factory.SynchronizationErrorFactory;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.port.out.FileSynchronizationPort;
import fr.plum.e2e.manager.core.domain.port.out.JavascriptConverterPort;
import fr.plum.e2e.manager.core.domain.port.out.SourceCodePort;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
@RequiredArgsConstructor
public class SourceCodeSynchronizer {

  private final ClockPort clockPort;
  private final SourceCodePort sourceCodePort;
  private final FileSynchronizationPort fileSynchronizationPort;
  private final JavascriptConverterPort javascriptConverterPort;

  public SourceCodeProject cloneRepository(Environment environment) {
    return sourceCodePort.cloneRepository(environment.getSourceCodeInformation());
  }

  public Map<SynchronizationFileName, SynchronizationFileContent> processFiles(
      SourceCodeProject sourceCodeProject, List<SynchronizationError> errors) {
    var fileContents = fileSynchronizationPort.listFiles(sourceCodeProject);
    var processedFiles = new HashMap<SynchronizationFileName, SynchronizationFileContent>();

    fileContents.forEach(
        (fileName, content) -> processFile(fileName, content, processedFiles, errors));

    return processedFiles;
  }

  private void processFile(
      SynchronizationFileName fileName,
      SynchronizationFileContent content,
      Map<SynchronizationFileName, SynchronizationFileContent> processedFiles,
      List<SynchronizationError> errors) {
    try {
      convertAndTranspileContent(fileName, content, errors)
          .ifPresent(c -> processedFiles.put(fileName, c));
    } catch (Exception e) {
      errors.add(
          SynchronizationErrorFactory.createFileError(fileName, e.getMessage(), clockPort.now()));
    }
  }

  private Optional<SynchronizationFileContent> convertAndTranspileContent(
      SynchronizationFileName fileName,
      SynchronizationFileContent content,
      List<SynchronizationError> errors) {

    if (fileName.value().endsWith(END_TEST_TS_PATH)) {
      try {
        content = javascriptConverterPort.convertTsToJs(fileName, content);
      } catch (Exception e) {
        errors.add(
            SynchronizationErrorFactory.createFileError(
                fileName, ERROR_TYPESCRIPT_TRANSPILATION, clockPort.now()));
        return Optional.empty();
      }
    }

    try {
      return Optional.of(javascriptConverterPort.transpileJs(fileName, content));
    } catch (Exception e) {
      errors.add(
          SynchronizationErrorFactory.createFileError(
              fileName, ERROR_ES6_TRANSPILATION, clockPort.now()));
      return Optional.empty();
    }
  }

  public void cleanup(
      EnvironmentId envId, SourceCodeProject project, List<SynchronizationError> errors) {
    if (project != null) {
      try {
        FileUtils.deleteDirectory(project.project());
      } catch (IOException e) {
        log.error("Error during synchronization for Environment id [{}].", envId.value(), e);
        errors.add(SynchronizationErrorFactory.createGlobalError(e.getMessage(), clockPort.now()));
      }
    }
  }
}
