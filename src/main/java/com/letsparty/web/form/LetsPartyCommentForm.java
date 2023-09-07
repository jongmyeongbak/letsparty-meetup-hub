package com.letsparty.web.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LetsPartyCommentForm {

	private long postNo;
	
	@NotNull(message = "파티는 필수 선택 값입니다.")
	private Integer partyNo;
	
	@NotBlank(message = "댓글 내용은 필수 입력 값입니다.")
	@Size(max = 500, message = "댓글의 내용은 500자를 넘길 수 없습니다.")
	private String content;
}
