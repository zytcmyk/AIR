package com.airquality.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 */
@Component
public class AliOSSUtils {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    /**
     * 上传文件到OSS，返回带签名的访问URL
     *
     * @param file 要上传的文件
     * @return 带签名的文件访问URL（有效期10年）
     * @throws IOException IO异常
     */
    public String upload(MultipartFile file) throws IOException {
        // 获取上传文件的输入流
        InputStream inputStream = file.getInputStream();

        // 生成唯一文件名，避免文件覆盖
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 设置文件元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());

        // 创建上传请求
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileName, inputStream);
        putRequest.setMetadata(metadata);

        // 上传文件到OSS
        ossClient.putObject(putRequest);

        // 生成带签名的URL，设置有效期（10年，足够长）
        Date expiration = new Date(System.currentTimeMillis() + 10 * 365 * 24 * 60 * 60 * 1000L);
        URL signedUrl = ossClient.generatePresignedUrl(bucketName, fileName, expiration);

        // 关闭OSS客户端
        ossClient.shutdown();

        return signedUrl.toString();
    }
}
