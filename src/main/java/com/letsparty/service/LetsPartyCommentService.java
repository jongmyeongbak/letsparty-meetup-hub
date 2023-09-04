package com.letsparty.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.letsparty.mapper.LetsPartyCommentMapper;
import com.letsparty.mapper.LetsPartyMapper;
import com.letsparty.mapper.PartyMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.LetsPartyComment;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.Party;
import com.letsparty.vo.Post;
import com.letsparty.vo.User;
import com.letsparty.web.form.LetsPartyCommentForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LetsPartyCommentService {
	
	private final LetsPartyCommentMapper letsPartyCommentMapper;
	private final LetsPartyMapper letsPartyMapper;
	private final PartyMapper partyMapper;
	
	// 댓글등록
	public LetsPartyComment insertComment(LetsPartyCommentForm commentForm, LoginUser loginUser) {
		LetsPartyPost post = letsPartyMapper.getPostDetailByNo(commentForm.getPostNo());
		Party party = partyMapper.getPartyByNo(commentForm.getPartyNo());
		User user = new User();
		user.setId(loginUser.getId());
		
		LetsPartyComment letsPartyComment = LetsPartyComment.builder()
											.post(post)
											.party(party)
											.user(user)
											.content(commentForm.getContent())
											.build();
		
		letsPartyCommentMapper.insertComment(letsPartyComment);
		post.setCommentCnt(post.getCommentCnt()+1);
		LetsPartyComment insertedComment = letsPartyCommentMapper.getCommentByNo(letsPartyComment.getNo());
		
		return insertedComment;
	}
	
	public List<LetsPartyComment> getAllCommentsByPostNo(long postNo){
		return letsPartyCommentMapper.getAllCommentsByPostNo(postNo);
	}

}
