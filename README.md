# E2E Test Manager

A comprehensive end-to-end (E2E) test management application that integrates with Cypress and GitLab CI. It provides an intuitive user interface to organize, execute, and monitor E2E tests efficiently.

## 🎯 Features

- Intuitive UI for test management
- Test environments management
- Individual or grouped test execution
- Parallel execution support
- Test scheduling with integrated calendar
- Real-time dashboards and metrics
- Detailed execution history
- Native GitLab integration

## 🔧 Prerequisites

- JDK 17+
- Maven
- PostgreSQL
- GitLab instance with API access
- Cypress project (see configuration below)

## ⚙️ Setup

### 1. Database Configuration

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=your_username
quarkus.datasource.password=your_password
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/e2e_manager
```

### 2. GitLab Configuration

```properties
gitlab.baseUrl=https://your-gitlab-instance/api/v4
workerUnit.max.in.parallel=5
gitlab.old-pipeline-to-verify-in-minutes=5
gitlab.old-pipeline-to-cancel-in-minutes=60
```

## 🚀 Cypress Project Configuration

### 1. Required Structure

```
your-cypress-project/
├── cypress/
│   ├── e2e/
│   │   └── tests/
│   │       ├── suite1/
│   │       │   └── test1.cy.ts
│   │       └── suite2/
│   │           └── test2.cy.ts
│   ├── screenshots/
│   ├── videos/
│   └── results/
└── .gitlab-ci.yml
```

### 2. GitLab CI Configuration

Add the following `.gitlab-ci.yml` to your Cypress project:

```yaml
stages:
  - test

default:
  interruptible: true
  tags:
    - openstack
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
    - npm config set //jfrog-artifactory.steelhome.publisher/artifactory/api/npm/galaxion-npm/:_auth $NPM_AUTH -L project
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
      - cypress/screenshots/
      - cypress/videos/
      - cypress/results/
```

### 3. Cypress Test Format

Tests should be written using the following format to ensure proper integration:

```typescript
describe('Suite Example', { tags: ['@smoke', '@priority-1'] }, () => {
  it('Test Example', { tags: ['@feature-login'] }, () => {
    // Test code
  });
});
```

## 📦 Environment Variables

### GitLab CI Variables

| Variable               | Description                     | Required |
|------------------------|---------------------------------|----------|
| CYPRESS_VARIABLES      | Environment variables for tests | Yes      |
| CYPRESS_VIDEO          | Enable/disable video recording  | Yes      |
| CYPRESS_TEST_SPEC      | Specific test path to run       | No       |
| CYPRESS_TEST_GREP      | Filter for specific tests       | No       |
| CYPRESS_TEST_GREP_TAGS | Filter by tags                  | No       |
| CYPRESS_TEST_ENABLED   | Enables test execution          | Yes      |

## 🚀 Installation

1. Clone the repository
2. Configure the database
3. Configure GitLab integration
4. Start the application:

```bash
./mvnw quarkus:dev
```

The application will be available at `http://localhost:60000`

## 📄 License

This project is licensed under the GNU Affero General Public License with additional terms - see the [LICENSE.md](LICENSE.md) file for details.

The AGPL license ensures that:

- The software remains free and open source
- Modifications must be shared with the community
- Users have access to the source code
- Original developers retain commercial support rights

For commercial support or development services, please contact maxprunier@gmail.com