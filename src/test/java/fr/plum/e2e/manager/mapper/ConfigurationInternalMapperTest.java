// package fr.plum.e2e.manager.mapper;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
// import fr.plum.e2e.OLD.mapper.ConfigurationInternalMapper;
// import org.junit.jupiter.api.Test;
//
// class ConfigurationInternalMapperTest {
//
//  @Test
//  void build_withAllParams() {
//    var content =
//        "//use group-for-parallelization-1 "
//            + System.lineSeparator()
//            + "describe('Suite1', {variables: ['variableSuite1'], tags: ['tagSuite1']}, function()
// { "
//            + "  it('Test1', {tags: ['tagTest1'], variables: ['variableTest1']}, function() { /*
// testFilter code */ });"
//            + "  it('Test2', function() { /* testFilter code */ });"
//            + "});";
//
//    var result = ConfigurationInternalMapper.build(content, "fullPath");
//
//    assertEquals(1, result.getSuites().size());
//    var suiteFilter = result.getSuites().getFirst();
//    assertEquals("Suite1", suiteFilter.getTitle());
//    assertFalse(suiteFilter.getVariables().isEmpty());
//    assertEquals("tagSuite1", suiteFilter.getTags().getFirst());
//    assertEquals("variableSuite1", suiteFilter.getVariables().getFirst());
//
//    assertEquals(2, suiteFilter.getTests().size());
//
//    var test1 = suiteFilter.getTests().getFirst();
//    assertEquals("Test1", test1.getTitle());
//    assertFalse(test1.getVariables().isEmpty());
//    assertFalse(test1.getTags().isEmpty());
//    assertEquals("tagTest1", test1.getTags().getFirst());
//    assertEquals("variableTest1", test1.getVariables().getFirst());
//
//    var test2 = suiteFilter.getTests().get(1);
//    assertEquals("Test2", test2.getTitle());
//    assertTrue(test2.getVariables().isEmpty());
//    assertTrue(test2.getTags().isEmpty());
//
//    assertEquals("1", result.getGroup());
//  }
//
//  @Test
//  void build_withAllParamsDifferentOrder() {
//    var content =
//        "// use group-for-parallelization-0040"
//            + System.lineSeparator()
//            + "describe('Suite1', function() { "
//            + "  it('Test1', {tags: ['tag1'], variables: ['variableTest1']}, function() { /*
// testFilter
// code */ });"
//            + "  it('Test2', function() { /* testFilter code */ });"
//            + "  it('Test3', function() { /* testFilter code */ });"
//            + "  it('Test4', function() { /* testFilter code */ });"
//            + "  it('Test5', function() { /* testFilter code */ });"
//            + "  it('Test6', function() { /* testFilter code */ });"
//            + "  it('Test7', function() { /* testFilter code */ });"
//            + "});";
//
//    var result = ConfigurationInternalMapper.build(content, "fullPath");
//
//    assertEquals(1, result.getSuites().size());
//    var suiteFilter = result.getSuites().getFirst();
//    assertEquals("Suite1", suiteFilter.getTitle());
//    assertTrue(suiteFilter.getVariables().isEmpty());
//
//    assertEquals(7, suiteFilter.getTests().size());
//
//    var test1 = suiteFilter.getTests().getFirst();
//    assertEquals("Test1", test1.getTitle());
//    assertFalse(test1.getVariables().isEmpty());
//    assertFalse(test1.getTags().isEmpty());
//    assertEquals("tag1", test1.getTags().getFirst());
//    assertEquals("variableTest1", test1.getVariables().getFirst());
//
//    var test2 = suiteFilter.getTests().get(1);
//    assertEquals("Test2", test2.getTitle());
//    assertTrue(test2.getVariables().isEmpty());
//    assertTrue(test2.getTags().isEmpty());
//
//    var test3 = suiteFilter.getTests().get(2);
//    assertEquals("Test3", test3.getTitle());
//
//    var test4 = suiteFilter.getTests().get(3);
//    assertEquals("Test4", test4.getTitle());
//
//    var test5 = suiteFilter.getTests().get(4);
//    assertEquals("Test5", test5.getTitle());
//
//    var test6 = suiteFilter.getTests().get(5);
//    assertEquals("Test6", test6.getTitle());
//
//    var test7 = suiteFilter.getTests().get(6);
//    assertEquals("Test7", test7.getTitle());
//
//    assertEquals("0040", result.getGroup());
//  }
//
//  @Test
//  void build_withDisableTest() {
//    var content =
//        "/* use group-for-parallelization-0040 */"
//            + System.lineSeparator()
//            + "describe('Suite1', function() { "
//            + "  it('Test1', {tags: ['testTag1']}, function() { /* testFilter code */ });"
//            + "  it('Test2', {variables: ['testVariable1'], tags:
// ['disable-on-e2e-testing-manager']}, function() { /* testFilter code */ });"
//            + "});";
//
//    var result = ConfigurationInternalMapper.build(content, "fullPath");
//
//    assertEquals(1, result.getSuites().size());
//    var suiteFilter = result.getSuites().getFirst();
//    assertEquals("Suite1", suiteFilter.getTitle());
//
//    assertEquals(1, suiteFilter.getTests().size());
//
//    var test1 = suiteFilter.getTests().getFirst();
//    assertEquals("Test1", test1.getTitle());
//    assertTrue(test1.getVariables().isEmpty());
//    assertFalse(test1.getTags().isEmpty());
//    assertEquals("testTag1", test1.getTags().getFirst());
//
//    assertEquals("0040", result.getGroup());
//  }
//
//  @Test
//  void build_withDisableSuite() {
//    var content =
//        "// group-for-parallelization-aasa"
//            + System.lineSeparator()
//            + "describe('Suite1', {tags: ['disable-on-e2e-testing-manager']}, function() { "
//            + "  it('Test1', {tags: ['testTag1']}, function() { /* testFilter code */ });"
//            + "});";
//
//    var result = ConfigurationInternalMapper.build(content, "fullPath");
//
//    assertTrue(result.getSuites().isEmpty());
//    assertNull(result.getGroup());
//  }
// }
