package fr.njj.galaxion.endtoendtesting.helper;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.GLOBAL_ENVIRONMENT_ERROR;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileHelper {

  public static void cleanRepo(long environmentId, File projectFolder, Map<String, String> errors) {
    try {
      FileUtils.deleteDirectory(projectFolder);
    } catch (IOException exception) {
      errors.put(GLOBAL_ENVIRONMENT_ERROR, exception.getMessage());
      log.error(
          "Error during remove repository for Environment id [{}] : {}.",
          environmentId,
          exception.getMessage());
    }
  }
}
