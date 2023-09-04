package com.letsparty.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.letsparty.mapper.LetsPartyCommentMapper;
import com.letsparty.vo.LetsPartyComment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LetsPartyCommentService {
	
	private final LetsPartyCommentMapper letsPartyCommentMapper;
	
	// 댓글등록
	public void insertComment(LetsPartyComment comment) {
		letsPartyCommentMapper.insertComment(comment);
	}
	
	public List<LetsPartyComment> getAllCommentsByPostNo(long postNo){
		return letsPartyCommentMapper.getAllCommentsByPostNo(postNo);
	}
}
