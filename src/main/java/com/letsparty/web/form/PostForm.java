package com.letsparty.web.form;

import java.util.List;

import com.letsparty.vo.Place;
import com.letsparty.vo.Poll;
import com.letsparty.vo.PollAnswer;
import com.letsparty.vo.PollOption;
import com.letsparty.vo.Post;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostForm {
	
	private Post post;
	private List<String> imageName;
	private List<String> videoName;
	
//	지도 vo 
	private Place place;

//  투표/투표항목 vo
	private Poll poll;
	private PollOption pollOption;
	private PollAnswer pollAnswer;
	private PollOptionForm pollOptionForm;
}
