package com.letsparty.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.LetsPartyPost;

@Mapper
public interface LetsPartyMapper {

	void insertPost(LetsPartyPost letsPartyPost);
	void updatePost(LetsPartyPost letsPartyPost);
	LetsPartyPost getPostDetailByNo(long postNo);
	
	
	int getTotalRows(Map<String, Object> param);
	List<LetsPartyPost> getPosts(Map<String, Object> param);
}
