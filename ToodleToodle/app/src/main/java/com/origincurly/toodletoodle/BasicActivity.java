package com.origincurly.toodletoodle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import java.util.regex.Pattern;

import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_KAKAO_SESSION_CLOSED;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_KAKAO_TOKEN_FAIL;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_NORMAL;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_UNKNOWN;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.enum2String;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.enum2int;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class BasicActivity extends AppCompatActivity implements GlobalValue {

    //region UserInfo

    // device
    public int versionCode, push;
    public String versionName, lang, pushToken; //en,kr / push token

    // user
    public int loginState;
    public String userUuid, userPartition, userNick;
    public int userDbState;

    public void setDataLogin(String _userUuid, String _userPartition, String _userNick, int _userDbState) {
        push = PUSH_ON;
        loginState = LOGIN_EXIST;
        userUuid = _userUuid;
        userPartition = _userPartition;
        userNick = _userNick;
        userDbState = _userDbState;

        setDevicePreferences("push", push);
        setUserPreferences("login_state", loginState);
        setUserPreferences("user_uuid", userUuid);
        setUserPreferences("user_partition", userPartition);
        setUserPreferences("user_nick", userNick);
        setUserPreferences("user_db_state", userDbState);
    }

    public void setDataLogout() {
        setDevicePreferences("push", PUSH_OFF);
        NetworkManager.getInstance(mContext).clearSession();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        cookieManager.flush();
        clearUserPreferences();
    }

    //endregion

    //region Activity

    public Context mContext;
    public Activity mActivity;
    public boolean isDebug = false;

    public String webUrl;
    public String apiUrl;

    public void setActivity(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        backKeyPressedTime = 0;

        setStatusBarColor(mActivity, R.color.status_bar_color);

        if (BuildConfig.DEBUG) {
            apiUrl = API_URL_DEBUG;
            webUrl = WEB_URL_DEBUG;
            isDebug = true;

        } else {
            apiUrl = API_URL_RELEASE;
            webUrl = WEB_URL_RELEASE;
        }

        // device info
        versionName = getDevicePreferences("version_name", NOW_VERSION_NAME);
        versionCode = getDevicePreferences("version_code", NOW_VERSION_CODE);
        push = getDevicePreferences("push", PUSH_OFF);
        lang = getDevicePreferences("lang", LANG_DEFAULT);
        pushToken = getDevicePreferences("push_token", USER_PUSH_TOKEN_DEFAULT);

        loginState = getUserPreferences("login_state", LOGIN_NULL);
        userUuid = getUserPreferences("user_uuid", USER_UUID_DEFAULT);
        userPartition = getUserPreferences("user_partition", USER_PARTITION_DEFAULT);
        userNick = getUserPreferences("user_nick", USER_NICK_DEFAULT);
        userDbState = getUserPreferences("user_db_state", USER_DB_STATE_DEFAULT);

        if (lang.length() != 2 || !lang.equals("ko")) {
            lang = LANG_DEFAULT;
        }

        setToastMessage();
    }

    public void startActivityClass(Class c) {
        Intent intent = new Intent(mContext, c);
        startActivity(intent);
    }
    public void startActivityClass(Class c, int showId, int hideId) {
        Intent intent = new Intent(mContext, c);
        startActivity(intent);
        overridePendingTransition(showId, hideId);
    }

    public void startActivityResultClass(Class c, int requestCode) {
        Intent intent = new Intent(mContext, c);
        startActivityForResult(intent, requestCode);
    }
    public void startActivityResultClass(Class c, int requestCode, int showId, int hideId) {
        Intent intent = new Intent(mContext, c);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(showId, hideId);
    }
    public void startActivityIntent(Intent intent) {
        startActivity(intent);
    }
    public void startActivityIntent(Intent intent, int showId, int hideId) {
        startActivity(intent);
        overridePendingTransition(showId, hideId);
    }
    public void startActivityIntentForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
    public void startActivityIntentForResult(Intent intent, int requestCode, int showId, int hideId) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(showId, hideId);
    }
    public void finishActivity() {
        finish();
    }
    public void finishActivity(int showId, int hideId) {
        finish();
        overridePendingTransition(showId, hideId);
    }

    public void BackClicked(View v) {
        backActivity();
    }

    public void backActivity() {
        finishActivity(R.anim.animation_stop_short, R.anim.animation_fade_out);
    }

    //endregion

    //region Footer

    public int pageInt;
    public ImageView today_Img, project_Img, calendar_Img, my_Img;
    public ImageView today_indicator_Img, project_indicator_Img, calendar_indicator_Img, my_indicator_Img;

    public void initFooterLayout() {
        today_Img = findViewById(R.id.today_Img);
        today_indicator_Img = findViewById(R.id.today_indicator_Img);
        project_Img = findViewById(R.id.project_Img);
        project_indicator_Img = findViewById(R.id.project_indicator_Img);
        calendar_Img = findViewById(R.id.calendar_Img);
        calendar_indicator_Img = findViewById(R.id.calendar_indicator_Img);
        my_Img = findViewById(R.id.my_Img);
        my_indicator_Img = findViewById(R.id.my_indicator_Img);
    }

    public void setFooterLayout() {
        today_Img.setImageResource(R.drawable.ic_tab_today_default);
        today_indicator_Img.setImageResource(R.drawable.ic_tab_indicator_default);
        project_Img.setImageResource(R.drawable.ic_tab_project_default);
        project_indicator_Img.setImageResource(R.drawable.ic_tab_indicator_default);
        calendar_Img.setImageResource(R.drawable.ic_tab_calendar_default);
        calendar_indicator_Img.setImageResource(R.drawable.ic_tab_indicator_default);
        my_Img.setImageResource(R.drawable.ic_tab_my_default);
        my_indicator_Img.setImageResource(R.drawable.ic_tab_indicator_default);

        switch (pageInt) {
            case TODAY_PAGE:
                today_Img.setImageResource(R.drawable.ic_tab_today_active);
                today_indicator_Img.setImageResource(R.drawable.ic_tab_indicator_active);
                break;
            case PROJECT_PAGE:
                project_Img.setImageResource(R.drawable.ic_tab_project_active);
                project_indicator_Img.setImageResource(R.drawable.ic_tab_indicator_active);
                break;
            case CALENDAR_PAGE:
                calendar_Img.setImageResource(R.drawable.ic_tab_calendar_active);
                calendar_indicator_Img.setImageResource(R.drawable.ic_tab_indicator_active);
                break;
            case MY_PAGE:
                my_Img.setImageResource(R.drawable.ic_tab_my_active);
                my_Img.setImageResource(R.drawable.ic_tab_indicator_active);
                break;
        }
    }

    public void TodayClicked(View v) {

    }

    public void ProjectClicked(View v) {

    }

    public void CalendarClicked(View v) {

    }

    public void MyClicked(View v) {

    }

    //endregion
