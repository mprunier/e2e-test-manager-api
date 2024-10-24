package fr.njj.galaxion.endtoendtesting.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PipelineStatus {
  IN_PROGRESS(null),
  FINISH(null),
  CANCELED("This pipeline was cancelled."),
  SYSTEM_ERROR(
      "This pipeline has failed due to an internal error in the tool. Contact an administrator."),
  NO_REPORT_ERROR(
      "The pipeline has failed because there was no test report. Check the pipeline with logs/artefacts on your gitlab project.");

  private final String errorMessage;
}
