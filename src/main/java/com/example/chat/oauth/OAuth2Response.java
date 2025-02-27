package com.example.chat.oauth;

public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    // 이메일
    String getEmail();

    // 사용자 이름
    String getName();
}
