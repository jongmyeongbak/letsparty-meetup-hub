package com.letsparty.web.controller;

import org.springframework.stereotype.Controller;
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
}
