package com.letsparty.web.form;

import com.letsparty.vo.Place;
import com.letsparty.vo.Post;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostForm {
	
	private Post post;
	
//	지도 vo 
	private Place place;

}
