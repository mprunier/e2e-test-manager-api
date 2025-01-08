package fr.plum.e2e.manager.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import fr.plum.e2e.manager.sharedkernel.application.command.CommandHandler;
import fr.plum.e2e.manager.sharedkernel.application.command.NoParamCommandHandler;
import fr.plum.e2e.manager.sharedkernel.application.query.NoParamQueryHandler;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import org.junit.jupiter.api.Test;

public class HandlerArchitectureTest {

  JavaClasses coreClasses = new ClassFileImporter().importPackages("fr.plum.e2e.manager.core");
  JavaClasses applicationClasses =
      new ClassFileImporter().importPackages("fr.plum.e2e.manager.core.application");

  @Test
  void handler_classes_should_implement_handler_interfaces() {
    ArchRule rule =
        classes()
            .that()
            .haveNameMatching(".*Handler")
            .should()
            .implement(CommandHandler.class)
            .orShould()
            .implement(NoParamCommandHandler.class)
            .orShould()
            .implement(QueryHandler.class)
            .orShould()
            .implement(NoParamQueryHandler.class)
            .because("All handler classes must implement one of the handler interfaces");

    rule.check(applicationClasses);
  }

  @Test
  void verify_specific_handler_naming_convention() {
    ArchRule commandHandlerRule =
        classes()
            .that()
            .implement(CommandHandler.class)
            .or()
            .implement(NoParamCommandHandler.class)
            .should()
            .haveNameMatching(".*CommandHandler")
            .because("Command handlers should end with 'CommandHandler'");

    ArchRule queryHandlerRule =
        classes()
            .that()
            .implement(QueryHandler.class)
            .or()
            .implement(NoParamQueryHandler.class)
            .should()
            .haveNameMatching(".*QueryHandler")
            .because("Query handlers should end with 'QueryHandler'");

    commandHandlerRule.check(applicationClasses);
    queryHandlerRule.check(applicationClasses);
  }

  @Test
  void infrastructure_should_not_access_ports_or_services_directly() {
    ArchRule rule =
        noClasses()
            .that()
            .resideInAnyPackage(
                "..infrastructure.primary.consumer..", "..infrastructure.primary.rest..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..domain.port..")
            .orShould()
            .accessClassesThat()
            .resideInAPackage("..domain.service..");

    rule.check(coreClasses);
  }
}
