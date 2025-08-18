package github.tonyenergy.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DbKeepAliveScheduler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Run every 5 minutes (300000 ms) to keep DB connections alive.
     * "fixedRate" means the next execution starts counting after the previous one begins.
     */
    @Scheduled(fixedRate = 300000)
    public void keepAlive() {
        try {
            jdbcTemplate.execute("SELECT 1");
            log.info(" DB keep-alive query executed successfully");
        } catch (Exception e) {
            log.error("🛑 DB keep-alive query failed", e);
        }
    }
}

