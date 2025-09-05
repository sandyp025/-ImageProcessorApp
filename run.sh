#!/bin/bash

# Advanced Image Processor - Startup Script
# This script provides multiple ways to run the JavaFX application on macOS

echo "🎨 Advanced Image Processor - Startup Options"
echo "=============================================="
echo ""

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
echo "Java Version: $JAVA_VERSION"
echo ""

# Get JavaFX module path
JAVAFX_PATH=$(find ~/.m2/repository/org/openjfx -name "*.jar" 2>/dev/null | head -1 | xargs dirname 2>/dev/null || echo "")

echo "Choose startup method:"
echo "1. Maven JavaFX Plugin (Default)"
echo "2. Direct Java with Module Path"  
echo "3. Java with Explicit JavaFX Modules"
echo "4. Build and run JAR"
echo ""
read -p "Enter choice [1-4]: " choice

case $choice in
    1)
        echo "🚀 Starting with Maven JavaFX Plugin..."
        export MAVEN_OPTS="-Xmx2G --enable-native-access=ALL-UNNAMED"
        ./mvnw clean compile
        ./mvnw javafx:run
        ;;
    2)  
        echo "🚀 Starting with Direct Java..."
        ./mvnw clean compile
        java --enable-native-access=ALL-UNNAMED \
             --module-path target/classes:$(ls ~/.m2/repository/org/openjfx/javafx-*/21/*.jar | tr '\n' ':' | sed 's/:$//') \
             --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing \
             -cp target/classes \
             com.my.app.HelloApplication
        ;;
    3)
        echo "🚀 Starting with Explicit JavaFX Modules..."
        ./mvnw clean compile
        java -Xmx2G \
             --enable-native-access=ALL-UNNAMED \
             --module-path ~/.m2/repository/org/openjfx/javafx-controls/21:~/.m2/repository/org/openjfx/javafx-graphics/21:~/.m2/repository/org/openjfx/javafx-base/21:~/.m2/repository/org/openjfx/javafx-fxml/21 \
             --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing \
             -cp target/classes \
             com.my.app.HelloApplication
        ;;
    4)
        echo "🚀 Building and running JAR..."
        ./mvnw clean package
        java -jar target/ImageProcessorApp-1.0-SNAPSHOT.jar
        ;;
    *)
        echo "Invalid choice. Using default Maven method..."
        export MAVEN_OPTS="-Xmx2G --enable-native-access=ALL-UNNAMED"
        ./mvnw clean compile javafx:run
        ;;
esac

echo ""
echo "✨ Thank you for using Advanced Image Processor!"
echo "Made in India ❤️ by Sandip Mandal"