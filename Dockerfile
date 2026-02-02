# =========================
# 1️⃣ BUILD STAGE
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# Cache dependency
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source & build
COPY src ./src
RUN mvn clean package -DskipTests


# =========================
# 2️⃣ RUNTIME STAGE
# =========================
FROM eclipse-temurin:21-jre

# Security: không chạy bằng root
RUN useradd -ms /bin/bash avakids
USER avakids

WORKDIR /app

# Copy jar từ build stage
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

# JVM options chuẩn production
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "app.jar" \
]

