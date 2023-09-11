package com.letsparty.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.letsparty.dto.BeginEndPostNo;
import com.letsparty.dto.SimplePostDto;
import com.letsparty.mapper.PostMapper;
import com.letsparty.vo.Post;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	
	private final PostMapper postMapper;

	public int getLastPostNoByPartyNo(int partyNo) {
		Integer lastPostNo = postMapper.getLastPostNoByPartyNo(partyNo);
		if (null == lastPostNo) {
			lastPostNo = 0;
		}
		return lastPostNo;
	}

	public void readIncrement(int partyNo, int postNo) {
		Post post = postMapper.getPostByPostNoAndPartyNo(partyNo, postNo);
		post.setReadCnt(post.getReadCnt() + 1);
		postMapper.updatePost(post);
	}

	public Post getPostByPostNoAndPartyNo(int partyNo, int postNo) {
		return postMapper.getPostByPostNoAndPartyNo(partyNo, postNo);
	}

	public List<SimplePostDto> getSimplePostLimit5(int partyNo, int postNo) {
		int[] savedPostNos = postMapper.getPostNoWithCurPostNoLimit5(partyNo, postNo);
		List<SimplePostDto> sPostDtos = new ArrayList<>();
		for (int pNo : savedPostNos) {
			SimplePostDto sPostDto = postMapper.getSimplePostByPostNoAndPartyNo(partyNo, pNo);
			sPostDtos.add(sPostDto);
		}
		return sPostDtos;
	}

	public BeginEndPostNo getBeginAndEndPostNo(int partyNo) {
		return postMapper.getBeginAndEndPostNo(partyNo);
	}

	public BeginEndPostNo getThirdBeginAndEndPostNo(int partyNo, int postNo) {
		return postMapper.getThirdBeginAndEndPostno(partyNo, postNo);
	}
	
}
