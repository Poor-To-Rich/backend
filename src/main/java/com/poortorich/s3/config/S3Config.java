package com.poortorich.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.poortorich.s3.constans.S3Constants.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value(Config.BUCKET_NAME)
    private String bucketName;

    @Value(Config.REGION)
    private String region;

    @Value(Config.ACCESS_KEY)
    private String accessKey;

    @Value(Config.SECRET_KEY)
    private String secretKey;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        return (AmazonS3Client) AmazonS3ClientBuilder.standard().withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }
}
