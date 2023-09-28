package com.study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * EntityManager.persist(entity)를 호출한다.
     * @PrePersist가 붙은 prePersist() 메서드가 호출되어 createdDate와 updatedDate를 현재 시간으로 설정한다.
     *
     * 결론, @PrePersist 애노테이션이 붙은 메서드는 엔터티가 처음 영속성 컨텍스트에 저장되기 전에 호출된다.
     */
    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    /**
     * 영속 상태의 엔터티의 필드 값을 변경한다.
     * 트랜잭션을 커밋하거나 EntityManager.flush()를 호출한다.
     * @PreUpdate가 붙은 preUpdate() 메서드가 호출되어 updatedDate를 현재 시간으로 설정한다.
     * 변경된 엔터티가 데이터베이스에 반영된다.
     *
     * 결론, @PreUpdate 애노테이션이 붙은 메서드는 영속성 컨텍스트에 있는 엔터티가 데이터베이스에 업데이트되기 전에 호출된다.
     */
    @PreUpdate
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}
