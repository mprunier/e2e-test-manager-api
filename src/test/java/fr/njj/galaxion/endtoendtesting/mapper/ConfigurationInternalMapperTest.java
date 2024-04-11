package fr.njj.galaxion.endtoendtesting.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationInternalMapperTest {

    @Test
    void build_withAllParams() {
        var content =
                "describe('Suite1', {variables: ['variableSuite1']}, function() { " +
                "  it('Test1', {identifiers: ['identifier1'], variables: ['variableTest1']}, function() { /* test code */ });" +
                "  it('Test2', function() { /* test code */ });" +
                "});";

        var result = ConfigurationInternalMapper.build(content, "fullPath");

        assertEquals(1, result.getSuites().size());
        var suite = result.getSuites().getFirst();
        assertEquals("Suite1", suite.getTitle());
        assertFalse(suite.getVariables().isEmpty());
        assertEquals("variableSuite1", suite.getVariables().getFirst());

        assertEquals(2, suite.getTests().size());

        var test1 = suite.getTests().getFirst();
        assertEquals("Test1", test1.getTitle());
        assertFalse(test1.getVariables().isEmpty());
        assertFalse(test1.getIdentifiers().isEmpty());
        assertEquals("identifier1", test1.getIdentifiers().getFirst());
        assertEquals("variableTest1", test1.getVariables().getFirst());

        var test2 = suite.getTests().get(1);
        assertEquals("Test2", test2.getTitle());
        assertTrue(test2.getVariables().isEmpty());
        assertTrue(test2.getIdentifiers().isEmpty());
    }

    @Test
    void build_withAllParamsDifferentOrder() {
        var content =
                "describe('Suite1', function() { " +
                "  it('Test1', {variables: ['variableTest1'], identifiers: ['identifier1']}, function() { /* test code */ });" +
                "  it('Test2', function() { /* test code */ });" +
                "});";

        var result = ConfigurationInternalMapper.build(content, "fullPath");

        assertEquals(1, result.getSuites().size());
        var suite = result.getSuites().getFirst();
        assertEquals("Suite1", suite.getTitle());
        assertTrue(suite.getVariables().isEmpty());

        assertEquals(2, suite.getTests().size());

        var test1 = suite.getTests().getFirst();
        assertEquals("Test1", test1.getTitle());
        assertFalse(test1.getVariables().isEmpty());
        assertFalse(test1.getIdentifiers().isEmpty());
        assertEquals("identifier1", test1.getIdentifiers().getFirst());
        assertEquals("variableTest1", test1.getVariables().getFirst());

        var test2 = suite.getTests().get(1);
        assertEquals("Test2", test2.getTitle());
        assertTrue(test2.getVariables().isEmpty());
        assertTrue(test2.getIdentifiers().isEmpty());
    }
}
