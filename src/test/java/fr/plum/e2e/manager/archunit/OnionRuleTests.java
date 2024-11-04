package fr.plum.e2e.manager.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "fr.plum.e2e.manager",
    importOptions = {ImportOption.DoNotIncludeTests.class, CustomImportOption.class})
public final class OnionRuleTests {

  @ArchTest
  static final ArchRule validateRegistrationContextArchitecture =
      onionArchitecture()
          .domainModels("..domain.model..", "..domain.port..")
          .domainServices("..domain.service..")
          .applicationServices("..application..")
          .adapter("infrastructure", "..infrastructure..")
          .withOptionalLayers(true);

  @ArchTest
  static final ArchRule testDomainDependencies =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("jakarta.enterprise..", "jakarta.persistence..");

  @ArchTest
  static final ArchRule testApplicationDependencies =
      noClasses()
          .that()
          .resideInAPackage("..application..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("jakarta.enterprise..", "jakarta.persistence..");

  @ArchTest
  static final ArchRule noCyclicDependencies =
      slices().matching("fr.plum.e2e.manager.(*)..").should().beFreeOfCycles();

  private OnionRuleTests() {}
}
