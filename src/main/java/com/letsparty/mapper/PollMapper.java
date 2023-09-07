package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.Poll;
import com.letsparty.vo.PollOption;

@Mapper
public interface PollMapper {
	
	void insertPoll(Poll poll);
	
	void insertPollOption(PollOption pollOption);
}
