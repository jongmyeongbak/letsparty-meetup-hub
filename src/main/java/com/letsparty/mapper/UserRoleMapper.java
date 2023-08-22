package com.letsparty.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
	
	public void addRole(String id, int no);

	public void addRole(Map<String, Object> param);

	public String getRoleNameById(String id);

	public void updateRole(Map<String, Object> param);
}
