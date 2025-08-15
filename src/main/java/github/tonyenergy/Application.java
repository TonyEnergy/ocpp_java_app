package github.tonyenergy;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * Application
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = "github.tonyenergy.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("🚀 WebSocket Server Start！");
    }
}
