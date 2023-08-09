package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/party")
public class PartyController {
	
	@GetMapping("/{partyNo}/member")
	public String member(@PathVariable String partyNo) {
		// partyNo를 사용하여 파티 멤버를 조회하고 작업을 수행합니다.
		return "page/party/member";
	}
	
	@GetMapping("/{partyNo}/modify")
	public String modify(@PathVariable String partyNo, Model model) {
		// partyNo를 사용하여 파티 정보를 조회하고 작업을 수행합니다.
		return "page/party/modify";
	}
	
	@GetMapping("/{partyNo}")
	public String home(@PathVariable String partyNo, Model model) {
		// partyNo를 사용하여 파티 게시물을 조회합니다.
		return "page/party/home";
	}
}
