package fr.njj.galaxion.endtoendtesting.helper;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationSynchronizationException;
import fr.njj.galaxion.endtoendtesting.domain.exception.ZipDecompressionErrorException;
import fr.njj.galaxion.endtoendtesting.domain.internal.ArtifactDataInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportInternal;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipInputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GitHelper {

  public static final int DEFAULT_BUFFER_SIZE = 1024;

  public static void extractArtifact(
      ArtifactDataInternal artifactDataInternal, Response zipArtifacts) {
    var screenshots = new HashMap<String, byte[]>();
    var videos = new HashMap<String, byte[]>();

    var zipData = zipArtifacts.readEntity(byte[].class);

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
            videos.put(filename, byteArray);
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
    } catch (IOException e) {
      log.error("Error during artifact extraction", e);
      throw new ZipDecompressionErrorException();
    }
  }

  public static Set<String> getChangedFilesAfterDate(
      File repoDir, ZonedDateTime thresholdDateTime) {
    var changedFiles = new HashSet<String>();
    try (Git git = Git.open(repoDir)) {
      var commits = git.log().call();
      for (var commit : commits) {
        var commitDateTime =
            commit.getCommitterIdent().getWhen().toInstant().atZone(ZoneId.systemDefault());

        if (commitDateTime.isAfter(
            thresholdDateTime.minusMinutes(
                1))) { // Je mets -1 min au cas où y'a une synchro quasi en meme temps qu'un pull.
          var parents = commit.getParents();
          if (parents.length == 0) {
            // C'est le premier commit; tous les fichiers sont considérés comme nouveaux
            firstCommit(commit, git, changedFiles);
          } else {
            // Comparez le commit actuel à son parent pour obtenir la liste des fichiers modifiés
            newCommit(commit, git, parents, changedFiles);
          }
        }
      }
    } catch (Exception exception) {
      throw new ConfigurationSynchronizationException(
          "Modified File Recovery Error : " + exception.getMessage());
    }

    return changedFiles;
  }

  private static void newCommit(
      RevCommit commit, Git git, RevCommit[] parents, HashSet<String> changedFiles)
      throws IOException {
    try (var treeWalk = new TreeWalk(git.getRepository())) {
      treeWalk.addTree(commit.getTree());
      treeWalk.addTree(
          parents[0]
              .getTree()); // Prendre le premier parent; cela ne gérera pas les cas de merge commits
      treeWalk.setRecursive(true);

      while (treeWalk.next()) {
        if (!treeWalk.getObjectId(0).equals(treeWalk.getObjectId(1))) {
          String path = treeWalk.getPathString();
          if (path.startsWith(START_PATH)) {
            changedFiles.add(path);
          }
        }
      }
    }
  }

  private static void firstCommit(RevCommit commit, Git git, HashSet<String> changedFiles)
      throws IOException {
    try (var treeWalk = new TreeWalk(git.getRepository())) {
      treeWalk.addTree(commit.getTree());
      treeWalk.setRecursive(true);
      while (treeWalk.next()) {
        String path = treeWalk.getPathString();
        if (path.startsWith(START_PATH)) {
          changedFiles.add(path);
        }
      }
    }
  }
}
