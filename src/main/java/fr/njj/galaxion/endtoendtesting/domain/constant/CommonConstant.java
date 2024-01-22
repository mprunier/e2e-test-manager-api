package fr.njj.galaxion.endtoendtesting.domain.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstant {

    public static final String START_PATH = "cypress/e2e/";
    public static final String END_TEST_JS_PATH = ".cy.js";
    public static final String END_TEST_TS_PATH = ".cy.ts";
    public static final String SCREENSHOT_PATH = "cypress/screenshots/";
    public static final String NO_SUITE = "No Suite";
}
