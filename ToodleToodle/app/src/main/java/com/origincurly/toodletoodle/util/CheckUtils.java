package com.origincurly.toodletoodle.util;

import com.origincurly.toodletoodle.GlobalValue;

import java.util.regex.Pattern;

public class CheckUtils implements GlobalValue {

    public static boolean isNickNotNull(String nick) {
        return nick.length() > 1 && !nick.contains(USER_NICK_DEFAULT);
    }
    public static boolean validMail(String mail) {
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$");
        return pattern.matcher(mail).matches();
    }
    public static boolean validPw(String pw) {
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\w~!@#$%^&*_-]*$");
        return pattern.matcher(pw).find();
    }
    public static boolean validNick(String nick) {
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z가-힣]*$");
        return pattern.matcher(nick).matches();
    }
    public static boolean validContent(String content) {
        Pattern pattern = Pattern.compile("^[a-zA-Zㄱ-힣\\d\\w .,/?+~!@#$%^&*()_-]*$");
        return pattern.matcher(content).matches();
    }
    public static boolean validNumber(String number) {
        Pattern pattern = Pattern.compile("^[0-9-]*$");
        return pattern.matcher(number).matches();
    }

    public static boolean isNotNull(String value) {
        return (value != null && value.length() > 0);
    }

    public static boolean validLength(String value, int min, int max) {
        return (value.length() >= min && value.length() <= max);
    }
}
