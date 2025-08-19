# 使用更轻量的 JDK 基础镜像
FROM eclipse-temurin:8-jre-jammy as runtime

# 设置工作目录
WORKDIR /app

# 复制 Maven 打包产物（只复制 jar，而不是整个项目）
COPY target/ocpp_java_app-1.0-SNAPSHOT.jar app.jar

# 设置环境变量（Spring Profile）
ENV SPRING_PROFILES_ACTIVE=prod

# JVM 内存优化参数，避免 OOM
# -Xmx 限制最大堆内存
# -Xms 设置初始堆内存
# -XX:+UseContainerSupport 让 JVM 感知容器内存限制
# -XX:+UseSerialGC 小内存下更合适
ENV JAVA_OPTS="-Xms64m -Xmx350m -XX:+UseContainerSupport -XX:+UseSerialGC"

# 启动命令
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
