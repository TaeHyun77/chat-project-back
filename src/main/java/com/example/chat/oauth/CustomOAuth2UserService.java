package com.example.chat.oauth;

import com.example.chat.member.Member;
import com.example.chat.member.repository.MemberRepository;
import com.example.chat.member.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String member_id = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if (member_id.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }

        String username = oAuth2Response.getProvider() + oAuth2Response.getProviderId();

        OAuth2Response finalOAuth2Response = oAuth2Response;

        Member member = memberRepository.findByUsername(username)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .username(username)
                            .name(finalOAuth2Response.getName())
                            .email(finalOAuth2Response.getEmail())
                            .nickName("익명의 사용자")
                            .role(Role.MEMBER)
                            .build();
                    return memberRepository.save(newMember);
                });

        if (!member.isNew()) {
            log.info("{}, 기 가입자 입니다.", username);
        }

        return new CustomOAuth2User(member, oAuth2User.getAttributes());
    }
}
