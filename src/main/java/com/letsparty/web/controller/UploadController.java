package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 파일업로드 샘플 페이지를 위한 컨트롤러
@Controller
@RequestMapping("/upload")
public class UploadController {

	// 첨부파일 경로제공
	@GetMapping("/file")
	public String file() {
		return "page/upload/file.html";
	}
	
	// 미디어 경로제공
	@GetMapping("/media")
	public String media() {
		return "page/upload/media.html";
	}
	
	// 파티커버 경로제공
	@GetMapping("/cover")
	public String cover() {
		return "page/upload/cover.html";
	}
	
	// 유저프로필 경로제공
	@GetMapping("/profile")
	public String profile() {
		return "page/upload/profile.html";
	}
	
	// 파일전송 시험용
	@GetMapping("/testparty")
	public String testparty() {
		return "page/upload/testparty.html";
	}
	
	// 파일전송불가 시험용
	@GetMapping("/wrongpath")
	public String wrongpath() {
		return "page/upload/wrongpath.html";
	}
}
