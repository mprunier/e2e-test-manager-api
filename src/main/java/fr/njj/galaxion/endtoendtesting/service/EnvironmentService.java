package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentService {

  private final EnvironmentRetrievalService environmentRetrievalService;

  @Transactional
  public void stopAllTests(Long id) {
    var environment = environmentRetrievalService.get(id);
    environment.stopAllTests();
  }
}
