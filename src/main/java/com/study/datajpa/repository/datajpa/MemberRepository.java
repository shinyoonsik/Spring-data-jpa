package com.study.datajpa.repository.datajpa;

import com.study.datajpa.dto.PMemberDTO;
import com.study.datajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findMemberBy(); // 전체 검색

    /**
     * @Query는 Spring Data JPA에서 사용되며, JPQL 문자열을 입력으로 받아 애플리케이션 초기화 시점에 파싱하고,
     * 실행 시점에 JPA 구현체가 이를 SQL로 변환하여 실행합니다. JPQL관련 문법 오류는 파싱하는 시점인 초기화 시점(애플리케이션이 처음 로딩되어 시작되는 시점)에 발생하면 알려줍니다.
     */
    @Query("select m from Member m where m.username = :username and m.age = :age") // 엔티티 조회
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernames();

    @Query("select new com.study.datajpa.dto.PMemberDTO(m.id, m.username, t.name) from Member m join m.team t")
    List<PMemberDTO> findMemberDTO();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);
}
