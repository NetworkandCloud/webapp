package com.example.assignment1.config;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AmazonConfig {

	@Value("${aws.region}")
	private String accessRegion;

	 @Value("${aws.accessKey}")
	 private String accessKey;
	 @Value("${aws.secretKey}")
	 private String accessSecret;

	@Bean
	public AmazonS3 amazonS3() {

		//AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard().build();
		//return amazonS3Client;
		AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, accessSecret);
		return AmazonS3ClientBuilder.standard().withRegion(accessRegion)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();

		//return AmazonS3ClientBuilder.standard().build();

	}
}