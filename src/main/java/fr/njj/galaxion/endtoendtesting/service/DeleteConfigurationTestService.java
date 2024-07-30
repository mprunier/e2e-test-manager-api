package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSuiteRepository;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationTestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class DeleteConfigurationTestService {

    private final ConfigurationSuiteRepository configurationSuiteRepository;
    private final ConfigurationTestRepository configurationTestRepository;

    @Transactional
    public void deleteByFile(String file,
                             long environmentId) {
        log.info("Environment id [{}] : Remove file [{}].", environmentId, file);
        configurationTestRepository.deleteByFileAndEnv(file, environmentId);
        configurationSuiteRepository.deleteByFileAndEnv(file, environmentId);
    }
}

