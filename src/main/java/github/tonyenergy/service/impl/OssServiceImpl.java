package github.tonyenergy.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import github.tonyenergy.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Oss service Impl
 *
 * @author Tony
 * @date 2025/5/14
 */
@Slf4j
@Service
public class OssServiceImpl implements OssService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public String uploadFile(String objectName, InputStream inputStream, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        ossClient.putObject(bucketName, objectName, inputStream, metadata);
        return getFileUrl(objectName);
    }

    @Override
    public boolean deleteFile(String objectName) {
        ossClient.deleteObject(bucketName, objectName);
        return true;
    }

    @Override
    public String getFileUrl(String objectName) {
        // will expire after 24 hours
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000 * 24);
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString();
    }

    @Override
    public boolean doesFileExist(String objectName) {
        return ossClient.doesObjectExist(bucketName, objectName);
    }

    @Override
    public InputStream downloadFile(String objectName) {
        OSSObject object = ossClient.getObject(bucketName, objectName);
        return object.getObjectContent();
    }

    @Override
    public List<String> listFiles(String prefix, String end) {
        List<String> fileNames = new ArrayList<>();
        ObjectListing listing = ossClient.listObjects(bucketName, prefix);
        for (OSSObjectSummary summary : listing.getObjectSummaries()) {
            String key = summary.getKey();
            if (key.endsWith(end)) {
                fileNames.add(key.substring(prefix.length()));
            }
        }
        return fileNames;
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
