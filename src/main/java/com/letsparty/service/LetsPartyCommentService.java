package com.letsparty.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.letsparty.dto.LetsPartyCommentDto;
import com.letsparty.dto.LetsPartyPostDto;
import com.letsparty.mapper.LetsPartyCommentMapper;
import com.letsparty.mapper.LetsPartyMapper;
import com.letsparty.mapper.PartyMapper;
import com.letsparty.security.user.LoginUser;
import com.letsparty.vo.LetsPartyComment;
import com.letsparty.vo.LetsPartyPost;
import com.letsparty.vo.Party;
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
	public void insertComment(LetsPartyCommentForm commentForm, LoginUser loginUser) {
		LetsPartyPost letsPartyPost = new LetsPartyPost();
		letsPartyPost.setNo(commentForm.getPostNo());
		Party party = new Party();
		party.setNo(commentForm.getPartyNo());
		User user = new User();
		user.setId(loginUser.getId());
		
		LetsPartyComment newComment = LetsPartyComment.builder()
											.post(letsPartyPost)
											.party(party)
											.user(user)
											.content(commentForm.getContent())
											.build();
		// 댓글등록
		letsPartyCommentMapper.insertComment(newComment);
		// 저장된 게시물을 불러오는 dto
		LetsPartyPostDto savedLetsPartyPost = letsPartyMapper.getPostDetailByNo(commentForm.getPostNo());
		// 해당 게시물에 댓글 수 증가 후 업데이트
		savedLetsPartyPost.setCommentCnt(savedLetsPartyPost.getCommentCnt()+1);
		letsPartyMapper.updatePost(savedLetsPartyPost);
	}
	
	// 게시물에 달린 모든 댓글 출력
	public List<LetsPartyCommentDto> getAllCommentsByPostNo(long postNo){
		return letsPartyCommentMapper.getAllCommentsByPostNo(postNo);
	}

	public List<LetsPartyCommentDto> getLatestTwoCommentsByPostNo(long postNo) {
		return letsPartyCommentMapper.getLatestTwoCommentsByPostNo(postNo);
	}

}
