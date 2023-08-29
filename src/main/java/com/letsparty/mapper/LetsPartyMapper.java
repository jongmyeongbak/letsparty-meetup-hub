package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.LetsPartyPost;

@Mapper
public interface LetsPartyMapper {

	void insertPost(LetsPartyPost letsPartyPost);
}
