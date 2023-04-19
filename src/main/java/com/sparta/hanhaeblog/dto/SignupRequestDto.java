package com.sparta.hanhaeblog.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Setter
@Getter
public class SignupRequestDto {

    // 최소 4자 이상, 10자 이하
    @Size(min = 4, max = 10)
    private String username;

    // 최소 8자 이상, 15자 이하
    @Size(min = 8, max = 15)
    private String password;

    private boolean admin = false;

    private String adminToken = "";
}