package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.dto.LetsPartyCommentDto;
import com.letsparty.dto.PartyCommentDto;
import com.letsparty.vo.Comment;
import com.letsparty.web.form.CommentForm;

@Mapper
public interface CommentMapper {

	void insertComment(Comment comment);
	
	// 파티게시판 댓글 불러오기
	List<PartyCommentDto> getAllCommentsByPostNo(long postId);

	void getCommentByNo(long no);

	List<PartyCommentDto> getLatestTwoCommentsByPostNo(long postId);
}
