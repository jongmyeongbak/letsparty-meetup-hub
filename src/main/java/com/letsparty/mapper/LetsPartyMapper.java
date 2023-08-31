package com.letsparty.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.LetsPartyPost;

@Mapper
public interface LetsPartyMapper {

	void insertPost(LetsPartyPost letsPartyPost);
	
	int getTotalRows(Map<String, Object> param);
	List<LetsPartyPost> getPosts(Map<String, Object> param);
}
