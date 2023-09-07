package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.dto.LetsPartyCommentDto;
import com.letsparty.vo.LetsPartyComment;

@Mapper
public interface LetsPartyCommentMapper {
	
	// 댓글 등록하기
	void insertComment(LetsPartyComment comment);
	
	// 렛츠파티 게시판에 등록된 댓글들 불러오기
	List<LetsPartyCommentDto> getAllCommentsByPostNo(long postNo);

	void getCommentByNo(long no);

	List<LetsPartyCommentDto> getLatestTwoCommentsByPostNo(long postNo);

}
