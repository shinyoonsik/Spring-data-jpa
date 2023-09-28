package com.study.datajpa.controller;

import com.study.datajpa.entity.Member;
import com.study.datajpa.repository.datajpa.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") long id){
        // id가 null이 될 가능성이 없으므로(path가 잘못입력되면 404, 타입이 맞이 않으면 400)
        // 따라서 auto boxing할 필요없이 long으로 받아도 될듯

        Optional<Member> member = memberRepository.findById(id);
        if(member.isPresent()){
            return member.get().getUsername();
        }else{
            return "NoNo";
        }
    }

    // 객체의 생성과 의존성 주입이 완료된 후에 실행되는 초기화 메소드
    @PostConstruct
    public void init(){
        memberRepository.save(new Member("userA"));
    }

}
