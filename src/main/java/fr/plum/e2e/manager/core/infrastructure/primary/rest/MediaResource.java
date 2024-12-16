package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import fr.plum.e2e.manager.core.application.TestResultFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.core.domain.model.query.DownloadScreenshotQuery;
import fr.plum.e2e.manager.core.domain.model.query.DownloadVideoQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "MediaApi")
@Slf4j
@Path("/medias")
@RequiredArgsConstructor
public class MediaResource {

  private final TestResultFacade testResultFacade;

  @Operation(operationId = "downloadVideo")
  @GET
  @Path("/videos/{id}")
  @Produces("video/mp4")
  public byte[] downloadVideo(@PathParam("id") UUID id) {
    return testResultFacade.downloadVideo(new DownloadVideoQuery(new TestResultVideoId(id)));
  }

  @Operation(operationId = "downloadScreenshot")
  @GET
  @Path("/screenshots/{id}")
  @Produces("image/png")
  public byte[] downloadScreenshot(@PathParam("id") UUID id) {
    return testResultFacade.downloadScreenshot(
        new DownloadScreenshotQuery(new TestResultScreenshotId(id)));
  }
}
