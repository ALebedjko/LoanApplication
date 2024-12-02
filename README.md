# Project Name

## Project Description
Provide a brief description of your project, including its purpose, features, and any unique aspects.

## Technologies Used
- **Java**: Version 18
- **Jakarta EE**: List the specific Jakarta EE components used
- **Spring Data JPA**
- **Spring MVC**
- **Lombok**
- **Docker**
- **Other technologies or frameworks**: List any other technologies/frameworks used

## Prerequisites
- **Java SDK**: Version 18
- **Gradle**: The project uses the Gradle wrapper, so no local installation is required.
- **Docker**: Ensure Docker is installed and running.
- **IDE**: IntelliJ IDEA 2024.2.4, Ultimate Edition is recommended

## Setup Instructions

1. **Clone the repository**
    ```bash
    git clone <repository-url>
    cd <project-directory>
    ```

2. **Configure the database**
  - Ensure your database configuration is correct in `application.properties` or `application.yml`.

4. **Start Database in Docker**
    ```bash
    docker-compose up -d
    ```

3. **Build the project**
    ```bash
    ./gradlew clean build
    ```

5. **Access the application**
  - Open your browser and navigate to `http://localhost:<port-number>`

## Swagger API Documentation

Swagger UI is integrated for easy exploration and testing of RESTful APIs.

- **Access Swagger UI:**
   - Navigate to `http://localhost:<port>/swagger-ui/index.html` to view and interact with the API documentation.
