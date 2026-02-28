package com.example.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum
ErrorCode {

    UNKNOWN("000_UNKNOWN", "알 수 없는 에러가 발생했습니다."),

    NOT_FOUND_MEMBER("NOT_FOUND_MEMBER", "사용자를 찾을 수 없습니다."),

    ERROR_TO_RESPONSE_MEMBER("ERROR_TO_RESPONSE_MEMBER", "member 반화 중 에러 발생"),

    ACCESSTOKEN_IS_EXPIRED("ACCESSTOKEN_IS_EXPIRED", "access 토큰 만료"),

    REFRESHTOKEN_IS_EXPIRED("REFRESHTOKEN_IS_EXPIRED", "refresh 토큰 만료"),

    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),

    IS_NOT_REFRESHTOKEN("IS_NOT_REFRESHTOKEN", "refresh 토큰이 아닙니다."),

    NOT_FOUND_CHATROOM("NOT_FOUND_CHATROOM", "채팅방을 찾을 수 없습니다."),

    UNAUTHORIZED_CHATROOM_ACCESS("UNAUTHORIZED_CHATROOM_ACCESS", "채팅방을 삭제할 권한이 없습니다."),

    ERROR_TO_CALL_DEPARTURE_API("ERROR_TO_CALL_DEPARTURE_API", "출국장 데이터 API 호출 실패"),

    ERROR_TO_CALL_PLANE_API("ERROR_TO_CALL_PLANE_API", "항공편 데이터 API 호출 실패"),

    FAIL_TO_DELETE_PLANE_DATA("FAIL_TO_DELETE_PLANE_DATA", "항공편 데이터 삭제 에러"),

    FAIL_TO_DELETE_DEPARTURE_DATA("FAIL_TO_DELETE_DEPARTURE_DATA", "출국장 데이터 삭제 에러"),

    ERROR_TO_CHANGE_JSON_DATE("ERROR_TO_CHANGE_JSON_DATE", "json 데이터 변환 실패"),

    ERROR_TO_PARSE_JSON("ERROR_TO_PARSE_JSON", "JSON 형식으로 파싱 싪패");
    private final String errorCode;

    private final String message;
}
