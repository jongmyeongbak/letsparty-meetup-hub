package com.letsparty.web.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("isAuthenticated()")
public class FileRestController {

	private final FileService fileService;
	
	@PostMapping("/upload/{path}")
	public ResponseEntity<UploadFileResponse> uploadFile(@PathVariable String path, @RequestParam("file") MultipartFile file) throws IOException {
		UploadFileResponse response = fileService.uploadByPath(path, file);
		return ResponseEntity.ok().body(response);
	}

}
