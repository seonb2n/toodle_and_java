package com.origincurly.toodletoodle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.origincurly.toodletoodle.util.ErrorCodeEnum;

import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_UNKNOWN;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.enum2String;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.enum2int;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class BasicFragment extends Fragment implements GlobalValue {

    //region UserInfo

    // device
    public int versionCode, push;
    public String versionName, lang, pushToken; //en,kr / push token

    // user
    public int loginState;
    public String userUuid, userPartition, userNick;
    public int userDbState;

    //endregion

    //region Fragments

    public Context mContext;
    public Activity mActivity;
    public boolean isDebug = false;

    public String webUrl;
    public String apiUrl;

    public void setFragment(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;

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
        mActivity.startActivity(intent);
    }
    public void startActivityClass(Class c, int showId, int hideId) {
        Intent intent = new Intent(mContext, c);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(showId, hideId);
    }

    public void startActivityResultClass(Class c, int requestCode) {
        Intent intent = new Intent(mContext, c);
        mActivity.startActivityForResult(intent, requestCode);
    }
    public void startActivityResultClass(Class c, int requestCode, int showId, int hideId) {
        Intent intent = new Intent(mContext, c);
        mActivity.startActivityForResult(intent, requestCode);
        mActivity.overridePendingTransition(showId, hideId);
    }
    public void startActivityIntent(Intent intent) {
        startActivity(intent);
    }
    public void startActivityIntent(Intent intent, int showId, int hideId) {
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(showId, hideId);
    }
    public void startActivityIntentForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }
    public void startActivityIntentForResult(Intent intent, int requestCode, int showId, int hideId) {
        mActivity.startActivityForResult(intent, requestCode);
        mActivity.overridePendingTransition(showId, hideId);
    }
    public void finishActivity() {
        mActivity.finish();
    }
    public void finishActivity(int showId, int hideId) {
        mActivity.finish();
        mActivity.overridePendingTransition(showId, hideId);
    }

    public void BackClicked(View v) {
        backActivity();
    }

    public void backActivity() {
        finishActivity(R.anim.animation_stop_short, R.anim.animation_fade_out);
    }

    //endregion

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
        SharedPreferences pref = mContext.getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, fault);
    }
    public String getUserPreferences(String key, String fault) {
        SharedPreferences pref = mContext.getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, fault);
    }
    public void setUserPreferences(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public void setUserPreferences(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void clearUserPreferences() {
        SharedPreferences pref = mContext.getSharedPreferences(USER_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public int getViewPreferences(String key, int fault) {
        SharedPreferences pref = mContext.getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, fault);
    }
    public String getViewPreferences(String key, String fault) {
        SharedPreferences pref = mContext.getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, fault);
    }
    public void setViewPreferences(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public void setViewPreferences(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void clearViewPreferences() {
        SharedPreferences pref = mContext.getSharedPreferences(VIEW_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public int getDevicePreferences(String key, int fault) {
        SharedPreferences pref = mContext.getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, fault);
    }
    public String getDevicePreferences(String key, String fault) {
        SharedPreferences pref = mContext.getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, fault);
    }
    public void setDevicePreferences(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public void setDevicePreferences(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //endregion


    public boolean isKeyboardHide = true;

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
}
