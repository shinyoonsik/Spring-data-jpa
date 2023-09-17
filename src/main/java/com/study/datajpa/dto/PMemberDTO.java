package com.study.datajpa.dto;

import lombok.Data;

@Data
public class PMemberDTO { // P는 Projection을 의미함
    private Long id;
    private String username;
    private String teamName;

    public PMemberDTO(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
