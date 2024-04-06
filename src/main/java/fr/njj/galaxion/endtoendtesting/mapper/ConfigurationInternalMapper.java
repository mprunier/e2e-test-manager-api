package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.exception.SubSuiteException;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationTestInternal;
import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.StringLiteral;

import java.util.ArrayList;
import java.util.Comparator;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationInternalMapper {

    public static ConfigurationInternal build(String content, String fullPath) {
        var configurationInternal = new ConfigurationInternal();
        try {

            content = content.replaceAll("(?m)^import .+;$", "");
            content = content.replaceAll("(?m)^[ \t]*\r?\n", "");
            content = content.replaceAll("async function", "function");

            var env = new CompilerEnvirons();
            env.setRecordingComments(true);
            env.setRecordingLocalJsDocComments(true);
            env.setLanguageVersion(Context.VERSION_ES6);

            var parser = new Parser(env);
            var astRoot = parser.parse(content, null, 0);

            var sortedComments = new ArrayList<Comment>();
            if (astRoot.getComments() != null) {
                sortedComments.addAll(astRoot.getComments());
            }
            sortedComments.sort(Comparator.comparingInt(AstNode::getAbsolutePosition));
            processNode(astRoot, configurationInternal, null, fullPath);
        } catch (EvaluatorException e) {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "synchronization-error", String.format("Error in file %s : %s on line : %s. Please note that this application is currently only compatible with Cypress code written in ES6 or lower. If you use features from ES8 or newer, such as async or await, your file cannot be added.", fullPath, e.details(), e.lineSource()));
        }
        return configurationInternal;
    }

    private static void processNode(AstNode node, ConfigurationInternal configurationInternal, ConfigurationSuiteInternal parentSuite, String fullPath) {
        if (node instanceof ExpressionStatement exprStatement) {
            var expression = exprStatement.getExpression();
            if (expression instanceof FunctionCall call) {
                var firstArg = call.getArguments().get(0);
                if (call.getTarget().toSource().equals("describe") && firstArg instanceof StringLiteral) {
                    buildSuite(node, configurationInternal, parentSuite, call, (StringLiteral) firstArg, fullPath);
                } else if (call.getTarget().toSource().equals("it") && firstArg instanceof StringLiteral) {
                    buildTest(node, configurationInternal, parentSuite, (StringLiteral) firstArg);
                }
            }
        } else {
            for (var childNode = node.getFirstChild(); childNode != null; childNode = childNode.getNext()) {
                if (childNode instanceof AstNode) {
                    processNode((AstNode) childNode, configurationInternal, parentSuite, fullPath);
                }
            }
        }
    }

    private static void buildSuite(AstNode node, ConfigurationInternal configurationInternal, ConfigurationSuiteInternal parentSuite, FunctionCall call, StringLiteral firstArg, String fullPath) {
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

            for (var childNode = body.getFirstChild(); childNode != null; childNode = childNode.getNext()) {
                if (childNode instanceof AstNode) {
                    processNode((AstNode) childNode, null, suite, fullPath);
                }
            }
        }

        if (!(secondArg instanceof FunctionNode) && call.getArguments().size() > 2) {
            var thirdArg = call.getArguments().get(2);
            if (thirdArg instanceof FunctionNode) {
                var body = ((FunctionNode) thirdArg).getBody();

                for (var childNode = body.getFirstChild(); childNode != null; childNode = childNode.getNext()) {
                    if (childNode instanceof AstNode) {
                        processNode((AstNode) childNode, null, suite, fullPath);
                    }
                }
            }
        }
    }

    private static void buildTest(AstNode node, ConfigurationInternal configurationInternal, ConfigurationSuiteInternal parentSuite, StringLiteral firstArg) {
        var testCase = new ConfigurationTestInternal();
        testCase.setTitle(firstArg.getValue());

        extractIdentifiersAndVariables(node, testCase);

        if (parentSuite == null && configurationInternal != null) {
            configurationInternal.getTests().add(testCase);
        } else if (parentSuite != null) {
            parentSuite.getTests().add(testCase);
        }
    }

    private static void extractIdentifiersAndVariables(AstNode node, ConfigurationTestInternal testCase) {
        if (node instanceof ExpressionStatement) {
            var expression = ((ExpressionStatement) node).getExpression();
            if (expression instanceof FunctionCall functionCall && functionCall.getArguments().size() > 1) {
                var metadataNode = functionCall.getArguments().get(1);
                if (metadataNode instanceof ObjectLiteral) {
                    var properties = ((ObjectLiteral) metadataNode).getElements();
                    for (ObjectProperty property : properties) {
                        if (property.getLeft() instanceof Name name) {
                            if ("identifiers".equals(name.getIdentifier())) {
                                var value = property.getRight();
                                if (value instanceof ArrayLiteral xrayIds) {
                                    for (AstNode idNode : xrayIds.getElements()) {
                                        if (idNode instanceof StringLiteral) {
                                            var xrayId = ((StringLiteral) idNode).getValue();
                                            testCase.getIdentifiers().add(xrayId);
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

    private static void extractVariables(AstNode node, ConfigurationSuiteInternal testCase) {
        if (node instanceof ExpressionStatement) {
            var expression = ((ExpressionStatement) node).getExpression();
            if (expression instanceof FunctionCall functionCall && functionCall.getArguments().size() > 1) {
                var metadataNode = functionCall.getArguments().get(1);
                if (metadataNode instanceof ObjectLiteral) {
                    var properties = ((ObjectLiteral) metadataNode).getElements();
                    for (ObjectProperty property : properties) {
                        if (property.getLeft() instanceof Name name) {
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
}

