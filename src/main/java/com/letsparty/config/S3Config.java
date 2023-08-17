package com.letsparty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;
	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;
	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public AmazonS3 s3Client() {
		// 빠른 구현을 위해 BasicAWSCredentials와 AWSStaticCredentialsProvider를 사용함
		//	- BasicAWSCredentials
		//		Credential key를 직접 입력함
		// 다른 방법으로 ProfileCredentialsProvider 등이 있음
		//	- 참고
		//		https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
		//		https://bkjeon1614.tistory.com/709
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		new ProfileCredentialsProvider();
		return AmazonS3ClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}
}
