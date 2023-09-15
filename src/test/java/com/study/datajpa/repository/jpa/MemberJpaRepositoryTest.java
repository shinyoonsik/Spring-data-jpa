package com.study.datajpa.repository.jpa;

import com.study.datajpa.entity.Member;
import com.study.datajpa.repository.jpa.MemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Test
    @DisplayName("Member basicCRUD테스트 on jpa")
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 검증
        Member foundMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member foundMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(foundMember1).isEqualTo(member1);
        assertThat(foundMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> results = memberJpaRepository.findAll();
        assertThat(results.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaRepository.delete(member1 );
        memberJpaRepository.delete(member2);
        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    @DisplayName("JPA update테스트-변경감지")
    void updateTest(){
        // given
        int updatedAge = 2000;
        Member member = new Member("test1");
        memberJpaRepository.save(member);

        // when
        member.setAge(updatedAge);
        memberJpaRepository.getEm().flush();
        memberJpaRepository.getEm().clear();

        // then
        Member result = memberJpaRepository.find(member.getId());
        assertThat(result.getAge()).isEqualTo(updatedAge);
    }

    @Test
    @DisplayName("findByUsernameAndAgeGreaterThan() 테스트")
    void 테스트_findByUsernameAndAgeGreaterThan(){
        // given
        int age = 91;
        int age2 = 92;
        String username = "ys";
        Member member1 = new Member(username, age);
        Member member2 = new Member(username, age2);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        // JPQL쿼리를 실행하기전에 flush가 호출됨
        List<Member> results = memberJpaRepository.findByUsernameAndAgeGreaterThan(username, 90);

        // then
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0).getUsername()).isEqualTo(username);
    }
}