package com.letsparty.web.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LetsPartyPostForm {
	
	@NotBlank(message = "게시글의 제목은 필수 입력 값입니다.")
	@Size(max = 255, message = "게시글의 제목은 255자를 넘길 수 없습니다.")
	private String title;
	
	@NotBlank(message = "게시글의 내용은 필수 입력 값입니다.")
	@Size(max = 4000, message = "게시글의 내용은 4000자를 넘길 수 없습니다.")
	private String content;
	
	@Min(value = 1, message = "파티는 필수 선택 값입니다.")
	private int partyNo;
	private int categoryNo;
}