#!/bin/bash

# Ensure the script stops on errors
set -e

# Check if Gradle wrapper exists
if [ ! -f "./gradlew" ]; then
  echo "Gradle wrapper not found. Please ensure you're in the project directory with a valid Gradle setup."
  exit 1
fi

# Clean and build the application
echo "Building the application..."
./gradlew clean build

# Navigate to the build directory
JAR_FILE=$(find build/libs -name "*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "Error: No JAR file found in build/libs. Make sure your Gradle build produces a JAR."
  exit 1
fi

# Run the application
echo "Running the application..."
java -jar "$JAR_FILE"
