package fr.plum.e2e.OLD.service;

import fr.plum.e2e.OLD.model.repository.TestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class TestService {

  private final TestRepository testRepository;

  @Transactional
  public void setNotWaiting(String pipelineId) {
    var tests = testRepository.findAllByPipelineId(pipelineId);
    tests.forEach(
        testEntity -> {
          testEntity.setWaiting(false);
        });
  }
}
