package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.Place;

@Mapper
public interface PlaceMapper {
	
	void insertPlace(Place place);
}
