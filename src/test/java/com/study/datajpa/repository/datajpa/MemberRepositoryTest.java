package com.study.datajpa.repository.datajpa;

import com.study.datajpa.dto.PMemberDTO;
import com.study.datajpa.entity.Member;
import com.study.datajpa.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager em; // 같은 트랜잭션이면 같은 em을 주입받는다. 따라서 멀티스레드 환경이어도 걱정할 필요 없음

    @Test
    @DisplayName("MemberRepository의 구현체 테스트")
    void MemberRepository_구현체_확인(){
        // MemberRepository는 인터페이스이고 나는 그 구현체를 만든적이 없는데 어떤 객체가 Injection되는거지?
        System.out.println("memberRepository구현체: " + memberRepository.getClass());

    }

    @Test
    @DisplayName("Spring data jpa를 활용한 Member save, find테스트")
    void testName(){
        Member member = new Member("memberB");
        Member savedMember = memberRepository.save(member);

        Member foundMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
        assertThat(foundMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(foundMember).isEqualTo(savedMember); //JPA는 엔티티의 동일성(identity)을 보장한다
    }

    @Test
    @DisplayName("Member basicCRUD테스트 on spring data jpa")
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member foundMember1 = memberRepository.findById(member1.getId()).get();
        Member foundMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(foundMember1).isEqualTo(member1);
        assertThat(foundMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> results = memberRepository.findAll();
        assertThat(results.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    @DisplayName("findByUsernameAndAgeGreaterThan() 테스트")
    void 테스트_findByUsernameAndAgeGreaterThan(){
        // given
        int age = 101;
        int age2 = 102;
        String username = "ys";
        Member member1 = new Member(username, age);
        Member member2 = new Member(username, age2);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> results = memberRepository.findByUsernameAndAgeGreaterThan(username, 100);

        // then
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0).getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("@Query테스트, 메소드: findUser()")
    void 테스트_findUser메소드(){
        // given
        int age = 120;
        String name = "yss";
        Member member = new Member(name, age);
        memberRepository.save(member);

        // when
        List<Member> user = memberRepository.findUser(name, age);

        // then
        assertThat(user.size()).isEqualTo(1);
        assertThat(user.get(0).getUsername()).isEqualTo(name);
    }

    @Test
    @DisplayName("@Query테스트, 메소드: findUsernames()")
    void 테스트_findUsernames(){
        // given
        int age = 120;
        String name = "yss1234";
        Member member = new Member(name, age);
        memberRepository.save(member);

        // when
        List<String> usernames = memberRepository.findUsernames();

        // then
        assertThat(usernames.size()).isGreaterThanOrEqualTo(1);
        for(String s : usernames){
            System.out.println(s);
        }
    }

    @Test
    @DisplayName("@Query테스트, 메소드: findUsernames()")
    void 테스트_findMemberDTO(){
        // given
        Team team = new Team("LiverPool");
        teamRepository.save(team);

        Member member = new Member("yss1234", 123);
        member.changeTeam(team);
        memberRepository.save(member);

        // when
        List<PMemberDTO> results = memberRepository.findMemberDTO();

        // then
        for (PMemberDTO pMemberDTO : results){
            System.out.println(pMemberDTO);
        }
    }

    @Test
    @DisplayName("@Query테스트, 메소드: findByNames()")
    void 테스트_findByNames(){
        // given
        Member member1 = new Member("AAA", 1);
        Member member2 = new Member("BBB", 2);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> results = memberRepository.findByNames(List.of("AAA", "BBB"));

        // then
        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("JPA 반환 타입 테스트 - 컬렉션 리턴")
    void 테스트_findMembersByUsername(){
        // when
        List<Member> results = memberRepository.findMemberListByUsername("AAA");

        // then
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("JPA 반환 타입 테스트 - 단건 리턴")
    void 테스트_findMemberByUsername(){
        // when
        Member result = memberRepository.findMemberByUsername("AAA");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("JPA 반환 타입 테스트 - optional 리턴")
    void 테스트_findByUsername(){
        // when
        Member member = memberRepository.findByUsername("AAA").orElse(new Member("default"));
        assertThat(member.getUsername()).isEqualTo("default");

        try{
            Member member1 = memberRepository.findByUsername("AAA").orElseThrow(() -> new NoSuchElementException("No value"));
        } catch (NoSuchElementException e){
            assertThat(e.getMessage()).isEqualTo("No value");
        }
    }

    @Test
    @DisplayName("Spring Data JPA 페이징 테스트-Page")
    void 테스트_Spring_Data_JPA_페이징_Page(){
        // given
        int age = 10;
        int offset = 0;
        int limit = 3;
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // sorting이 필요없는 경우 or sorting조건이 복잡해서 JPQL로 따로 빼는 경우
        // PageRequest pageRequest = PageRequest.of(0, 3);
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest);

        // then
        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지 번호. (몇 번째 page인지)
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Spring Data JPA 페이징 테스트-Slice")
    void 테스트_Spring_Data_JPA_페이징_Slice(){
        // given
        int age = 10;
        int offset = 0;
        int limit = 3; // size를 의미함
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3);

        // when
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);

        // then
        assertThat(slice.getContent().size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Spring Data JPA 페이징 테스트-Page최적화")
    void 테스트_Spring_Data_JPA_페이징_Page_최적화(){
        // given
        int age = 10;
        int offset = 0;
        int limit = 3;
        Team team = new Team("teamA");
        teamRepository.save(team);

        memberRepository.save(new Member("member1", 10, team));
        memberRepository.save(new Member("member2", 10, team));
        memberRepository.save(new Member("member3", 10, team));
        memberRepository.save(new Member("member4", 10, team));
        memberRepository.save(new Member("member5", 10, team));

        // sorting이 필요없는 경우 or sorting조건이 복잡해서 JPQL로 따로 빼는 경우
        // PageRequest pageRequest = PageRequest.of(0, 3);
        PageRequest pageRequest = PageRequest.of(0, 3);

        // when
        Page<Member> page = memberRepository.findMemberAllBy(pageRequest);

        // then
        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지 번호. (몇 번째 page인지)
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Spring Data JPA 벌크연산 테스트")
    void 테스트_bulk_update(){
        // given
        int age = 10;
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // when
        int resultCount = memberRepository.updateAgeInBulk(age);

        // then
        assertThat(resultCount).isEqualTo(5);
    }

    @Test
    @DisplayName("Spring Data JPA 벌크연산 주의사항 테스트")
    void 테스트_bulk_update_modify(){
        System.out.println("entityManager: " + em);
        // given
        int age = 10;
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // when
        int resultCount = memberRepository.updateAgeInBulk(age);

        Optional<Member> member = memberRepository.findByUsername("member1");
        member.ifPresent(System.out::println);

        // then
        assertThat(resultCount).isEqualTo(5);
    }

    @Test
    @DisplayName("N + 1문제 구현")
    void 테스트_엔플러스1_문제(){
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        member1.setTeam(teamA);
        member2.setTeam(teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // N + 1 문제 발생
        // memberRepository.findAll()을 통해 1번 조회했으나, for문에서 N개의 쿼리가 나감
        List<Member> members = memberRepository.findAll();
        for(Member member : members){
            System.out.println(member.getUsername());

            // Member가 lazy loading이므로 proxy객체를 가져옴
            System.out.println("team: " + member.getTeam().getClass());

            // 실제로 team의 속성정보를 사용할 때, 쿼리를 날림
            System.out.println("team의 name" + member.getTeam().getName());
        }
    }

    @Test
    @DisplayName("N + 1문제 해결 방법 테스트: fetch join")
    void 테스트_fetch_join(){
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        member1.setTeam(teamA);
        member2.setTeam(teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // member를 조회할 때, team도 한 번에 다 가져온다
        List<Member> members = memberRepository.findMemberFetchJoin();
        for(Member member : members){
            System.out.println("team객체: " + member.getTeam().getClass()); // proxy가 아님
            System.out.println("member의 team's name: " + member.getTeam().getName());
        }
    }

    @Test
    @DisplayName("N + 1문제 해결 방법 테스트: EntityGraph")
    void 테스트_EntityGraph(){
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        member1.setTeam(teamA);
        member2.setTeam(teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // member를 조회할 때, team도 한 번에 다 가져온다
        List<Member> members = memberRepository.findMemberBy();
        for(Member member : members){
            System.out.println("member의 team's name: " + member.getTeam().getName());
        }
    }

    @Test
    @DisplayName("JPA Hint테스트")
    void 테스트_JPA_hint(){
        // given
        memberRepository.save(new Member("member1"));
        em.flush();
        em.clear();

        // when
        Member foundMember = memberRepository.findReadOnlyByUsername("member1");
        foundMember.setUsername("change username");
        em.flush(); // update 쿼리가 나가지 않음.
        em.clear();

        Member member1 = memberRepository.findByUsername("member1").get();
        member1.setUsername("member2"); // update쿼리가 발생
    }

    @Test
    @DisplayName("사용자 정의 리파지토리: MemberRepositoryCustom테스트")
    void 테스트_MemberRepositoryCustom(){
        memberRepository.save(new Member("heloo"));

        List<Member> memberCustom = memberRepository.findCustomMember();

        assertThat(memberCustom.size() > 0).isTrue();
    }

}