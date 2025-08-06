FROM eclipse-temurin:17-jdk

WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy project files
COPY pom.xml .
COPY src src

RUN mvn clean package

# List files to debug
RUN ls -la target/ || echo "No target directory found"


# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "target/temperature-api-1.0.0.jar"]