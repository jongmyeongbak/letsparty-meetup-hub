package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 파일업로드 샘플 페이지를 위한 컨트롤러
@Controller
@RequestMapping("/upload")
public class UploadController {

	@GetMapping("/file")
	public String file() {
		return "page/upload/file.html";
	}
	
	@GetMapping("/media")
	public String media() {
		return "page/upload/media.html";
	}
	
	@GetMapping("/cover")
	public String cover() {
		return "page/upload/cover.html";
	}
	
	@GetMapping("/profile")
	public String profile() {
		return "page/upload/profile.html";
	}
	
	@GetMapping("/wrongpath")
	public String wrongpath() {
		return "page/upload/wrongpath.html";
	}
}
