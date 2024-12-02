package com.hey.givumethemoney.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//ADMIN 계정의 password 암호화를 돕는 코드
public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin1234"; //admin 계정의 암호
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
    }
}