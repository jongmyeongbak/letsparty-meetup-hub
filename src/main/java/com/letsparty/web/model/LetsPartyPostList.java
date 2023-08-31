package com.letsparty.web.model;

import java.util.List;

import com.letsparty.info.Pagination;
import com.letsparty.vo.LetsPartyPost;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetsPartyPostList {
	
	private Pagination pagination;
	private List<LetsPartyPost> posts;
}
