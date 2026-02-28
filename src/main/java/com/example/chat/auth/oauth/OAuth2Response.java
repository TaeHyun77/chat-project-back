package com.example.chat.auth.oauth;

public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    // 이메일
    String getEmail();

    // 사용자 이름
    String getName();
}
