package github.tonyenergy.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import com.aliyun.oss.model.OSSObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Use for server chan api interface
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
@RestController
@RequestMapping("/logs")
@Slf4j
public class LogController {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void init() {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }


    /**
     * get log file through OSS
     */
    @GetMapping("/{fileName}")
    public String getLogFileContent(@PathVariable String fileName) {
        // create OSSClient
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // Get log file object
            OSSObject ossObject = ossClient.getObject(new GetObjectRequest(bucketName, fileName));
            InputStream inputStream = ossObject.getObjectContent();
            // Read file content
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Read OSS log file failed: {}", e.getMessage(), e);
            return "Can't read log file: " + e.getMessage();
        } finally {
            // Close OSSClient
            ossClient.shutdown();
        }
    }
}
