package:
	mvn package

install:
	mvn clean install

install_skip_test:
	mvn clean install -DskipTests

run: install_skip_test
	java -Xmx512m -Xms512m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=60005 -jar target/quarkus-app/quarkus-run.jar -Dprocess.name=Test_Quarkus