package com.study.datajpa.repository.datajpa;

import com.study.datajpa.dto.PMemberDTO;
import com.study.datajpa.entity.Member;
import com.study.datajpa.entity.Team;
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

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

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
    @DisplayName("findMemberBy 테스트")
    void 테스트_쿼리메소드(){
        // given
        Member member = new Member("testyy");
        memberRepository.save(member);

        // when
        List<Member> results = memberRepository.findMemberBy();

        // then
        assertThat(results.size()).isGreaterThan(0);
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
}