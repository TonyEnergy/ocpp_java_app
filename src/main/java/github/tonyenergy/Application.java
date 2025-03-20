package github.tonyenergy;

/**
 * @Author: liutaohua
 * @Date: ${DATE}
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("🚀 WebSocket 服务器已启动！");
    }
}
