# ##############################################################################
# Service commands
# ##############################################################################

package:
	mvn package

install_skip_test:
	mvn clean install -DskipTests

run_quarkus:
	java -Xmx256m -Xms256m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=60005 -jar target/quarkus-app/quarkus-run.jar -Dprocess.name=Test_Quarkus

build:
	docker build -f Dockerfile -t itsffr-docker/tools/e2e-testing-manager-api:1.0.0 --platform=linux/amd64 .

tag:
	docker tag itsffr-docker/tools/e2e-testing-manager-api:1.0.0 jfrog-artifactory.steelhome.internal/itsffr-docker/tools/e2e-testing-manager-api:1.0.0

push:
	docker push jfrog-artifactory.steelhome.internal/itsffr-docker/tools/e2e-testing-manager-api:1.0.0

run_docker:
	docker run -i --rm -p 60000:60000 --name cypress-api  itsffr-docker/tools/e2e-testing-manager-api:1.0.0

release-start:
	mvn com.amashchenko.maven.plugin:gitflow-maven-plugin:1.21.0:release-start -DskipTestProject -DuseSnapshotInRelease -DcommitDevelopmentVersionAtStart -DpushRemote -DversionDigitToIncrement=1 -DproductionBranch=main

release-finish:
	mvn com.amashchenko.maven.plugin:gitflow-maven-plugin:1.21.0:release-finish -DskipTestProject -DuseSnapshotInRelease -DcommitDevelopmentVersionAtStart -DversionDigitToIncrement=1 -DproductionBranch=main