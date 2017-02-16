package com.rnh.showmethecard.model.mapper;

import com.rnh.showmethecard.model.dto.Member;

//com.mvcdemoweb.model.mapper.MemberMapper 파일에 대응하는 인터페이스
public interface CardMapper {
	boolean selectCardDbBySiteUrl(String url);
	void insertCardDb(String userId, String url);
	void insertCardDb(int cardNo, String mycComment);
}
