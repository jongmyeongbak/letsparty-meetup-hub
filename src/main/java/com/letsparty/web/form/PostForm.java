package com.letsparty.web.form;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@lombok.ToString
@Slf4j
public class PostForm {
	
	private String title;
	private String content;
	
//	지도 vo 필드
	private String placeName;
	private String placeId;
	private String addressName;
	private String roadAddressName;
}
