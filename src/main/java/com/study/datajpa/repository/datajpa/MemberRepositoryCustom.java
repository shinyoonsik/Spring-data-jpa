package com.study.datajpa.repository.datajpa;

import com.study.datajpa.entity.Member;

import java.util.List;

// 사용자 정의 인터페이스
public interface MemberRepositoryCustom {
    List<Member> findCustomMember();
}