/*
    //region Web

    public WebView webView;
    public String webUrl;
    public String apiUrl;
    public String nowUrl;

    public boolean isWebDebug;
    public ArrayList<String> beginUrls;
    public boolean isBegin, isAnchor;
    public String postData;
    public static Dialog webDialog;

    public void setWebView(WebView webView) {
        isWebDebug = false;
        webDialog = new Dialog(mContext);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.setWebViewClient(new WebViewClientClass(false));
        webView.setWebChromeClient(new WebChromeClientClass());

        beginUrls = new ArrayList<>();
    }

    public class WebViewClientClass extends WebViewClient {
        public static final String INTENT_PROTOCOL_START = "intent://";
        public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
        public static final String INTENT_PROTOCOL_END = ";end";
        public static final String PACKAGE_PROTOCOL_START = "package=";
        public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

        boolean isPopup;

        public WebViewClientClass(boolean isPopup) {
            this.isPopup = isPopup;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            Log.d(TAG, "should, start Url:"+url);
            if (url.startsWith(INTENT_PROTOCOL_START)) {
                final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                if (customUrlEndIndex < 0) {
                    return false;

                } else {
                    final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                    try {
                        mContext.startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));

                    } catch (URISyntaxException e) {
                        Log.d(TAG, "Uri Error:"+e.toString());

                    }  catch (ActivityNotFoundException e) {
                        Log.d(TAG, "App not found");
                        final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                        final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                        final int packageStartIndex2 = url.indexOf(PACKAGE_PROTOCOL_START) + PACKAGE_PROTOCOL_START.length();
                        final int packageEndIndex2 = url.indexOf(';', packageStartIndex2);

                        final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                        final String packageName2 = url.substring(packageStartIndex2, packageEndIndex2);
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName2)));
                    }
                    return true;
                }

            } else {
                if (isPopup) {
                    Log.d(TAG, "should, Popup Url:"+url);
                    if (checkPopupWebError(url) && checkPopupUrl(webView, url)) {
                        loadUrlWithPost(webView, url);
                    }

                } else {
                    Log.d(TAG, "should, Url:"+url);
                    if (checkWebError(url) && checkAndSaveUrl(webView, url)) {
                        loadUrlWithPost(webView, url);
                    }
                }
                return true;
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            //Log.d(TAG, "request.getRequestHeaders()::"+request.getRequestHeaders());

            return null;
        }

        @Override
        public void onPageStarted(WebView webView, String url, Bitmap favicon) {
            Log.d(TAG, "started, Url:"+url);
            super.onPageStarted(webView, url, favicon);
            onWebStart(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "finished, begin:"+isBegin+" Url:"+url);
            super.onPageFinished(view, url);
            onWebFinish(url);
        }

        @Override
        public void onReceivedError(WebView webView, WebResourceRequest req, WebResourceError rerr) {
            switch (rerr.getErrorCode()) {
                case ERROR_AUTHENTICATION: // 서버에서 사용자 인증 실패
                case ERROR_BAD_URL: // 잘못된 URL
                case ERROR_CONNECT: // 서버로 연결 실패
                case ERROR_FAILED_SSL_HANDSHAKE: // SSL handshake 수행 실패
                case ERROR_FILE: // 일반 파일 오류
                case ERROR_FILE_NOT_FOUND: // 파일을 찾을 수 없습니다
                case ERROR_HOST_LOOKUP: // 서버 또는 프록시 호스트 이름 조회 실패
                case ERROR_IO: // 서버에서 읽거나 서버로 쓰기 실패
                case ERROR_PROXY_AUTHENTICATION: // 프록시에서 사용자 인증 실패
                case ERROR_REDIRECT_LOOP: // 너무 많은 리디렉션
                case ERROR_TIMEOUT: // 연결 시간 초과
                case ERROR_TOO_MANY_REQUESTS: // 페이지 로드중 너무 많은 요청 발생
                case ERROR_UNKNOWN: // 일반 오류
                case ERROR_UNSUPPORTED_AUTH_SCHEME: // 지원되지 않는 인증 체계
                case ERROR_UNSUPPORTED_SCHEME:
                    if (!BuildConfig.DEBUG) {
                        webView.loadUrl(NULL_URL); // 빈페이지 출력
                    }
                    break;
            }
        }
    }
    public void onWebStart(String url) {

    }
    public void onWebFinish(String url) {

    }

    public class WebChromeClientClass extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            webView.requestFocusNodeHref(resultMsg);
            String url = resultMsg.getData().getString("url");

            //웹뷰 다이얼로그 오픈
            WebView newWebView = new WebView(mContext);
            WebSettings webSettings = newWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            newWebView.setWebViewClient(new WebViewClientClass(true));

            newWebView.getSettings().setUseWideViewPort(true);
            newWebView.getSettings().setLoadWithOverviewMode(true);

            webDialog.setContentView(newWebView);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(webDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            webDialog.show();
            webDialog.getWindow().setAttributes(lp);
            newWebView.setWebChromeClient(new WebChromeClientClass() {
                @Override
                public void onCloseWindow(WebView window) {
                    webDialog.dismiss();
                } });
            ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);


            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();

            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            Log.d(TAG, "onJSConfirm");
            new AlertDialog.Builder(mActivity)
                    .setTitle("")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            })
                    .create()
                    .show();

            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String message = consoleMessage.message();
            Log.d(TAG, "Console :"+message);
            if (message.contains("push-0")) {
                push = 0;
                setDevicePreferences("push", PUSH_OFF);
            } else if (message.contains("push-1")) {
                push = 1;
                setDevicePreferences("push", PUSH_ON);
            }
            return super.onConsoleMessage(consoleMessage);
        }
    }

    public boolean checkWebError(String url) {
        return true;
    }
    public boolean checkAndSaveUrl(WebView webView, String url) {
        return true;
    }
    public boolean checkPopupWebError(String url) {
        return true;
    }
    public boolean checkPopupUrl(WebView webView, String url) {
        return true;
    }
    public boolean isBeginUrl(String url) {
        String pureUrl = getPureUrl(url);
        for (String beginUrl : beginUrls) {
            if (pureUrl.equals(beginUrl)) {
                return true;
            }
        }
        return false;
    }
    public String getPureUrl(String url) {
        return removeGetData(removeDomain(url));
    }
    public String removeDomain(String url) {
        int index = url.indexOf(DOMAIN_URL);
        if (index > 0) {
            url = url.substring(index + DOMAIN_URL.length());
        }

        return url;
    }
    public String removeGetData(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        int length = url.length();
        String lastString = url.substring(length-1, length);
        if (lastString.equals("/")) {
            url = url.substring(0, length-1);
        }

        return url;
    }

    public void loadUrlWithPost(WebView view, String url) {
        try {
            postData = "push_token=" + URLEncoder.encode(pushToken, "UTF-8")
                    + "&push=" + URLEncoder.encode(String.valueOf(push), "UTF-8");

        } catch (Exception e) {
            Log.d(TAG, "Error: "+e.toString());
        }

        Log.d(TAG, "loadUrl, Url:"+url+", Post:"+postData);
        view.postUrl(url, postData.getBytes());
    }

    public String getGetData(String url, String key, String _default) {
        if (!url.contains("?")) {
            return _default;
        }

        String needle1 = "?"+key+"=";
        String needle2 = "&"+key+"=";
        int index1 = url.indexOf(needle1);
        int index2 = url.indexOf(needle2);

        if (index1 > 0) {
            String tmp = url.substring(index1 + needle1.length());
            int nextIndex = tmp.indexOf("&");
            if (nextIndex > 0) {
                return tmp.substring(0, nextIndex);
            } else {
                return tmp;
            }

        } else if (index2 > 0) {
            String tmp = url.substring(index2 + needle2.length());
            int nextIndex = tmp.indexOf("&");
            if (nextIndex > 0) {
                return tmp.substring(0, nextIndex);
            } else {
                return tmp;
            }

        } else {
            return _default;
        }
    }

    public void goBack(WebView webView, WebBackForwardList historyList) {
        String backUrl = historyList.getItemAtIndex(historyList.getCurrentIndex() - 1).getUrl();
        Log.d(TAG, "goBack, Url:"+backUrl);
        //String backUrl = webView.getUrl();
        checkAndSaveUrl(webView, backUrl);
        webView.goBack();
    }

    //endregion
*/
    //region ToastMessage

    public static Toast mToast;

    public void setToastMessage() {
        mToast = Toast.makeText(mContext, "null", Toast.LENGTH_SHORT);
    }
    public void showToastMessage(String msg) {
        mToast.setText(msg);
        mToast.show();
    }
    public void showToastMessage(int id) {
        mToast.setText(id);
        mToast.show();
    }

    public void showServerErrorRedo(ErrorCodeEnum errorCodeEnum, int errorCode) {
        if (errorCodeEnum == CODE_UNKNOWN) {
            if (isDebug) {
                String msg = String.format(mContext.getString(R.string.msg_error_debug), String.valueOf(errorCode), "SERVER_ERROR");
                showToastMessage(msg);
            } else {
                showToastMessage(R.string.msg_server_error_redo);
            }
        } else {
            showServerErrorRedo(errorCodeEnum);
        }
    }

    public void showServerErrorRedo(ErrorCodeEnum errorCodeEnum) {
        if (isDebug) {
            String msg = String.format(mContext.getString(R.string.msg_error_debug), String.valueOf(enum2int(errorCodeEnum)), enum2String(errorCodeEnum));
            showToastMessage(msg);
        } else {
            showToastMessage(R.string.msg_server_error_redo);
        }
    }

    public void showServerError(ErrorCodeEnum code) {
        if (isDebug) {
            String msg = String.format(mContext.getString(R.string.msg_error_debug), String.valueOf(enum2int(code)), enum2String(code));
            showToastMessage(msg);
        } else {
            showToastMessage(R.string.msg_server_error);
        }
    }

    public void showServerError(int code) {
        if (isDebug) {
            String msg = String.format(mContext.getString(R.string.msg_error_debug), String.valueOf(code), enum2String(int2Enum(code)));
            showToastMessage(msg);
        } else {
            showToastMessage(R.string.msg_server_error);
        }
    }

    //endregion

    //region UserPreferences

    public int getUserPreferences(String key, int fault) {
        SharedPreferences pref = getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, fault);
    }
    public String getUserPreferences(String key, String fault) {
        SharedPreferences pref = getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, fault);
    }
    public void setUserPreferences(String key, int value) {
        SharedPreferences pref = getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public void setUserPreferences(String key, String value) {
        SharedPreferences pref = getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void clearUserPreferences() {
        SharedPreferences pref = getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public int getViewPreferences(String key, int fault) {
        SharedPreferences pref = getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, fault);
    }
    public String getViewPreferences(String key, String fault) {
        SharedPreferences pref = getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, fault);
    }
    public void setViewPreferences(String key, int value) {
        SharedPreferences pref = getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public void setViewPreferences(String key, String value) {
        SharedPreferences pref = getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void clearViewPreferences() {
        SharedPreferences pref = getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public int getDevicePreferences(String key, int fault) {
        SharedPreferences pref = getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, fault);
    }
    public String getDevicePreferences(String key, String fault) {
        SharedPreferences pref = getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, fault);
    }
    public void setDevicePreferences(String key, int value) {
        SharedPreferences pref = getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public void setDevicePreferences(String key, String value) {
        SharedPreferences pref = getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //endregion

    //region Theme

    public static void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, colorId));
        }
    }

    //endregion

    //region Keyboard

    public boolean isKeyboardHide = true;

    @Override // hide keyboard when touch outside
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) { // when touch outside
                if (isKeyboardHide) {
                    hideSoftKeyboard(mActivity);
                    clearFocusBundle();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    public static void hideSoftKeyboard(Activity activity) { // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    public void clearFocusBundle() {

    }
    public static void showSoftKeyboard(Activity activity, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, 0);
    }

    //endregion

    //region AppExit

    public long backKeyPressedTime;

    public void exitApp() {
        long nowTime = System.currentTimeMillis();
        if (nowTime > backKeyPressedTime + BACK_KEY_DELAY_TIME) {
            backKeyPressedTime = nowTime;
            showToastMessage(R.string.msg_app_exit);

        } else if (nowTime <= backKeyPressedTime + BACK_KEY_DELAY_TIME) {
            mToast.cancel();
            ActivityCompat.finishAffinity(mActivity);
        }
    }

    public void killApp() {
        ActivityCompat.finishAffinity(mActivity);
    }

    //endregion

    //region Social

    public String naverId, kakaoId, googleId;

    // kakao
    public void setKakaoLogout() {
        UserManagement.getInstance()
                .requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Log.d("KAKAO_API", "로그아웃 완료");
                    }
                });
    }

    public void setKakaoDisconnect() {
        UserManagement.getInstance()
                .requestUnlink(new UnLinkResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.d("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.d("KAKAO_API", "연결 끊기 실패: " + errorResult);

                    }
                    @Override
                    public void onSuccess(Long result) {
                        Log.d("KAKAO_API", "연결 끊기 성공. id: " + result);
                    }
                });
    }

    public void getKakaoToken() {
        AuthService.getInstance()
                .requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.d("KAKAO_API", "세션이 닫혀 있음: " + errorResult);

                        resultKakaoToken(CODE_KAKAO_SESSION_CLOSED);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.d("KAKAO_API", "토큰 정보 요청 실패: " + errorResult);

                        resultKakaoToken(CODE_KAKAO_TOKEN_FAIL);
                    }

                    @Override
                    public void onSuccess(AccessTokenInfoResponse result) {
                        Log.d("KAKAO_API", "사용자 아이디: " + result.getUserId());
                        Log.d("KAKAO_API", "남은 시간(s): " + result.getExpiresIn());

                        kakaoId = String.valueOf(result.getUserId());
                        resultKakaoToken(CODE_NORMAL);
                    }
                });
    }

    public void resultKakaoToken(ErrorCodeEnum errorCodeEnum) {
    }


    //endregion

}