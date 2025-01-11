// use group-for-parallelization-1000
describe("Suite 1", {tags: ["suitetag1", "suitetag2"], variables: ["suitevariable1", "suitevariable2"] }, function () {
    it("Test 1", { tags: ["testtag1"] }, function () {
        // ...
    });

    it("Test 2", { variables: ["testvariable1"] }, function () {
        // ...
    });

    it("Test 3", { tags: ["testtag1", "testtag2"], variables: ["testvariable1", "testvariable2"] }, function () {
        // ...
    });

    it("Test 4", { tags: ["disable-on-e2e-testing-manager"]}, function () {
        // ...
    });
});
