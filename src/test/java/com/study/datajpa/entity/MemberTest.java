package com.study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false) // DB에 결과를 확인하고 싶다면 추가. @Transactional은 default로 Rollback=true
class MemberTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("Member 엔티티 저장 테스트")
    void testName(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 11, teamA);
        Member member3 = new Member("member3", 12, teamB);
        Member member4 = new Member("member4", 13, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush(); // 영속성 컨텍스트에 있는 insert쿼리를 DB에 날림
        em.clear(); // 영속성 컨텍스트에 있는 1차 캐시를 다 날림

        // 확인
        List<Member> selectedMember = em.createQuery("select m from Member m", Member.class).getResultList();
        assertThat(selectedMember.size()).isEqualTo(4);
        for(Member member : selectedMember){
            System.out.println(member);
        }
    }

    @Test
    @DisplayName("JpaBaseEntity 테스트")
    void 테스트_JpaBaseEntity() throws InterruptedException {
        // given
        Member member = new Member("memberA");
        em.persist(member);
        em.flush();

        Thread.sleep(2000);
        member.setAge(10000);

        em.flush();
        em.clear();

        // when
        Member foundMember = em.find(Member.class, member.getId());

        // then
        assertThat(foundMember.getUsername()).isEqualTo("memberA");
        assertThat(foundMember.getCreatedDate()).isNotEqualTo(foundMember.getUpdatedDate());
        System.out.println(foundMember.getCreatedBy());
        System.out.println(foundMember.getUpdateBy());
    }
}