package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.ArtifactDataInternal;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.MochaReportInternal;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.exception.ZipDecompressionErrorException;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.zip.ZipInputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.ConfigProvider;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CypressArtifactsExtractor {

  private static final String DEFAULT_SCREENSHOTS_PATH = "cypress/screenshots/";
  private static final String DEFAULT_VIDEOS_PATH = "cypress/videos/";
  private static final String DEFAULT_RESULTS_PATH = "cypress/results/results.json";
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  private static final String SCREENSHOTS_PATH =
      ConfigProvider.getConfig()
          .getOptionalValue("artifacts.screenshots.path", String.class)
          .orElse(DEFAULT_SCREENSHOTS_PATH);

  private static final String VIDEOS_PATH =
      ConfigProvider.getConfig()
          .getOptionalValue("artifacts.videos.path", String.class)
          .orElse(DEFAULT_VIDEOS_PATH);

  private static final String RESULTS_PATH =
      ConfigProvider.getConfig()
          .getOptionalValue("artifacts.results.path", String.class)
          .orElse(DEFAULT_RESULTS_PATH);

  public static ArtifactDataInternal extractArtifact(Object zipArtifacts) {
    var zipArtifactsResponse = (Response) zipArtifacts;
    var artifactDataInternal = new ArtifactDataInternal();
    var screenshots = new HashMap<String, byte[]>();
    var videos = new HashMap<String, byte[]>();

    var zipData = zipArtifactsResponse.readEntity(byte[].class);

    try (ByteArrayInputStream bais = new ByteArrayInputStream(zipData);
        var zis = new ZipInputStream(bais)) {

      var entry = zis.getNextEntry();
      while (entry != null) {
        var filename = entry.getName();
        if (!entry.isDirectory()) {
          var baos = new ByteArrayOutputStream();
          byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
          int count;

          while ((count = zis.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
          }

          byte[] byteArray = baos.toByteArray();

          if (filename.contains(SCREENSHOTS_PATH)) {
            screenshots.put(normalizeFileName(filename, SCREENSHOTS_PATH, ".png"), byteArray);
          } else if (filename.contains(VIDEOS_PATH)) {
            videos.put(normalizeFileName(filename, VIDEOS_PATH, ".mp4"), byteArray);
          } else if (filename.contains(RESULTS_PATH)) {
            var resultsJson = new String(byteArray, StandardCharsets.UTF_8);
            var objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            var mochaReportInternal =
                objectMapper.readValue(resultsJson, MochaReportInternal.class);
            artifactDataInternal.setReport(mochaReportInternal);
          }
        }

        zis.closeEntry();
        entry = zis.getNextEntry();
      }

      artifactDataInternal.setScreenshots(screenshots);
      artifactDataInternal.setVideos(videos);

      return artifactDataInternal;
    } catch (IOException e) {
      log.error("Error during artifact extraction", e);
      throw new ZipDecompressionErrorException();
    }
  }

  /**
   * Normalizes a file path by removing the base path and file extension. Also handles potential
   * platform-specific path separators.
   *
   * @param filename The complete file path
   * @param pathToRemove The base path to remove
   * @param extensionToRemove The file extension to remove (including the dot)
   * @return The normalized filename
   */
  private static String normalizeFileName(
      String filename, String pathToRemove, String extensionToRemove) {
    var normalizedPath = filename.replace('\\', '/');
    var withoutBasePath = normalizedPath.replace(pathToRemove, "");
    var withoutExtension = withoutBasePath;
    if (withoutBasePath.toLowerCase().endsWith(extensionToRemove.toLowerCase())) {
      withoutExtension =
          withoutBasePath.substring(0, withoutBasePath.length() - extensionToRemove.length());
    }
    return withoutExtension.trim().replace("^/+|/+$", "");
  }
}
