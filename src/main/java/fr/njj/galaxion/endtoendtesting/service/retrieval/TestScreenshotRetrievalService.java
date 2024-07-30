package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.domain.exception.TestScreenshotNotFoundException;
import fr.njj.galaxion.endtoendtesting.model.repository.TestScreenshotRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestScreenshotRetrievalService {

    private final TestScreenshotRepository testScreenshotRepository;

    @Transactional
    public byte[] getScreenshot(Long id) {
        return testScreenshotRepository
                .findByIdOptional(id)
                .orElseThrow(() -> new TestScreenshotNotFoundException(id))
                .getScreenshot();
    }
}
