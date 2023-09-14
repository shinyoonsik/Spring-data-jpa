package com.study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    private String username;


    protected Member(){
    }

    public Member(String username) {
        this.username = username;
    }
}
