package fr.njj.galaxion.endtoendtesting.service.test;

import fr.njj.galaxion.endtoendtesting.domain.exception.TestScreenshotNotFoundException;
import fr.njj.galaxion.endtoendtesting.model.repository.TestScreenshotRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestScreenshotRetrievalService {

    private final TestScreenshotRepository testScreenshotRepository;

    public byte[] getCypressScreenshot(Long id) {
        return testScreenshotRepository.findByIdOptional(id)
                                       .orElseThrow(() -> new TestScreenshotNotFoundException(id))
                                       .getScreenshot();
    }
}
