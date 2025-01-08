# E2E Test Manager

E2E Test Manager est une application conçue pour orchestrer l'execution de tests E2E.
Elle permet de centraliser les tests, de gérer les environnements, d'exécuter des tests individuels ou groupés, de planifier des tests, de visualiser des tableaux de bord en temps réel et de consulter l'historique des exécutions.

## 👁️ Aperçu

TODO : Add video

## Compatibilité des outils

| **E2E Test Tool** | **Disponible** |
|-------------------|----------------|
| Cypress           | ✅              |
| Playwright        | ❌              |
| Selenium          | ❌              |

| **CI/CD Tool** | **Disponible** |
|----------------|----------------|
| GitLab         | ✅              |
| GitHub         | ❌              |
| Jenkins        | ❌              |

L'architecture du projet a été faite de sorte à normalement pouvoir facilement implémenter les autres outils de tests E2E et CI/CD.

## 🔧 Prerequisites

- Docker
- PostgreSQL
- OIDC Provider like Keycloak
- Ton projet de test e2e sur un repo git.
- Un outil de CI/CD permettant de lancer des pipelines et une API KEY pour l'utiliser.

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
business.scheduler.worker.report.verification.interval-minutes=5
business.scheduler.worker.report.cancel-timeout.interval-minutes=60
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
  - testFilter

default:
  interruptible: true
  tags:
    - openstack
  cache:
    key: node-cache
    paths:
      - node_modules/

cypress:
  stage: testFilter
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

## ❗Troobleshooting