package com.study.datajpa.repository;

import com.study.datajpa.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // 테스트를 하더라도 Spring Bean을 injection받아서 사용할 것이므로 이 어노테이션이 필요
@Transactional // 테스트상황에서 @Transactional은 결과를 자동적으로 rollback함. 따라서 콘솔에 쿼리가 보이지 않음
@Rollback(value = false)
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("jpa를 활용한 Member save, find 테스트")
    void testName(){
        Member memberA = new Member("memberA");
        Member savedMember = memberJpaRepository.save(memberA);

        Member foundMember = memberJpaRepository.find(savedMember.getId());

        assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
        assertThat(foundMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(foundMember).isEqualTo(memberA);
    }
}