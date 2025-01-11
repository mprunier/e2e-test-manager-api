# E2E Test Manager API

E2E Test Manager is an application designed to orchestrate the execution of E2E tests.

It enables centralized test management, environment configuration, individual or group test execution, test scheduling, real-time dashboard visualization, and execution history tracking.

## 👁️ Overview

TODO: Add video

## 🔄 Tool Compatibility

| **E2E Test Tool** | **Available**          |
|-------------------|------------------------|
| Cypress           | ✅                      |
| Playwright        | ❌ (Not yet integrated) |

| **CI/CD Tool** | **Available**          |
|----------------|------------------------|
| GitLab         | ✅                      |
| GitHub         | ❌ (Not yet integrated) |

The project architecture has been designed to facilitate easy integration of additional E2E testing and CI/CD tools.

## 🔧 Prerequisites

- PostgreSQL
- OIDC Provider like Keycloak
- Your E2E test project in a git repository
- A CI/CD tool capable of running pipelines and an API KEY for accessing it
- The converter tool: https://github.com/mprunier/js-converter-api
- The user interface: https://github.com/mprunier/e2e-test-manager-ui

## ⚙️ Environment Variables

| **Variable**                                                       | **Description**                                                        | **Default value**                                  |
|--------------------------------------------------------------------|------------------------------------------------------------------------|----------------------------------------------------|
| `QUARKUS_HTTP_PORT`                                                | HTTP port used by the application                                      | `60000`                                            |
| `QUARKUS_DATASOURCE_JDBC_URL`                                      | PostgreSQL database JDBC URL                                           | `jdbc:postgresql://localhost:5432/cypress-manager` |
| `QUARKUS_DATASOURCE_USERNAME`                                      | Database connection username                                           | `mprunier`                                         |
| `QUARKUS_DATASOURCE_PASSWORD`                                      | Database connection password                                           | `azerty`                                           |
| `QUARKUS_OIDC_AUTH_SERVER_URL`                                     | OpenID Connect authentication server URL                               | `http://localhost:5555/realms/master`              |
| `QUARKUS_OIDC_CLIENT_ID`                                           | OIDC client ID                                                         | `e2e-testing-manager-api`                          |
| `QUARKUS_OIDC_CREDENTIALS_SECRET`                                  | OIDC authentication secret                                             | `E9SgMpWOvq0iS9gT5ZNnBIA82kgIgLQM`                 |
| `QUARKUS_REST_CLIENT_CONVERTER_URL`                                | Conversion service REST URL                                            | `http://localhost:3000`                            |
| `BUSINESS_SCHEDULER_WORKER_REPORT_VERIFICATION_CRON_EXPR`          | CRON expression for worker report verification                         | `0 0/5 * * * ?`                                    |
| `BUSINESS_SCHEDULER_WORKER_REPORT_CANCEL_TIMEOUT_INTERVAL_MINUTES` | Timeout interval (in minutes) before canceling a worker without report | `60`                                               |
| `BUSINESS_WORKER_MAX_UNIT_IN_PARALLEL`                             | Maximum number of work units that can be executed in parallel          | `10`                                               |

## 📋 Business Rules

### 1. Disabling a Test or Suite

Add the tag `disable-on-e2e-testing-manager` to the test or suite you want to disable.

For example, with Cypress, to disable a test:

```javascript
describe('Suite Example', () => {
  it('Test Example', { tags: ['disable-on-e2e-testing-manager'] }, () => {
    // Test code
  });
});
```

### 2. Grouping Tests for Parallelization

This feature allows you to group test files together. For example, if you have tests that are dependent on each other, you can group them to ensure they're executed in the same worker if 'BUSINESS_WORKER_MAX_UNIT_IN_PARALLEL' is greater than 1.

Add the comment `use group-for-parallelization-XXXX` as the first comment in the file, where XXXX is your group name using decimals.

```javascript
// use group-for-parallelization-1030
describe('Suite Example', () => {
  it('Test Example', () => {
    ...
  });
});
```

