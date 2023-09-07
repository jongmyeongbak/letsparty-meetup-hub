package com.letsparty.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.amazonaws.util.IOUtils;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.letsparty.dto.UploadFileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileS3ServiceImpl implements FileService {

	private final AmazonS3 s3Client;
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;
	@Value("${s3.path.files}")
	private String filesPath;
	
	@Override
	public UploadFileResponse upload(String path, MultipartFile file) throws IOException {
		return uploadOnS3(path, file);
	}

	// 코드 기반: https://docs.aws.amazon.com/ko_kr/AmazonS3/latest/userguide/mpu-upload-object.html
	private UploadFileResponse uploadOnS3(String keyPath, MultipartFile file) throws IOException {
		UploadFileResponse uploadFileResponse = null;

		try {
			final TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3Client).build();
			final String NanoId = NanoIdUtils.randomNanoId();
			final String key = keyPath + NanoId;
			final PutObjectRequest request =
					new PutObjectRequest(bucketName, key, file.getInputStream(), getMetadataValue(file));
			
			// TrnasferManager의 모든 전송은 비동기임. 따라서 이 요청은 즉시 반환된다.
			final Upload upload = tm.upload(request);
			log.info("Object 업로드 시작 -> {}({},{})", key, file.getContentType(), file.getSize());
			
			// 업로드가 완료되기를 기다림
			UploadResult result = upload.waitForUploadResult();
			if (upload.getState().name().equals("Completed")) {
				log.info("Object 업로드 성공 -> {}", file.getOriginalFilename());
				uploadFileResponse = new UploadFileResponse(
						file.getOriginalFilename(),
						NanoId,
						result.getETag(),
						getMetadataValue(file).getContentType(),
						file.getSize());
			} else {
				log.info("Object 업로드 실패 -> {}", file.getOriginalFilename());
				new Exception("Upload fail");
			}
		} catch (AmazonServiceException e) {
			// 요청 전송을 성공했지만, S3가 처리하지 못함
			log.error("amazonServiceException -> {}", e);
		} catch (SdkClientException e) {
			// S3에 응답을 달라고 연결할 수 없거나, 클라이언트가 S3의 응답 구문을 분석하지 못함
			log.error("amazonClientException -> {}", e);
		} catch (AmazonClientException e) {
			// 자바 클라이언트 코드 내 문제
			log.error("amazonClientException -> {}", e);
		} catch (InterruptedException e) {
			log.error("InterruptedException -> {}", e);
		}
		return uploadFileResponse;
	}

	@Override
	public byte[] download(String keyName) throws IOException {
		byte[] content;
		final S3Object s3Object = s3Client.getObject(bucketName, keyName);
		final S3ObjectInputStream stream = s3Object.getObjectContent();
		try {
			content = IOUtils.toByteArray(stream);
			s3Object.close();
		} catch (final IOException ex) {
			throw new IOException("IO Error Message: " + ex.getMessage());
		}
		return content;
	}
	
	public String getPreSignedURLForAttachedFile(String key, String OriginalName) {
		ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides()
				.withContentDisposition("attachment; filename=\"" + urlEncode(OriginalName) + "\"");
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
				new GeneratePresignedUrlRequest(bucketName, filesPath + key, HttpMethod.GET)
				.withExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(2))))
				.withResponseHeaders(responseHeaders);
		String presignedURL = s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
		log.info("presigned URL: {}", presignedURL);
		
		return presignedURL;
	}

	private ObjectMetadata getMetadataValue(MultipartFile file) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(file.getContentType());
		objectMetadata.setContentLength(file.getSize());
		return objectMetadata;
	}
	
	private String urlEncode(String value) {
	    try {
	        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("UTF-8 encoding not supported", e);
	    }
	}
}
