FROM openjdk:17

WORKDIR /app

# Copy source files
COPY src /app/src

# Compile Java files
RUN find src -name "*.java" -type f > javafiles.txt && \
    javac -cp "src" -d bin -sourcepath src @javafiles.txt && \
    rm javafiles.txt

# Copy sample files
COPY sample_file /app/sample_file

# Run the application
CMD ["java", "-cp", "bin", "MainApp"]

