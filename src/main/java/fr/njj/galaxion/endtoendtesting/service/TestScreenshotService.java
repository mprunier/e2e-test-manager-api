package fr.njj.galaxion.endtoendtesting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportTestInternal;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestScreenshotEntity;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.SCREENSHOT_PATH;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestScreenshotService {

    public void create(MochaReportTestInternal mochaTest,
                       Map<String, byte[]> screenshots,
                       TestEntity test) {
        try {
            var contextList = mochaTest.getContextParse();
            if (contextList != null) {
                var screenshotError = contextList.stream()
                                                 .filter(item -> "screenshotError".equals(item.getTitle()))
                                                 .findFirst();
                screenshotError.ifPresent(mochaReportContextInternal -> handleScreenshot(mochaReportContextInternal.getValue(), screenshots, test));
            }
        } catch (JsonProcessingException e) {
            log.info("Screenshot not found on test id [{}]", test.getId());
        }
    }

    private void handleScreenshot(String screenshotFilename, Map<String, byte[]> screenshots, TestEntity test) {
        screenshotFilename = screenshotFilename.replace(":", ""); // TODO add other character
        byte[] screenshot = screenshots.get(screenshotFilename);

        if (screenshot != null) {
            TestScreenshotEntity.builder()
                                .test(test)
                                .filename(screenshotFilename.replace(SCREENSHOT_PATH, ""))
                                .screenshot(screenshot)
                                .build()
                                .persist();
        } else {
            var modifiedScreenshotFilename = removeTextBetweenSlashes(screenshotFilename);
            if (!modifiedScreenshotFilename.equals(screenshotFilename)) {
                handleScreenshot(modifiedScreenshotFilename, screenshots, test);
            }
        }
    }

    private static String removeTextBetweenSlashes(String input) {
        int firstSlash = indexOfNthSlash(input, 2);
        int secondSlash = indexOfNthSlash(input, 3);

        if (firstSlash == -1 || secondSlash == -1) {
            return input;
        }

        String before = input.substring(0, firstSlash);
        String after = input.substring(secondSlash);

        return before + after;
    }

    private static int indexOfNthSlash(String input, int n) {
        int index = -1;
        while (n > 0 && index < input.length() - 1) {
            index = input.indexOf("/", index + 1);
            n--;
        }
        return index;
    }

}


