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
	docker build -f Dockerfile -t galaxion/e2e-testing-manager:0.5.0 --platform=linux/amd64 .

tag:
	docker tag galaxion/e2e-testing-manager:0.5.0 nexus.itsf.io:5004/galaxion/e2e-testing-manager:0.5.0

push:
	docker push nexus.itsf.io:5004/galaxion/e2e-testing-manager:0.5.0

run_docker:
	docker run -i --rm -p 60000:60000 --name cypress-api  galaxion/e2e-testing-manager:0.5.0
