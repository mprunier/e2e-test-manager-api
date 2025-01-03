package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.mapper;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.GROUP_FOR_PARALLELIZATION;
import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.NO_SUITE;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.constant.CypressConstant.CYPRESS_SUITE_FUNCTION_NAME;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.constant.CypressConstant.CYPRESS_TAGS_PARAM_NAME;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.constant.CypressConstant.CYPRESS_TEST_FUNCTION_NAME;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.constant.CypressConstant.CYPRESS_VARIABLES_PARAM_NAME;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.SuiteConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.TestConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Variable;
import fr.plum.e2e.manager.core.domain.model.exception.CharactersForbiddenException;
import fr.plum.e2e.manager.core.domain.model.exception.SuiteNoTitleException;
import fr.plum.e2e.manager.core.domain.model.exception.TitleEmptyException;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.exception.BuildFileConfigurationException;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.exception.SuiteShouldBeNotContainsSubSuiteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.StringLiteral;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CypressFileConfigurationMapper {

  public static FileConfiguration build(String fileName, String content) {
    try {
      var env = new CompilerEnvirons();
      env.setRecordingComments(true);
      env.setRecordingLocalJsDocComments(true);
      env.setLanguageVersion(Context.VERSION_ES6);

      var parser = new Parser(env);
      var astRoot = parser.parse(content, null, 0);

      var rootTests = new ArrayList<TestConfiguration>();
      var suites = new ArrayList<SuiteConfiguration>();

      processNode(astRoot, rootTests, suites, fileName);

      if (!rootTests.isEmpty()) {
        var rootTestSuite =
            SuiteConfiguration.builder().title(new SuiteTitle(NO_SUITE)).tests(rootTests).build();
        suites.add(rootTestSuite);
      }

      return FileConfiguration.builder()
          .id(new FileName(fileName))
          .group(getGroupName(astRoot))
          .suites(suites)
          .build();

    } catch (EvaluatorException e) {
      throw new BuildFileConfigurationException(e.details(), e.lineSource());
    }
  }

  private static GroupName getGroupName(AstRoot astRoot) {
    var sortedComments = new ArrayList<Comment>();
    if (astRoot.getComments() != null) {
      sortedComments.addAll(astRoot.getComments());
    }
    sortedComments.sort(Comparator.comparingInt(AstNode::getAbsolutePosition));
    if (!sortedComments.isEmpty()) {
      var firstComment = sortedComments.getFirst();
      var commentValue = firstComment.getValue();
      if (commentValue != null) {
        String groupPattern = "use\\s+" + GROUP_FOR_PARALLELIZATION + "(\\d+)";
        Pattern pattern = Pattern.compile(groupPattern);
        Matcher matcher = pattern.matcher(commentValue);
        if (matcher.find()) {
          String group = matcher.group(1);
          return new GroupName(group);
        }
      }
    }
    return null;
  }

  private static void processNode(
      AstNode node,
      List<TestConfiguration> parentTests,
      List<SuiteConfiguration> parentSuites,
      String fileName) {

    if (node instanceof ExpressionStatement exprStatement) {
      var expression = exprStatement.getExpression();
      if (expression instanceof FunctionCall call) {
        var firstArg = call.getArguments().getFirst();
        if (CYPRESS_SUITE_FUNCTION_NAME.equals(call.getTarget().toSource())
            && firstArg instanceof StringLiteral) {
          buildSuite(node, parentSuites, call, (StringLiteral) firstArg, fileName);
        } else if (CYPRESS_TEST_FUNCTION_NAME.equals(call.getTarget().toSource())
            && firstArg instanceof StringLiteral) {
          buildTest(node, parentTests, (StringLiteral) firstArg);
        }
      }
    } else {
      for (var childNode = node.getFirstChild();
          childNode != null;
          childNode = childNode.getNext()) {
        if (childNode instanceof AstNode) {
          processNode((AstNode) childNode, parentTests, parentSuites, fileName);
        }
      }
    }
  }

  private static void buildSuite(
      AstNode node,
      List<SuiteConfiguration> parentSuites,
      FunctionCall call,
      StringLiteral titleNode,
      String fileName) {

    var tests = new ArrayList<TestConfiguration>();
    var subSuites = new ArrayList<SuiteConfiguration>();
    var tags = new ArrayList<Tag>();
    var variables = new ArrayList<Variable>();

    extractMetadata(node, tags, variables);
    processSuiteBody(call, tests, subSuites, fileName);

    validateNoSubSuite(subSuites);
    validateSuiteTitle(titleNode);

    var suite =
        SuiteConfiguration.builder()
            .title(new SuiteTitle(titleNode.getValue()))
            .tags(tags)
            .variables(variables)
            .tests(tests)
            .build();

    parentSuites.add(suite);
  }

  private static void validateSuiteTitle(StringLiteral titleNode) {
    if (StringUtils.isBlank(titleNode.getValue())) {
      throw new TitleEmptyException();
    }
    if (titleNode.getValue().contains("|") || titleNode.getValue().contains(";")) {
      throw new CharactersForbiddenException();
    }
    if (NO_SUITE.equals(titleNode.getValue())) {
      throw new SuiteNoTitleException();
    }
  }

  private static void validateNoSubSuite(ArrayList<SuiteConfiguration> subSuites) {
    if (!subSuites.isEmpty()) {
      throw new SuiteShouldBeNotContainsSubSuiteException();
    }
  }

  private static void processSuiteBody(
      FunctionCall call,
      List<TestConfiguration> tests,
      List<SuiteConfiguration> suites,
      String fileName) {

    var functionNode = getFunctionNode(call);
    if (functionNode != null) {
      var body = functionNode.getBody();
      for (var childNode = body.getFirstChild();
          childNode != null;
          childNode = childNode.getNext()) {
        if (childNode instanceof AstNode) {
          processNode((AstNode) childNode, tests, suites, fileName);
        }
      }
    }
  }

  private static FunctionNode getFunctionNode(FunctionCall call) {
    var secondArg = call.getArguments().get(1);
    if (secondArg instanceof FunctionNode) {
      return (FunctionNode) secondArg;
    }
    if (call.getArguments().size() > 2) {
      var thirdArg = call.getArguments().get(2);
      if (thirdArg instanceof FunctionNode) {
        return (FunctionNode) thirdArg;
      }
    }
    return null;
  }

  private static void buildTest(
      AstNode node, List<TestConfiguration> parentTests, StringLiteral titleNode) {

    var tags = new ArrayList<Tag>();
    var variables = new ArrayList<Variable>();

    extractMetadata(node, tags, variables);

    var test =
        TestConfiguration.builder()
            .title(new TestTitle(titleNode.getValue()))
            .position(new Position(node.getAbsolutePosition()))
            .tags(tags)
            .variables(variables)
            .build();

    parentTests.add(test);
  }

  private static void extractMetadata(AstNode node, List<Tag> tags, List<Variable> variables) {
    if (node instanceof ExpressionStatement) {
      var expression = ((ExpressionStatement) node).getExpression();
      if (expression instanceof FunctionCall functionCall
          && functionCall.getArguments().size() > 1) {
        var metadataNode = functionCall.getArguments().get(1);
        if (metadataNode instanceof ObjectLiteral) {
          var properties = ((ObjectLiteral) metadataNode).getElements();
          for (ObjectProperty property : properties) {
            if (property.getLeft() instanceof Name name) {
              if (CYPRESS_TAGS_PARAM_NAME.equals(name.getIdentifier())) {
                extractTags(property.getRight(), tags);
              }
              if (CYPRESS_VARIABLES_PARAM_NAME.equals(name.getIdentifier())) {
                extractVariables(property.getRight(), variables);
              }
            }
          }
        }
      }
    }
  }

  private static void extractTags(AstNode value, List<Tag> tags) {
    if (value instanceof ArrayLiteral arrayLiteral) {
      for (AstNode idNode : arrayLiteral.getElements()) {
        if (idNode instanceof StringLiteral stringLiteral) {
          tags.add(new Tag(stringLiteral.getValue()));
        }
      }
    }
  }

  private static void extractVariables(AstNode value, List<Variable> variables) {
    if (value instanceof ArrayLiteral arrayLiteral) {
      for (AstNode idNode : arrayLiteral.getElements()) {
        if (idNode instanceof StringLiteral stringLiteral) {
          variables.add(new Variable(stringLiteral.getValue()));
        }
      }
    }
  }
}
