package com.study.datajpa.repository.datajpaImpl;

import com.study.datajpa.entity.Member;
import com.study.datajpa.repository.datajpa.MemberRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    /**
     * <사용자 정의 리포지토리>
     * 복잡한 쿼리가 필요한 경우 repository를 custom해서 사용
     * 1. QueryDSL
     * 2. Mybatis
     * 3. Spring jdbc template
     * 등을 이 클래스내의 멤버 메소드에 구현해서 사용
     *
     * 결론, 간단한 쿼리는 Spring Data jpa에서 제공하는 인터페이스및 쿼리 메소드 or @Query
     * 복잡한 쿼리나 동적쿼리는 (@Query(native = true))네이티브 쿼리 or QueryDSL or MyBatis, Spring jdbc template 사용
     *
     * 규칙
     * - 구현클래스의 이름은 MemberRepositoryCustom를 상속한 리포지터리 인터페이스 이름(MemberRepository) + Impl
     * - or 사용자 정의 인터페이스명(MemberRepositoryCustom) + Impl
     */

    private final EntityManager em; // 생성자가 하나만 있으면 Spring이 그 생성자를 사용하여 의존성을 자동으로 주입한다.

    @Override
    public List<Member> findCustomMember() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
