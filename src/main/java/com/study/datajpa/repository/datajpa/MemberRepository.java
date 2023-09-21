package com.study.datajpa.repository.datajpa;

import com.study.datajpa.dto.PMemberDTO;
import com.study.datajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);


    /**
     * @Query는 Spring Data JPA에서 사용되며, JPQL 문자열을 입력으로 받아 애플리케이션 초기화 시점에 파싱하고,
     * 실행 시점에 JPA 구현체가 이를 SQL로 변환하여 실행합니다. JPQL관련 문법 오류는 파싱하는 시점인 초기화 시점(애플리케이션이 처음 로딩되어 시작되는 시점)에 발생하면 알려줍니다.
     */
    @Query("select m from Member m where m.username = :username and m.age = :age") // 엔티티 조회
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m join m.team t")
    List<String> findUsernames();

    @Query("select new com.study.datajpa.dto.PMemberDTO(m.id, m.username, t.name) from Member m join m.team t")
    List<PMemberDTO> findMemberDTO();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findMemberListByUsername(String name); // return: 컬렉션
    Member findMemberByUsername(String name); // return: 단건
    Optional<Member> findByUsername(String name); // return: 단건

    /**
     * 리턴 타입이 Page이면 Page계산에 totalCount가 필요하므로 totalCount쿼리를 JPA가 날려줌
     * 따라서, totalCount쿼리를 다시 작성할 필요 없다
     */
    Page<Member> findPageByAge(int age, Pageable pageable);

    /**
     * Slice는 totalCount쿼리를 보내지 않음
     * Slice는 3개 요청하면 4(3 + 1)개를 쿼리해서 response해준다
     * 활용: (모바일)클라이언트에서 3개만 보여주고 한 개가 있다면 "더 보기"버튼을 통해 스크롤 방식으로 다음 10개을 또 제공한다
     */
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    /**
     * 데이터가 많아지면 totalCount하는데 오래걸리므로 성능이슈가 발생할 수 있다.
     * 이를 대비해서 totalCount쿼리를 따로 작성한다
     * 예를 들어, totalCount쿼리는 정렬도 필요없으며 만약 left outer join이라면 기준이 되는 테이블의 개수만 파악해서
     * 리턴해주면 된다.
     */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllBy(Pageable pageable);

    @Modifying(clearAutomatically = true) // em.clear()를 따로 해주지 않아도 된다
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int updateAgeInBulk(@Param("age") int age);

    @Query("select m from Member m join fetch m.team")
    List<Member> findMemberFetchJoin();

    // fetch join(== left outer join)을 지원하는 Spring Data jpa기능. (fetch join의 간편버전)
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberBy();


}

