package fr.plum.e2e.OLD.mapper;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.DISABLE_TAG;
import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.GROUP_FOR_PARALLELIZATION;

import fr.plum.e2e.OLD.domain.exception.SubSuiteException;
import fr.plum.e2e.OLD.domain.internal.ConfigurationInternal;
import fr.plum.e2e.OLD.domain.internal.ConfigurationSuiteInternal;
import fr.plum.e2e.OLD.domain.internal.ConfigurationTestInternal;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public final class ConfigurationInternalMapper {

  public static ConfigurationInternal build(String content, String fullPath) {
    var configurationInternal = new ConfigurationInternal();
    try {
      var env = new CompilerEnvirons();
      env.setRecordingComments(true);
      env.setRecordingLocalJsDocComments(true);
      env.setLanguageVersion(Context.VERSION_ES6);

      var parser = new Parser(env);
      var astRoot = parser.parse(content, null, 0);

      setGroupIfExist(astRoot, configurationInternal);

      processNode(astRoot, configurationInternal, null, fullPath);
    } catch (EvaluatorException e) {
      throw new CustomException(
          Response.Status.INTERNAL_SERVER_ERROR,
          "synchronization-error",
          String.format("Error : %s on line : %s.", e.details(), e.lineSource()));
    }

    removeDisabledItems(configurationInternal);
    return configurationInternal;
  }

  private static void setGroupIfExist(
      AstRoot astRoot, ConfigurationInternal configurationInternal) {
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
          configurationInternal.setGroup(group);
        }
      }
    }
  }

  private static void removeDisabledItems(ConfigurationInternal config) {
    config.setTests(removeDisabledTests(config.getTests()));
    config.setSuites(removeDisabledSuites(config.getSuites()));
  }

  private static List<ConfigurationTestInternal> removeDisabledTests(
      List<ConfigurationTestInternal> tests) {
    tests.removeIf(ConfigurationTestInternal::isToDisable);
    return tests;
  }

  private static List<ConfigurationSuiteInternal> removeDisabledSuites(
      List<ConfigurationSuiteInternal> suites) {
    suites.removeIf(ConfigurationSuiteInternal::isToDisable);
    for (ConfigurationSuiteInternal suite : suites) {
      removeDisabledTests(suite.getTests());
      removeDisabledSuites(suite.getSuites());
    }
    return suites;
  }

  private static void processNode(
      AstNode node,
      ConfigurationInternal configurationInternal,
      ConfigurationSuiteInternal parentSuite,
      String fullPath) {
    if (node instanceof ExpressionStatement exprStatement) {
      var expression = exprStatement.getExpression();
      if (expression instanceof FunctionCall call) {
        var firstArg = call.getArguments().getFirst();
        if ("describe".equals(call.getTarget().toSource()) && firstArg instanceof StringLiteral) {
          buildSuite(
              node, configurationInternal, parentSuite, call, (StringLiteral) firstArg, fullPath);
        } else if ("it".equals(call.getTarget().toSource()) && firstArg instanceof StringLiteral) {
          buildTest(node, configurationInternal, parentSuite, (StringLiteral) firstArg);
        }
      }
    } else {
      for (var childNode = node.getFirstChild();
          childNode != null;
          childNode = childNode.getNext()) {
        if (childNode instanceof AstNode) {
          processNode((AstNode) childNode, configurationInternal, parentSuite, fullPath);
        }
      }
    }
  }

  private static void buildSuite(
      AstNode node,
      ConfigurationInternal configurationInternal,
      ConfigurationSuiteInternal parentSuite,
      FunctionCall call,
      StringLiteral firstArg,
      String fullPath) {
    var suite = new ConfigurationSuiteInternal();
    suite.setTitle(firstArg.getValue());

    extractVariables(node, suite);

    if (parentSuite == null && configurationInternal != null) {
      configurationInternal.getSuites().add(suite);
    } else if (parentSuite != null) {
      throw new SubSuiteException(fullPath, firstArg.getValue());
    }

    var secondArg = call.getArguments().get(1);
    if (secondArg instanceof FunctionNode) {
      var body = ((FunctionNode) secondArg).getBody();

      for (var childNode = body.getFirstChild();
          childNode != null;
          childNode = childNode.getNext()) {
        if (childNode instanceof AstNode) {
          processNode((AstNode) childNode, null, suite, fullPath);
        }
      }
    }

    if (!(secondArg instanceof FunctionNode) && call.getArguments().size() > 2) {
      var thirdArg = call.getArguments().get(2);
      if (thirdArg instanceof FunctionNode) {
        var body = ((FunctionNode) thirdArg).getBody();

        for (var childNode = body.getFirstChild();
            childNode != null;
            childNode = childNode.getNext()) {
          if (childNode instanceof AstNode) {
            processNode((AstNode) childNode, null, suite, fullPath);
          }
        }
      }
    }
  }

  private static void buildTest(
      AstNode node,
      ConfigurationInternal configurationInternal,
      ConfigurationSuiteInternal parentSuite,
      StringLiteral firstArg) {
    var testCase = new ConfigurationTestInternal();
    testCase.setTitle(firstArg.getValue());

    testCase.setPosition(node.getAbsolutePosition());

    extractTagsAndVariables(node, testCase);

    if (parentSuite == null && configurationInternal != null) {
      insertTestInOrder(configurationInternal.getTests(), testCase);
    } else if (parentSuite != null) {
      insertTestInOrder(parentSuite.getTests(), testCase);
    }
  }

  private static void insertTestInOrder(
      List<ConfigurationTestInternal> tests, ConfigurationTestInternal newTest) {
    int insertIndex = 0;
    for (ConfigurationTestInternal test : tests) {
      if (test.getPosition() > newTest.getPosition()) {
        break;
      }
      insertIndex++;
    }
    tests.add(insertIndex, newTest);
  }

  private static void extractTagsAndVariables(AstNode node, ConfigurationTestInternal testCase) {
    if (node instanceof ExpressionStatement) {
      var expression = ((ExpressionStatement) node).getExpression();
      if (expression instanceof FunctionCall functionCall
          && functionCall.getArguments().size() > 1) {
        var metadataNode = functionCall.getArguments().get(1);
        if (metadataNode instanceof ObjectLiteral) {
          var properties = ((ObjectLiteral) metadataNode).getElements();
          for (ObjectProperty property : properties) {
            if (property.getLeft() instanceof Name name) {
              if ("tags".equals(name.getIdentifier())) {
                var value = property.getRight();
                if (value instanceof ArrayLiteral xrayIds) {
                  for (AstNode idNode : xrayIds.getElements()) {
                    if (idNode instanceof StringLiteral) {
                      var tag = ((StringLiteral) idNode).getValue();
                      if (DISABLE_TAG.equals(tag)) {
                        testCase.setToDisable(true);
                      }
                      testCase.getTags().add(tag);
                    }
                  }
                }
              }
              if ("variables".equals(name.getIdentifier())) {
                var value = property.getRight();
                if (value instanceof ArrayLiteral xrayIds) {
                  for (AstNode idNode : xrayIds.getElements()) {
                    if (idNode instanceof StringLiteral) {
                      var variable = ((StringLiteral) idNode).getValue();
                      testCase.getVariables().add(variable);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private static void extractVariables(AstNode node, ConfigurationSuiteInternal suiteCase) {
    if (node instanceof ExpressionStatement) {
      var expression = ((ExpressionStatement) node).getExpression();
      if (expression instanceof FunctionCall functionCall
          && functionCall.getArguments().size() > 1) {
        var metadataNode = functionCall.getArguments().get(1);
        if (metadataNode instanceof ObjectLiteral) {
          var properties = ((ObjectLiteral) metadataNode).getElements();
          for (ObjectProperty property : properties) {
            if (property.getLeft() instanceof Name name) {
              if ("tags".equals(name.getIdentifier())) {
                var value = property.getRight();
                if (value instanceof ArrayLiteral xrayIds) {
                  for (AstNode idNode : xrayIds.getElements()) {
                    if (idNode instanceof StringLiteral) {
                      var tag = ((StringLiteral) idNode).getValue();
                      if (DISABLE_TAG.equals(tag)) {
                        suiteCase.setToDisable(true);
                        break;
                      }
                      suiteCase.getTags().add(tag);
                    }
                  }
                }
              }
              if ("variables".equals(name.getIdentifier())) {
                var value = property.getRight();
                if (value instanceof ArrayLiteral xrayIds) {
                  for (AstNode idNode : xrayIds.getElements()) {
                    if (idNode instanceof StringLiteral) {
                      var variable = ((StringLiteral) idNode).getValue();
                      suiteCase.getVariables().add(variable);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
