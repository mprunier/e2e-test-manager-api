package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.service.retrieval.TestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.TestScreenshotRetrievalService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/medias")
@RequiredArgsConstructor
public class DownloadController {

  private final TestRetrievalService testRetrievalService;
  private final TestScreenshotRetrievalService testScreenshotRetrievalService;

  @GET
  @Path("/videos/{id}")
  @Produces("video/mp4")
  public byte[] getCypressVideo(@PathParam("id") Long id) {
    return testRetrievalService.getVideo(id);
  }

  @GET
  @Path("/screenshots/{id}")
  @Produces("image/png")
  public byte[] getCypressScreenshot(@PathParam("id") Long id) {
    return testScreenshotRetrievalService.getScreenshot(id);
  }
}
