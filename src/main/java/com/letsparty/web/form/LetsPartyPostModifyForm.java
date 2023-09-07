package com.letsparty.web.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LetsPartyPostModifyForm {
	
	private long no;
	
	@NotBlank(message = "게시글의 제목은 필수 입력 값입니다.")
	@Size(max = 255, message = "게시글의 제목은 255자를 넘길 수 없습니다.")
	private String title;
	
	@NotBlank(message = "게시글의 내용은 필수 입력 값입니다.")
	@Size(max = 4000, message = "게시글의 내용은 4000자를 넘길 수 없습니다.")
	private String content;
	
	private String partyName;
	private int readCnt;
	private int commentCnt;
	
	public LetsPartyPostModifyForm() {}
	
	@Builder
	public LetsPartyPostModifyForm(String title, String content, String partyName, int readCnt, int commentCnt) {
		super();
		this.title = title;
		this.content = content;
		this.partyName = partyName;
		this.readCnt = readCnt;
		this.commentCnt = commentCnt;
	}
}