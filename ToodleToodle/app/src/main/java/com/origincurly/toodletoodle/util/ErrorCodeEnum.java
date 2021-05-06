package com.origincurly.toodletoodle.util;

public enum ErrorCodeEnum {

    CODE_NORMAL(1),

    CODE_MAIL_WRONG(1021), // 메일 틀림
    CODE_PW_WRONG(1022), // 비밀번호 틀림
    CODE_MAIL_DUPLICATE(1081), // 메일 중복
    CODE_NAVER_ID_WRONG(1031), // NAVER ID 없음
    CODE_NAVER_ID_DUPLICATE(1091), // NAVER ID 중복 (가입시) - 에러
    CODE_KAKAO_ID_WRONG(1041), // KAKAO ID 없음
    CODE_KAKAO_ID_DUPLICATE(1101), // KAKAO ID 중복 (가입시) - 에러
    CODE_GOOGLE_ID_WRONG(1051), // GOOGLE ID 없음
    CODE_GOOGLE_ID_DUPLICATE(1111), // GOOGLE ID 중복 (가입시) - 에러
    CODE_PW_RESET_MAIL_NULL(1151), // 메일 없음

    CODE_POSTIT_WRONG(2011), // POST IT 없음

    CODE_POSTIT_CATEGORY_WRONG(3011), // POST IT CATEGORY 없음

    CODE_PROJECT_ID_LIST_WRONG(4011), // PROJECT ID LIST 없음

    CODE_PROJECT_WRONG(5011), // PROJECT 없음
    CODE_PROJECT_AUTH_WRONG(5012), // PROJECT 조회 권한 없음

    CODE_DB_CHECK_UUID_WRONG(10001), // UUID 없음
    CODE_DB_CHECK_POSTIT_WRONG(10021), // POSTIT 없음
    CODE_DB_CHECK_POSTIT_CATEGORY_WRONG(10041), // POSTIT_CATEGORY 없음
    CODE_DB_CHECK_PROJECT_ID_LIST_WRONG(10061), // PROJECT_ID_LIST 없음

    CODE_UNKNOWN(-9000),
    CODE_JSON_ERROR(-9001), // JSON ERROR
    CODE_VERSION_ERROR(-9002), // JSON ERROR (INTRO VERSION CHECK)
    CODE_KAKAO_SESSION_CLOSED(-9003), // KAKAO SESSION 에러
    CODE_KAKAO_TOKEN_FAIL(-9004), // KAKAO TOKEN 에러
    CODE_ERROR_NULL(-9999); // 에러 값이 존재하지 않는 경우

    public final int value;

    ErrorCodeEnum(int v) {
        value = v;
    }

    public static ErrorCodeEnum int2Enum(int value) {
        for (ErrorCodeEnum response : values()) {
            if (response.value == value) {
                return response;
            }
        }
        return CODE_UNKNOWN;
    }

    public static ErrorCodeEnum string2Enum(String value) {
        for (ErrorCodeEnum response : values()) {
            if (response.value == Integer.parseInt(value)) {
                return response;
            }
        }
        return CODE_UNKNOWN;
    }

    public static String enum2String(ErrorCodeEnum errorCodeEnum) {
        return errorCodeEnum.name();
    }
    public static int enum2int(ErrorCodeEnum errorCodeEnum) {
        return errorCodeEnum.value;
    }
}