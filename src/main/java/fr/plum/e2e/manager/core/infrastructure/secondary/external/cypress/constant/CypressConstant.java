package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CypressConstant {

  public static final String END_TEST_JS_PATH = ".cy.js";
  public static final String END_TEST_TS_PATH = ".cy.ts";
  public static final String START_PATH = "cypress/e2e/";
  public static final String SCREENSHOT_PATH = "cypress/screenshots/";
  public static final String CYPRESS_SUITE_FUNCTION_NAME = "describe";
  public static final String CYPRESS_TEST_FUNCTION_NAME = "it";
  public static final String CYPRESS_TAGS_PARAM_NAME = "tags";
  public static final String CYPRESS_VARIABLES_PARAM_NAME = "variables";
  public static final String SCREENSHOT_EXTENSION = ".png";
}
