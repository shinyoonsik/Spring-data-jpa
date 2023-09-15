package com.study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class TeamTest {

    @PersistenceContext
    EntityManager em;


    @Test
    @DisplayName("Team엔티티 저장 테스트")
    void testName(){
        Team team = new Team("teamAAA");
        em.persist(team);

        em.flush();
        em.clear();

        String jpql = "select t from Team t where t.id=:id";
        TypedQuery<Team> query = em.createQuery(jpql, Team.class);
        query.setParameter("id", team.getId()); // 파라미터 바인딩
        List<Team> selectedTeams = query.getResultList();

        assertThat(selectedTeams.get(0).getName()).isEqualTo(team.getName());
        for(Team myTeam : selectedTeams){
            System.out.println(myTeam);
        }
    }
}