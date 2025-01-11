package fr.plum.e2e.manager.it;

import fr.plum.e2e.manager.core.application.command.synchronization.ProcessSynchronizationCommandHandler;
import fr.plum.e2e.manager.core.application.query.suite.SearchSuiteQueryHandler;
import fr.plum.e2e.manager.core.application.query.synchronization.ListAllSynchronizationErrorsQueryHandler;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.port.SourceCodePort;
import fr.plum.e2e.manager.it.resource.JSConverterTestResource;
import fr.plum.e2e.manager.it.resource.PostgresTestResource;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@QuarkusTestResource(JSConverterTestResource.class)
class SynchronizationIntegrationTest {

  public static final UUID DEFAULT_ENVIRONMENT_UUID =
      UUID.fromString("8d8ea7dd-6115-4437-94f3-67c4999d9468");
  private static final String TEST_RESOURCES_PATH = "src/test/resources/cypress/gitrepo";
  private static final String TEST_REPO_DIR = "target/test-repositories";

  @Inject ProcessSynchronizationCommandHandler processSynchronizationCommandHandler;
  @Inject SearchSuiteQueryHandler searchSuiteQueryHandler;
  @Inject ListAllSynchronizationErrorsQueryHandler listAllSynchronizationErrorsQueryHandler;

  @InjectMock SourceCodePort sourceCodePort;

  private File tempProjectDir;

  @BeforeEach
  void setUp() throws IOException {
    File baseDir = new File(TEST_REPO_DIR);
    baseDir.mkdirs();
    tempProjectDir = new File(baseDir, "git-repo-" + System.currentTimeMillis());
    FileUtils.copyDirectory(new File(TEST_RESOURCES_PATH), tempProjectDir);
  }

  @Test
  void shouldSynchronizeSuccessfully() {
    // Given
    var environmentId = new EnvironmentId(DEFAULT_ENVIRONMENT_UUID);
    var username = new ActionUsername("test-user");
    var command = new CommonCommand(environmentId, username);

    var mockProject = new SourceCodeProject(tempProjectDir);
    Mockito.when(sourceCodePort.cloneRepository(Mockito.any())).thenReturn(mockProject);

    // When
    processSynchronizationCommandHandler.execute(command);

    // Then
    var searchSuiteResults =
        searchSuiteQueryHandler.execute(
            SearchSuiteConfigurationQuery.builder()
                .environmentId(environmentId)
                .allNotSuccess(false)
                .page(0)
                .size(10)
                .sortField("file")
                .sortOrder("ASC")
                .build());
    Assertions.assertEquals(2, searchSuiteResults.getContent().size());
    var suite1 = searchSuiteResults.getContent().getFirst();
    Assertions.assertEquals("Suite 1", suite1.title());
    Assertions.assertEquals(2, suite1.tags().size());
    Assertions.assertEquals(2, suite1.variables().size());
    Assertions.assertEquals(3, suite1.tests().size());
    var suite1test3 = suite1.tests().getLast();
    Assertions.assertEquals(2, suite1test3.tags().size());
    Assertions.assertEquals(2, suite1test3.variables().size());

    var suite3 = searchSuiteResults.getContent().getLast();
    Assertions.assertEquals("Suite 3", suite3.title());
    Assertions.assertEquals(2, suite3.tags().size());
    Assertions.assertEquals(2, suite3.variables().size());
    Assertions.assertEquals(1, suite3.tests().size());
    var suite3test1 = suite3.tests().getFirst();
    Assertions.assertEquals(2, suite3test1.tags().size());
    Assertions.assertEquals(2, suite3test1.variables().size());

    var errors = listAllSynchronizationErrorsQueryHandler.execute(new CommonQuery(environmentId));
    Assertions.assertEquals(2, errors.size());
  }
}
