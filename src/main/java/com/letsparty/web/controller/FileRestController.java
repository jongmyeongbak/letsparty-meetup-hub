package com.letsparty.web.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letsparty.dto.UploadFileResponse;
import com.letsparty.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileRestController {

	private final FileService fileService;
	
	@PostMapping("/upload/{path}")
	public ResponseEntity<UploadFileResponse> uploadFile(@PathVariable String path, @RequestParam("file") MultipartFile file) {
		UploadFileResponse response;
		try {
			response = fileService.uploadByPath(path, file);
		} catch (IllegalArgumentException e) {
			// 지원하지 않는 path - 400에러
			return ResponseEntity.badRequest().build();
		} catch (IOException e) {
			// 500에러
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().body(response);
	}

}
