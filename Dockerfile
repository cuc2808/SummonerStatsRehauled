# Start with Amazon Corretto 25 Alpine base image
FROM amazoncorretto:25-alpine

# Install curl on Alpine
RUN apk update && apk add --no-cache curl

# Copy the jar file into the image
COPY target/app.jar /app.jar

# Expose the port your app runs on
EXPOSE 7070

# Command to run your app
CMD ["java", "-jar", "/app.jar"]
