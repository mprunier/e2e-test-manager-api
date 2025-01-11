package fr.plum.e2e.manager.core.domain.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BusinessConstant {

  public static final String NO_SUITE = "No Suite";
  public static final String GLOBAL_ENVIRONMENT_ERROR = "Global error on this environment";
  public static final String DISABLE_TAG = "disable-on-e2e-testing-manager";
  public static final String GROUP_FOR_PARALLELIZATION = "group-for-parallelization-";

  public static final String ERROR_TYPESCRIPT_TRANSPILATION =
      "Error during the transpilation of TypeScript code into JavaScript. "
          + "Please ensure that your code is correctly formatted as JavaScript or TypeScript without any errors.";
  public static final String ERROR_ES6_TRANSPILATION =
      "Error during the transpilation of JavaScript code to ES6. "
          + "Please ensure that your code is correctly formatted as JavaScript or TypeScript without any errors.";
  public static final String END_TS_PATH = ".ts";
  public static final String NO_GROUP_NAME = "Dummy";
}
