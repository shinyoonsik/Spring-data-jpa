package com.study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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

    // 발생: IllegalStateException: Not allowed to create transaction on shared EntityManager
    // 공유 EntityManager의 트랜잭션을 개발자가 직접적으로 시작하거나 커밋하는 것이 허용되지 않는다. 컨테이너가 관리(시작, 커밋, 롤백)할것이기 때문!
    // 결론, 선언적 트랜잭션을 사용하여 개발자는 트랜잭션의 경계를 선언할 수 있고, 트랜잭션의 세부적인 생명주기 관리는 프레임워크나 컨테이너에게 위임해야 한다.
    // 공유 EntityManager의 경우, 주로 @PersistenceContext 어노테이션을 통해 주입받게 되며, 이는 JTA(Java Transaction API) 트랜잭션을 사용합니다.
    // JTA 트랜잭션은 보통 컨테이너 환경(예: EJB 컨테이너)에서 관리되며, 개발자는 직접적으로 트랜잭션을 시작하거나 커밋하는 것이 허용되지 않습니다.
    // 대신, 선언적 트랜잭션 관리를 사용해야 합니다.
    @PersistenceContext
    private EntityManager em;

    @Test
    void testy(){
        Member member = new Member("hello");
        em.persist(member); // 트랜잭션안에서 호출되지 않으면 TransactionRequiredException발생
    }

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