### 3. Running a Suite or Test with Variables

This feature allows you to use environment variables for a test or suite. For example, if you want to run a test with a specific ID:

```javascript
describe("Your suite", function () {
    let orderReference;

    it("A basic test", function () {
       ...
       orderReference = "AZERTY";
       ...
    });

    it("Your test which need order reference",{variables: ["ORDER_REFERENCE"]},function () {
            if (!orderReference) {
                orderReference = Cypress.env("ORDER_REFERENCE");
                if (!orderReference) {
                    this.skip("No order reference found, skipping the test");
                }
            }
            ....
        },
    );
});
```

In this example, the second test depends on the first test. However, the tool allows running specific tests individually. If you want to run the second test without running the first one, you can pass the ORDER_REFERENCE variable in the environment variables.

While this approach might not always follow best practices (as tests should ideally be independent), it can be useful in certain cases to avoid creating excessive test data.

### 4. Reporting an ID

TODO: This feature allows reporting an ID in the test report.

## 🔌 Cypress + GitLab Integration

### 1. Required Structure

```
your-cypress-project/
├── cypress/
│   ├── e2e/
│   │   └── .../
│   │       ├── .../
│   │       │   └── test1.cy.ts
│   │       ├── .../
│   │       │   └── test2.cy.ts
│   │       └ ...
│   ├── screenshots/
│   ├── videos/
│   └── results/
└── .gitlab-ci.yml
```

The application does not support nested suites within a file's suite.

### 2. Required Cypress Dependencies

For the application to work correctly with Cypress, the following dependencies must be installed:

- "@cypress/grep" : https://github.com/cypress-io/cypress/tree/develop/npm/grep#readme
- "mochawesome" : https://github.com/adamgruber/mochawesome#readme
- "mochawesome-merge" : https://github.com/Antontelesh/mochawesome-merge#readme

Check documentation to configure these dependencies in your project.

### 3. GitLab CI Configuration

Add and adapt the following `.gitlab-ci.yml` to your Cypress project. (Keep the rules in the main script)

```yaml
stages:
  - test

default:
  interruptible: true
  cache:
    key: node-cache
    paths:
      - node_modules/

cypress:
  stage: test
  image:
    name: cypress/browsers:node-20.17.0-chrome-129.0.6668.70-1-ff-130.0.1-edge-129.0.2792.52-1
    entrypoint: [ "" ]
  before_script:
    - npm config set strict-ssl false
    - npm config set registry $NPM_REGISTRY_PUBLIC -L project
    - npm config set //your-npm-repository/:_auth $NPM_AUTH -L project
    - npm ci
  script:
    - |
      if [ -z "$CYPRESS_VARIABLES" ]; then
        echo "CYPRESS_VARIABLES is mandatory. Exiting the job."
        exit 1
      fi
      if [ -z "$CYPRESS_VIDEO" ]; then
        echo "CYPRESS_VIDEO is mandatory. Exiting the job."
        exit 1
      fi
      if [ -z "$CYPRESS_TEST_SPEC" ]; then
        echo "Run with all tests."
        echo "Grep Tags = $CYPRESS_TEST_GREP_TAGS"
        npx cypress run --browser chrome --env $CYPRESS_VARIABLES,grepTags="$CYPRESS_TEST_GREP_TAGS" --config video=$CYPRESS_VIDEO
      elif [ -z "$CYPRESS_TEST_GREP" ]; then
        echo "Run with specified tests."
        echo "Spec = $CYPRESS_TEST_SPEC"
        echo "Grep Tags = $CYPRESS_TEST_GREP_TAGS"
        npx cypress run --browser chrome --env $CYPRESS_VARIABLES,grepTags="$CYPRESS_TEST_GREP_TAGS" --spec $CYPRESS_TEST_SPEC --config video=$CYPRESS_VIDEO
      else
        echo "Run with specified tests and grep."
        echo "Spec = $CYPRESS_TEST_SPEC"
        echo "Grep = $CYPRESS_TEST_GREP"
        echo "Grep Tags = $CYPRESS_TEST_GREP_TAGS"
        npx cypress run --browser chrome --env $CYPRESS_VARIABLES,grep="$CYPRESS_TEST_GREP",grepTags="$CYPRESS_TEST_GREP_TAGS" --spec $CYPRESS_TEST_SPEC --config video=$CYPRESS_VIDEO
      fi
  after_script:
    - npx mochawesome-merge cypress/results/*.json > cypress/results/results.json
    - rm -rf ./cypress/results/mochawesome*
  rules:
    - if: $CYPRESS_TEST_ENABLED == "true"
  artifacts:
    when: always
    expire_in: 30 min
    paths:
      - /builds/...your-project.../cypress/screenshots/
      - /builds/...your-project.../cypress/videos/
      - /builds/...your-project.../cypress/results/
```

