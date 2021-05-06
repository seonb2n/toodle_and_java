package com.origincurly.toodletoodle;

public interface GlobalValue {
    String TAG = "TOODLE_TOODLE"; // curly

    // Device info
    int NOW_VERSION_CODE = 2;
    String NOW_VERSION_NAME = "1.0.1";
    String PACKAGE_NAME = "com.origincurly.toodletoodle";
    String LANG_DEFAULT = "en";

    int PUSH_ON = 1;
    int PUSH_OFF = 0;
    int LOGIN_EXIST = 1;
    int LOGIN_NULL = 0;

    // User info
    String USER_PUSH_TOKEN_DEFAULT = "token";
    String USER_UUID_DEFAULT = "uuid";
    String USER_PARTITION_DEFAULT = "p1";
    String USER_NICK_DEFAULT = "UNKNOWN";
    int USER_DB_STATE_DEFAULT = 0;
    int BEGIN_AMPM_DEFAULT = 0;
    int BEGIN_HOUR_DEFAULT = 8;
    int BEGIN_MINUTE_DEFAULT = 0;
    int END_AMPM_DEFAULT = 1;
    int END_HOUR_DEFAULT = 12;
    int END_MINUTE_DEFAULT = 0;
    int TIME_PICKER_MINUTE_INTERVAL = 15;


    // URL info
    String DOMAIN_URL = "toodletoodle.cafe24.com/";
    String API_URL_DEBUG = "http://toodletoodle.cafe24.com/_api/";
    String API_URL_RELEASE = "http://toodletoodle.cafe24.com/_api/";
    String WEB_URL_DEBUG = "http://toodletoodle.cafe24.com/web01/";
    String WEB_URL_RELEASE = "http://toodletoodle.cafe24.com/web01/";

    String NULL_URL = "about:blank";

    String DEVICE_NAME = "and:";

    // Preference
    String USER_PREFERENCE_NAME = "UserInfo";
    String DEVICE_PREFERENCE_NAME = "DeviceInfo";
    String VIEW_PREFERENCE_NAME = "ViewInfo";

    // Time
    int INTRO_DELAY_TIME = 1500;
    int DB_CHECK_DELAY_TIME = 1000;
    int DB_CHECK_ANIMATION_DURATION = 300;
    int BACK_KEY_DELAY_TIME = 2000;
    int POSTIT_ADD_ANIMATION_TIME = 5000;
    int DATE_PICKER_FOLD_ANIMATION_TIME = 250;

    // Page info
    String DEFAULT_PAGE = "999"; // DEFAULT == SAME
    int TODAY_PAGE = 101;
    int PROJECT_PAGE = 201;
    int CALENDAR_PAGE = 301;
    int MY_PAGE = 401;

    // Db check index
    int DB_CHECK_UUID = 0;
    int DB_CHECK_POSTIT = 1;
    int DB_ADD_POSTIT = 2;
    int DB_CHECK_POSTIT_CATEGORY = 3;
    int DB_ADD_POSTIT_CATEGORY = 4;
    int DB_CHECK_PROJECT_ID_LIST = 5;
    int DB_ADD_PROJECT_ID_LIST = 6;
    int DB_CHECK_SET_STATE = 7;

    // RequestCode
    int GOOGLE_STORE_ACTIVITY = 100;

    // Regex
    String MAIL_REGEX = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
    String PW_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\w~!@#$%^&*_-]+$";
    String NICK_REGEX = "^[a-zA-Z가-힣0-9]*$";
    String CONTENT_REGEX = "^(?=[A-Za-z\\d\\w~!@#$%^&*_-]*$";

    // Postit
    int POSTIT_STATE_NORMAL = 1;
    int POSTIT_STATE_NEW = 2;
    int POSTIT_STATE_END = -1;

    int POSTIT_CATEGORY_NO_ID = 0;
    int POSTIT_LENGTH_MAX = 15;

    // User Input
    int PW_LENGTH_MIN = 4;
    int PW_LENGTH_MAX = 20;
    int NICK_LENGTH_MIN = 2;
    int NICK_LENGTH_MAX = 8;

    // Project
    int PROJECT_NO_ID = 0;
    int PROJECT_TITLE_LENGTH_MAX = 15;
    int PROJECT_CONTENT_LENGTH_MAX = 30;
    int PROJECT_DONE = 1;
    int PROJECT_NOT_DONE = 0;

    // Task
    int TASK_TITLE_LENGTH_MAX = 15;
    int TASK_IMPORTANCE_HIGH = 5;
    int TASK_IMPORTANCE_MEDIUM = 3;
    int TASK_IMPORTANCE_LOW = 1;
    int TASK_DURING_DEFAULT = 1;
    int TASK_DURING_NO = -9999;
    int TASK_DURING_MIN = -365;
    int TASK_DURING_MAX = 365;

    int ACTION_TITLE_LENGTH_MAX = 15;
    int ACTION_COUNT_MAX = 10;
    int ACTION_STATE_ON = 1;
    int ACTION_STATE_OFF = -1;
    int ACTION_STATE_NEW = 2;
    int ACTION_STATE_NEW_READY = 3;

    // Schedule
    int SCHEDULE_TITLE_LENGTH_MAX = 15;
    int SCHEDULE_CONTENT_LENGTH_MAX = 200;

    // AddFast
    int ADD_FAST_INIT = -1;
    int ADD_FAST_TASK_TAB = 0;
    int ADD_FAST_SCHEDULE_TAB = 1;
    int ADD_FAST_POSTIT_TAB = 2;

    // NumberPicker
    int PICKER_YEAR_MAX_OFFSET = 10;
    int PICKER_DIVIDER_COLOR = 0x00000000;
    int PICKER_SELECT_TEXT_COLOR = 0x222222;

    //Time
    int TIME_ONE_DAY = 86400;
    int TIME_ONE_HOUR = 3600;
}