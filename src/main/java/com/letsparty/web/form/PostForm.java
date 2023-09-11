package com.letsparty.web.form;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.letsparty.vo.Place;
import com.letsparty.vo.Poll;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostForm {

	@NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;
	@NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
    private boolean isNotification;
	private List<String> imageName;
	private List<String> videoName;
	
//	지도 vo 
	private Place place;

//  투표/투표항목 vo
	private Poll poll;
	private PollOptionForm pollOptionForm;
}
