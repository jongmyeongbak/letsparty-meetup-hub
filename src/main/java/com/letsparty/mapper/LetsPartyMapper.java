package com.letsparty.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.dto.LetsPartyPostDto;
import com.letsparty.vo.LetsPartyPost;

@Mapper
public interface LetsPartyMapper {

	void insertPost(LetsPartyPost letsPartyPost);
	void updatePost(LetsPartyPostDto letsPartyPost);
	LetsPartyPostDto getPostDetailByNo(long postNo);
	
	
	int getTotalRows(Map<String, Object> param);
	List<LetsPartyPost> getPosts(Map<String, Object> param);
	List<LetsPartyPost> getPostsLimit5();
}
