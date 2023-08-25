package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Post {
	
    private long id;  
    private int no;  
    private Party partyNo; 
    private String userId;
    private String title; 
    private String content; 
    private int readCnt; 
    private int commentCnt;
    private int likeCnt;
    private boolean isDeleted;
    private boolean isNotification;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}