package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSuiteRepository;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationTestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DeleteConfigurationTestAndSuiteService {

    private final ConfigurationSuiteRepository configurationSuiteRepository;
    private final ConfigurationTestRepository configurationTestRepository;

    @Transactional
    public void deleteByEnvAndFile(
            long environmentId,
            String file) {
        log.info("Environment id [{}] : Remove file [{}].", environmentId, file);
        configurationTestRepository.deleteByFileAndEnv(file, environmentId);
        configurationSuiteRepository.deleteByFileAndEnv(file, environmentId);
    }

    @Transactional
    public void deleteTestByEnvAndFileAndNotInTestIds(
            long environmentId,
            String file,
            List<Long> testIds) {
        configurationTestRepository.deleteByEnvAndFileAndNotInTestIds(environmentId, file, testIds);
    }

    @Transactional
    public void deleteSuiteByEnvAndFileAndNotInTestIds(
            long environmentId,
            String file,
            List<Long> suiteIds) {
        configurationSuiteRepository.deleteByEnvAndFileAndNotInSuiteIds(environmentId, file, suiteIds);
    }
}

