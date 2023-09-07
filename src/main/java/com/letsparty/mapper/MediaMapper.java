package com.letsparty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.letsparty.vo.Media;

@Mapper
public interface MediaMapper {

	void insertMedia(List<Media> images);

}
