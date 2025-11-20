FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

# Install tools
RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*

# Install JMeter
RUN wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.2.zip \
 && unzip apache-jmeter-5.6.2.zip \
 && mv apache-jmeter-5.6.2 /opt/jmeter \
 && rm apache-jmeter-5.6.2.zip

ENV JMETER_HOME=/opt/jmeter
ENV PATH=$JMETER_HOME/bin:$PATH

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Create directories with proper permissions
RUN mkdir -p /app/target /app/allure-results && \
    chmod -R 777 /app/target /app/allure-results

CMD ["mvn", "test"]