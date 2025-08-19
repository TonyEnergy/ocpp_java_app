# ====== 构建阶段 ======
FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /app

# 只复制 Maven 相关文件，利用缓存加快构建
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制项目源代码
COPY src ./src

# 打包生成 jar
RUN mvn clean package -DskipTests

# ====== 运行阶段 ======
FROM eclipse-temurin:8-jre-jammy AS runtime

WORKDIR /app

# 复制编译好的 jar
COPY --from=build /app/target/ocpp_java_app-1.0-SNAPSHOT.jar app.jar

# 设置 Spring Profile
ENV SPRING_PROFILES_ACTIVE=prod

# JVM 内存优化参数
ENV JAVA_OPTS="-Xms64m -Xmx300m -XX:+UseContainerSupport -XX:+UseSerialGC"

# 启动命令
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
