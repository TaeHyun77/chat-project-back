package com.example.chat.member;

import com.example.chat.airport.plane.Plane;
import com.example.chat.airport.plane.dto.PlaneResDto;
import com.example.chat.airport.plane.repository.PlaneRepository;
import com.example.chat.airport.weather.WeatherService;
import com.example.chat.airport.weather.dto.WeatherForecastResDto;
import com.example.chat.messaging.chatRoom.dto.ChatRoomResDto;
import com.example.chat.messaging.chatRoom.repository.ChatRoomRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.member.dto.MemberResDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PlaneRepository planeRepository;
    private final PlaneSubscriptionRepository planeSubscriptionRepository;
    private final WeatherService weatherService;

    // 사용자 정보 반환
    public MemberResDto info(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

        return MemberResDto.from(member);
    }

    public ResponseEntity<String> googleLogout(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        log.info("로그 아웃 성공");

        return ResponseEntity.ok("로그 아웃 성공");
    }

    // nickName이 이미 존재하는지 여부
    public boolean isNickName(String editNickName) {
        return memberRepository.existsByNickName(editNickName);
    }

    // nickName 수정
    @Transactional
    public void editNickName(Long id, String editNickName) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER));

        member.changeNickName(editNickName);
    }

    // 특정 사용자가 생성한 채팅방 목록 반환
    public List<ChatRoomResDto> memberChatRooms(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER);
        }

        return chatRoomRepository.findChatroomsByMemberId(memberId).stream()
                .map(chatRoom -> ChatRoomResDto.builder()
                        .chatRoomId(chatRoom.getChatRoomId())
                        .chatRoomName(chatRoom.getChatRoomName())
                        .createdAt(chatRoom.getCreatedAt())
                        .modifiedAt(chatRoom.getModifiedAt())
                        .build())
                .toList();
    }

    // 항공편 구독
    @Transactional
    public void subscribePlane(String username, Long planeId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

        Plane plane = planeRepository.findById(planeId)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_PLANE));

        if (planeSubscriptionRepository.existsByMemberAndPlane(member, plane)) {
            throw new ChatException(HttpStatus.CONFLICT, ErrorCode.ALREADY_SUBSCRIBED);
        }

        planeSubscriptionRepository.save(PlaneSubscription.builder()
                .member(member).plane(plane).build());
    }

    // 항공편 구독 해제
    @Transactional
    public void unsubscribePlane(String username, Long planeId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

        planeSubscriptionRepository.deleteByMemberIdAndPlaneId(member.getId(), planeId);
    }

    // 구독 항공편 목록 조회
    @Transactional(readOnly = true)
    public List<PlaneResDto> getSubscribedPlanes(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

        return planeSubscriptionRepository.findByMember(member).stream()
                .map(sub -> PlaneResDto.from(sub.getPlane()))
                .toList();
    }

    // 구독 항공편 도착 공항 시간별 날씨 예보 조회
    public List<WeatherForecastResDto> getSubscribedFlightWeather(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

        List<PlaneSubscription> subscriptions = planeSubscriptionRepository.findByMember(member);

        // airportCode 기준 중복 제거
        Map<String, String> airportMap = new LinkedHashMap<>();
        for (PlaneSubscription sub : subscriptions) {
            Plane plane = sub.getPlane();
            airportMap.putIfAbsent(plane.getAirportCode(), plane.getAirport());
        }

        List<WeatherForecastResDto> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : airportMap.entrySet()) {
            try {
                result.add(weatherService.getHourlyForecast(entry.getKey(), entry.getValue()));
            } catch (ChatException e) {
                log.warn("날씨 예보 조회 건너뜀: airportCode={}, 사유={}", entry.getKey(), e.getMessage());
            }
        }

        return result;
    }
}
