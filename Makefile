install:
	apt install -y openjdk-21-jdk
	apt install -y maven

run:
	mvn exec:java -Dexec.mainClass="dev.osunolimits.main.App"
	