package fr.njj.galaxion.endtoendtesting.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationInternalMapperTest {

    @Test
    void build_withAllParams() throws IOException {
        var content =
                "describe('Suite1', {variables: ['variableSuite1']}, function() { " +
                "  it('Test1', {identifiers: ['identifier1'], variables: ['variableTest1']}, function() { /* test code */ });" +
                "  it('Test2', function() { /* test code */ });" +
                "});";

        var result = ConfigurationInternalMapper.build(content, "fullPath");

        assertEquals(1, result.getSuites().size());
        var suite = result.getSuites().get(0);
        assertEquals("Suite1", suite.getTitle());
        assertFalse(suite.getVariables().isEmpty());
        assertEquals("variableSuite1", suite.getVariables().get(0));

        assertEquals(2, suite.getTests().size());

        var test1 = suite.getTests().get(0);
        assertEquals("Test1", test1.getTitle());
        assertFalse(test1.getVariables().isEmpty());
        assertFalse(test1.getIdentifiers().isEmpty());
        assertEquals("identifier1", test1.getIdentifiers().get(0));
        assertEquals("variableTest1", test1.getVariables().get(0));

        var test2 = suite.getTests().get(1);
        assertEquals("Test2", test2.getTitle());
        assertTrue(test2.getVariables().isEmpty());
        assertTrue(test2.getIdentifiers().isEmpty());
    }

    @Test
    void build_withAllParamsDifferentOrder() throws IOException {
        var content =
                "describe('Suite1', function() { " +
                "  it('Test1', {variables: ['variableTest1'], identifiers: ['identifier1']}, function() { /* test code */ });" +
                "  it('Test2', function() { /* test code */ });" +
                "});";

        var result = ConfigurationInternalMapper.build(content, "fullPath");

        assertEquals(1, result.getSuites().size());
        var suite = result.getSuites().get(0);
        assertEquals("Suite1", suite.getTitle());
        assertTrue(suite.getVariables().isEmpty());

        assertEquals(2, suite.getTests().size());

        var test1 = suite.getTests().get(0);
        assertEquals("Test1", test1.getTitle());
        assertFalse(test1.getVariables().isEmpty());
        assertFalse(test1.getIdentifiers().isEmpty());
        assertEquals("identifier1", test1.getIdentifiers().get(0));
        assertEquals("variableTest1", test1.getVariables().get(0));

        var test2 = suite.getTests().get(1);
        assertEquals("Test2", test2.getTitle());
        assertTrue(test2.getVariables().isEmpty());
        assertTrue(test2.getIdentifiers().isEmpty());
    }
}
