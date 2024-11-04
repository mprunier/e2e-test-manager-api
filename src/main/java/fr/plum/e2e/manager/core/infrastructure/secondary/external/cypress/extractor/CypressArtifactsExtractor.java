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

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CypressArtifactsExtractor {

  public static final int DEFAULT_BUFFER_SIZE = 1024;

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

          if (filename.contains("screenshots")) {
            screenshots.put(filename, byteArray);
          } else if (filename.contains("videos")) {
            videos.put(filename.replace("cypress/videos/", "").replace(".mp4", ""), byteArray);
          } else if (filename.contains("cypress/results/results.json")) {
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
}
