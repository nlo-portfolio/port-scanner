![Port Scanner](https://raw.githubusercontent.com/nlo-portfolio/nlo-portfolio.github.io/master/style/images/programs/port-scanner.png "Port Scanner")

## Description ##

PortScanner is a simple Java application for determining if network ports are open for a list of hosts. It takes command-line arguments allowing users to specify both individual and a range of hosts/ports.

## Dependencies ##

Ubuntu<br>
Java v14+<br>
Apache Commons CLI<br>
JUnit5 (tests)<br>
\* All required components are included in the provided Docker image.

## Usage ##

Hosts and ports can be specified individually or with a hyphen to denote a range.<br>
Example: `javac ... PortScanner --hosts=127.0.0.1-127.0.0.4,127.0.0.7 --ports=80,443,8000-8005,9999`.<br>
Enter `--help` to see a list of commands.<br>

Ubuntu:

&emsp;Application:

```
javac -d src/main/bin --class-path src/main/resources/commons-cli-1.4.jar src/main/java/*.java
java --class-path src/main/bin:src/main/resources/commons-cli-1.4.jar PortScanner --hosts=<args> --ports=<args>
```

&emsp;Test:

```
javac -d src/test/bin --class-path src/test/bin:src/test/resources/junit-platform-console-standalone-1.7.2.jar:src/main/resources/commons-cli-1.4.jar src/main/java/*.java src/test/java/*.java
java -jar src/test/resources/junit-platform-console-standalone-1.7.2.jar --class-path src/test/bin:src/main/resources/commons-cli-1.4.jar --scan-class-path
```

<br>

Docker:

```
docker-compose build
docker-compose run <port-scanner --hosts=<args> --ports=<args>  |  test>
```