These variables are sent to the pipeline by the e2e-test-manager-api:

| Variable               | Description                                                          |
|------------------------|----------------------------------------------------------------------|
| CYPRESS_VARIABLES      | Environment variables for tests                                      |
| CYPRESS_VIDEO          | Enable/disable video recording                                       |
| CYPRESS_TEST_SPEC      | Specific test path to run                                            |
| CYPRESS_TEST_GREP      | Filter for specific tests                                            |
| CYPRESS_TEST_GREP_TAGS | Filter by tags                                                       |
| CYPRESS_TEST_ENABLED   | Enables test execution. For running this pipeline only with the tool |

### 4. Cypress Test Format

Tests should be written using the following format to ensure proper integration:

```typescript
// use group-for-parallelization-1030
describe('Suite Example', { tags: ['tag1', 'tag2'], variables: ["variable1", "variable2"] }, () => {
  it('Test Example', { tags: ['tag3'] }, () => {
    ...
  });
});
```

### 5. Add report command

In you Cypress project, add the following command to report an id or any other information:

```javascript
Cypress.Commands.add("addToReport", (context) => {
    cy.once("test:after:run", (test) => addContext({ test }, context));
});
```

And use this command in your tests:

```javascript
describe('Suite Example', () => {
  it('Test Example', () => {
    ...
    cy.addToReport({
        title: "orderId",
        value: "AZERTY",
    });
    ...
  });
});
```

## 🔄 Cypress + GitHub

Not implemented yet

## 🎭 Playwright + GitLab

Not implemented yet

## 🔄 Playwright + GitHub

Not implemented yet

## ❗Troubleshooting

### 1. Cypress - Screenshots and videos name too long

Sometimes screenshot and video filenames can be too long as they combine the filename + suite name + test name. A filename cannot exceed 255 characters.
These files are retrieved using their names.
If you have many suites or tests with long names, consider adding the following workaround in your Cypress project (e2e.js) to rename the files uniquely.

```javascript
function formatDate(date) {
    const pad = (num) => String(num).padStart(2, "0");
    return `${date.getFullYear()}${pad(date.getMonth() + 1)}${pad(date.getDate())}${pad(date.getHours())}${pad(date.getMinutes())}${pad(date.getSeconds())}`;
}

function getScreenshotInfo(test) {
    const screenshotName = formatDate(new Date());
    const screenshotPath = path.join("cypress", "screenshots", Cypress.spec.name, `${screenshotName}.png`);
    return { screenshotPath, screenshotName };
}

// Disable automatic screenshots on failure
Cypress.Screenshot.defaults({
    screenshotOnRunFailure: false,
    disableTimersAndAnimations: true,
});

afterEach(function () {
    if (this.currentTest && this.currentTest.state === "failed") {
        const { screenshotPath, screenshotName } = getScreenshotInfo(this.currentTest);

        cy.url().then((url) => {
            cy.addToReport({
                title: "urlError",
                value: url,
            });
        });

        cy.screenshot(screenshotName, { capture: "runner" });

        cy.addToReport({
            title: "screenshotError",
            value: screenshotPath,
        });
    }
});
```

(You need to add the command "addToReport" in your Cypress commands)