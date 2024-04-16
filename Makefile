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
	docker build -f Dockerfile -t itsffr/tools/e2e-testing-manager-api:0.7.0 --platform=linux/amd64 .

tag:
	docker tag itsffr/tools/e2e-testing-manager-api:0.7.0 nexus.itsf.io:5004/itsffr/tools/e2e-testing-manager-api:0.7.0

push:
	docker push nexus.itsf.io:5004/itsffr/tools/e2e-testing-manager-api:0.7.0

run_docker:
	docker run -i --rm -p 60000:60000 --name cypress-api  itsffr/tools/e2e-testing-manager-api:0.7.0
