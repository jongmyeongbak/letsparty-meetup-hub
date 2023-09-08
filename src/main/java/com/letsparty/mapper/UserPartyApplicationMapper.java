package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.letsparty.vo.UserPartyApplication;

@Mapper
public interface UserPartyApplicationMapper {

	void insert(UserPartyApplication userPartyApplication);
	UserPartyApplication findByNo(long no);
	UserPartyApplication findByPartyNoAndUserId(@Param("partyNo") int partyNo, @Param("userId") String userId);
	List<UserPartyApplication> findAllByPartyNo(int PartyNo);
	List<UserPartyApplication> findAllWithUserNoByPartyNoAndStatus(
			@Param("partyNo") int partyNo,
			@Param("status") String status,
			@Param("myNo") Integer myNo // Nullable parameter
	);
	void update(UserPartyApplication userPartyApplication);
	
	int countPartyMemberByPartyNoAndStatus(@Param("partyNo")int partyNo, @Param("status")String status);

	List<UserPartyApplication> findAllThatExceptMemberByUserId(String userId);
	List<UserPartyApplication> findAllByUserId(String userId);
	void withdraw(int upaNo);

}
