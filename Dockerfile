FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

# Install tools: wget + unzip for JMeter
RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*

# Install JMeter
RUN wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.2.zip \
 && unzip apache-jmeter-5.6.2.zip \
 && mv apache-jmeter-5.6.2 /opt/jmeter \
 && rm apache-jmeter-5.6.2.zip

ENV JMETER_HOME=/opt/jmeter
ENV PATH=$JMETER_HOME/bin:$PATH

# Install OWASP ZAP CLI
RUN wget https://github.com/zaproxy/zaproxy/releases/download/v2.14.0/ZAP_2_14_0_unix.sh \
 && chmod +x ZAP_2_14_0_unix.sh \
 && ./ZAP_2_14_0_unix.sh -q \
 && rm ZAP_2_14_0_unix.sh

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src
RUN mkdir -p allure-results

CMD ["mvn", "clean", "test"]
