package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.SuiteConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.TestConfiguration;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CypressFileConfigurationMapperTest {

  @Test
  void shouldParseFileWithAllParameters() {
    // Given
    String content =
        """
            //use group-for-parallelization-1
            describe('Suite1', {variables: ['variableSuite1'], tags: ['tagSuite1']}, function() {
              it('Test1', {tags: ['tagTest1'], variables: ['variableTest1']}, function() { /* test code */ });
              it('Test2', function() { /* test code */ });
            });
            """;

    // When
    FileConfiguration result =
        CypressFileConfigurationMapper.build(
            new EnvironmentId(UUID.randomUUID()),
            new SynchronizationFileName("testFile.js"),
            new SynchronizationFileContent(content),
            ZonedDateTime.now());

    // Then
    assertEquals("1", result.getGroup().value());
    assertEquals(1, result.getSuites().size());

    SuiteConfiguration suite = result.getSuites().getFirst();
    assertEquals("Suite1", suite.getTitle().value());
    assertEquals(1, suite.getTags().size());
    assertEquals("tagSuite1", suite.getTags().getFirst().value());
    assertEquals(1, suite.getVariables().size());
    assertEquals("variableSuite1", suite.getVariables().getFirst().value());

    assertEquals(2, suite.getTests().size());

    TestConfiguration test1 = suite.getTests().getFirst();
    assertEquals("Test1", test1.getTitle().value());
    assertEquals(1, test1.getTags().size());
    assertEquals("tagTest1", test1.getTags().getFirst().value());
    assertEquals(1, test1.getVariables().size());
    assertEquals("variableTest1", test1.getVariables().getFirst().value());

    TestConfiguration test2 = suite.getTests().get(1);
    assertEquals("Test2", test2.getTitle().value());
    assertTrue(test2.getTags().isEmpty());
    assertTrue(test2.getVariables().isEmpty());
  }

  @Test
  void shouldParseFileWithMultipleTests() {
    // Given
    String content =
        """
            // use group-for-parallelization-0040
            describe('Suite1', function() {
              it('Test1', {tags: ['tag1'], variables: ['variableTest1']}, function() { /* test code */ });
              it('Test2', function() { /* test code */ });
              it('Test3', function() { /* test code */ });
              it('Test4', function() { /* test code */ });
              it('Test5', function() { /* test code */ });
              it('Test6', function() { /* test code */ });
              it('Test7', function() { /* test code */ });
            });
            """;

    // When
    FileConfiguration result =
        CypressFileConfigurationMapper.build(
            new EnvironmentId(UUID.randomUUID()),
            new SynchronizationFileName("testFile.js"),
            new SynchronizationFileContent(content),
            ZonedDateTime.now());

    // Then
    assertEquals("0040", result.getGroup().value());
    assertEquals(1, result.getSuites().size());

    SuiteConfiguration suite = result.getSuites().getFirst();
    assertEquals("Suite1", suite.getTitle().value());
    assertTrue(suite.getTags().isEmpty());
    assertTrue(suite.getVariables().isEmpty());
    assertEquals(7, suite.getTests().size());

    TestConfiguration firstTest = suite.getTests().getFirst();
    assertEquals("Test1", firstTest.getTitle().value());
    assertEquals(1, firstTest.getTags().size());
    assertEquals("tag1", firstTest.getTags().getFirst().value());
    assertEquals(1, firstTest.getVariables().size());
    assertEquals("variableTest1", firstTest.getVariables().getFirst().value());

    // Verify Test2-7 exist with correct titles
    for (int i = 1; i < 7; i++) {
      TestConfiguration test = suite.getTests().get(i);
      assertEquals("Test" + (i + 1), test.getTitle().value());
      assertTrue(test.getTags().isEmpty());
      assertTrue(test.getVariables().isEmpty());
    }
  }
}
