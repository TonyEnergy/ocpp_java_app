# 使用 OpenJDK 8 作为基础镜像
FROM openjdk:8-jdk-alpine

# 安装 Maven
RUN apk add --no-cache maven

# 设置工作目录
WORKDIR /app

# 复制项目的所有文件到容器
COPY . .

# 运行 Maven 构建项目
RUN mvn clean package -DskipTests

# 设置环境变量，指定活动的 Spring 配置文件
ENV SPRING_PROFILES_ACTIVE=prod

# 运行 Java 应用
CMD ["java", "-jar", "target/ocpp_java_app-1.0-SNAPSHOT.jar"]
