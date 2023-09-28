package com.study.datajpa.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


/**
 * createdDate는 모든 테이블에 다 들어가지만 createdBy, updatedBy는 필요없을 수도 있다
 * 따라서, 시간과 사용자를 모두 필요로 하는 엔티티의 경우 BaseEntity를 상속받고
 * 시간만 필요한 엔티티의 경우 BaseTimeEntity를 상속박으면 된다.
 *
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;
}
