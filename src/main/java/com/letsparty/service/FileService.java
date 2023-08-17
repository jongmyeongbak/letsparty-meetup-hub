package com.letsparty.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.letsparty.dto.UploadFileResponse;

// 로컬, 클라우드 등 여러 구현 방법을 고려한 인터페이스
public interface FileService {

	byte[] download(String keyName) throws IOException;
	UploadFileResponse upload(String path, MultipartFile file) throws IOException;
	default UploadFileResponse uploadByPath(String path, MultipartFile file) throws IOException {
		switch (path) {
		case "file":
			return upload("files/", file);
		case "media":
			return upload("media/", file);
		case "cover":
			return upload("images/covers/", file);
		case "profile":
			return upload("images/profiles/", file);
		case "testparty":
			return upload("testparty/", file);
		default:
			throw new IllegalArgumentException();
		}
	}
}
