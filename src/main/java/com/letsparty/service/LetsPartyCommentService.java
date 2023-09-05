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
	public LetsPartyCommentDto insertComment(LetsPartyCommentForm commentForm, LoginUser loginUser) {
		// 기존 게시물을 불러오는 dto
		LetsPartyPostDto savedLetsPartyPost = letsPartyMapper.getPostDetailByNo(commentForm.getPostNo());
		
		// 댓글 vo에 set할 게시물 vo
		LetsPartyPost letsPartyPost = new LetsPartyPost();
		letsPartyPost.setNo(commentForm.getPostNo());
		Party party = partyMapper.getPartyByNo(commentForm.getPartyNo());
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
		// 해당 게시물에 댓글 수 증가 후 업데이트
		savedLetsPartyPost.setCommentCnt(savedLetsPartyPost.getCommentCnt()+1);
		letsPartyMapper.updatePost(savedLetsPartyPost);
		
		// 새롭게 추가된 댓글을 등록과 동시에 반환
		LetsPartyCommentDto insertedComment = letsPartyCommentMapper.getCommentByNo(newComment.getNo());
		
		return insertedComment;
	}
	
	// 게시물에 달린 모든 댓글 출력
	public List<LetsPartyCommentDto> getAllCommentsByPostNo(long postNo){
		return letsPartyCommentMapper.getAllCommentsByPostNo(postNo);
	}

}
