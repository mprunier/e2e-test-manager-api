package fr.plum.e2e.manager.core.domain.usecase.synchronization.process.factory;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.GLOBAL_ENVIRONMENT_ERROR;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationErrorValue;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SynchronizationErrorFactory {

  public static SynchronizationError createFileError(
      SynchronizationFileName fileName, String message, ZonedDateTime at) {
    return new SynchronizationError(
        fileName,
        new SynchronizationErrorValue(StringUtils.isNotBlank(message) ? message : "No message"),
        at);
  }

  public static SynchronizationError createGlobalError(String message, ZonedDateTime at) {
    return new SynchronizationError(
        new SynchronizationFileName(GLOBAL_ENVIRONMENT_ERROR),
        new SynchronizationErrorValue(message),
        at);
  }
}
