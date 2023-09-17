package com.study.datajpa.repository.datajpa;

import com.study.datajpa.dto.PMemberDTO;
import com.study.datajpa.entity.Member;
import com.study.datajpa.entity.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
//@Rollback(value = false)
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
 }