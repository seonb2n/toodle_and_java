package com.origincurly.toodletoodle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.origincurly.toodletoodle.util.CheckUtils.isNickNotNull;
import static com.origincurly.toodletoodle.util.CheckUtils.isNotNull;
import static com.origincurly.toodletoodle.util.CheckUtils.validMail;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class LoginActivity extends BasicActivity {

    //region View

    private EditText mail_EditTxt, pw_EditTxt;
    private ImageView pwShow_Img;
    private TextView userMsg_Txt;
    private RelativeLayout loginBtn_Layout;
    private ImageView loginBtn_Img;
    private LoginButton kakao_LoginButton;

    //endregion

    //region Variable

    private String mail, pw;
    private boolean isPwShow = false;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setActivity(this, this);

        init();
    }

    private void init() {
        setDataLogout();

        mail_EditTxt = findViewById(R.id.mail_EditTxt);
        mail_EditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setInputResult();
            }
        });
        pw_EditTxt = findViewById(R.id.pw_EditTxt);
        pw_EditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setInputResult();
            }
        });
        pw_EditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        hideSoftKeyboard(mActivity);
                        clearFocusBundle();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        pwShow_Img = findViewById(R.id.pwShow_Img);
        userMsg_Txt = findViewById(R.id.userMsg_Txt);
        loginBtn_Layout = findViewById(R.id.loginBtn_Layout);
        loginBtn_Img = findViewById(R.id.loginBtn_Img);

        kakao_LoginButton = findViewById(R.id.kakao_LoginButton);

        initKakao();
    }

    private void setInputResult() {
        if (mail_EditTxt.length() < 1 && pw_EditTxt.length() < 1) {
            loginBtn_Layout.setBackgroundResource(R.drawable.custom_submit_btn_off);
            loginBtn_Img.setImageResource(R.drawable.ic_check_off);

        } else {
            loginBtn_Layout.setBackgroundResource(R.drawable.custom_submit_btn_on);
            loginBtn_Img.setImageResource(R.drawable.ic_check_on);
        }
    }

    //region Button

    public void PwShowClicked(View v) {
        if (isPwShow) {
            isPwShow = false;
            pw_EditTxt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            pwShow_Img.setImageResource(R.drawable.ic_hide_off);
            clearFocusBundle();

        } else {
            isPwShow = true;
            pw_EditTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            pwShow_Img.setImageResource(R.drawable.ic_hide_on);
            clearFocusBundle();

        }
    }

    public void LoginClicked(View v) {
        mail = mail_EditTxt.getText().toString();
        pw = pw_EditTxt.getText().toString();

        if (!isNotNull(mail)) {
            showUserMsg(R.string.msg_user_mail_null);
            mail_EditTxt.requestFocus();
            return;
        }

        if (!validMail(mail)) {
            showUserMsg(R.string.msg_user_mail_regex_wrong);
            mail_EditTxt.requestFocus();
            return;
        }

        if (!isNotNull(pw)) {
            showUserMsg(R.string.msg_user_pw_null);
            pw_EditTxt.requestFocus();
            return;
        }

//        new LoginMailTask().execute();
        mockLogin();
    }
    private void mockLogin() {
        checkLoginResult("1"
                , "1"
                , "?????????"
                , 5);
    }
    public void Login2Clicked(View v) {
        mail = "a@a.com";
        pw = "q1111";

        new TestTask().execute();
    }

    public void FindClicked(View v) {
        startActivityClass(FindActivity.class, R.anim.animation_fade_in,  R.anim.animation_stop_short);
        /**
         * TODO find ??????
         * ???????????? ????????????
         * ????????? ?????? ????????? ???????????? (????????? ????????? ????????? ????????????)
         * @@@ ?????? ????????? ???????????????. >> ???????????? ??? ?????? ?????? ????????? ????????? ????????? ?????????.
         * ????????? ???????????? pw_reset = 1??? ?????????, pw_code??? ???????????? ??????????????????. + ???????????? ???????????? 1?????? ?????????????????? ????????? ???????????????.
         * ?????? ????????? ?????? get?????? uuid??? pw_code??? ????????? url??? ?????????
         * ??? ????????? ???????????? uuid??? pw_code??? ????????? ????????? ???????????????. ????????? ????????? ????????????, ??????????????? ?????? ???????????? ????????? ???????????? ???????????? ???????????????.
         * ????????? ???????????? ????????? ?????? (?????????????????? ?????? ????????????)
         * ???????????? ????????? ????????? (??????????????? ???????????????. ?????? ??????????????? ????????????)
         * ??? ????????? ???????????? ??????????????? ????????? ??? ?????? ??????, ????????????
         * pw_reset = 0, code??? ??????, ??????????????? ????????? ?????? pw??? ???????????? ???????????????.
         * pw??? ?????????????????????. ??????????????? ????????? ?????? ??????????????? ?????????. (web??? ????????? ????????? ????????????, ????????? ?????????????????? ?????????.)
         * */
    }

    public void JoinMailClicked(View v) {
        startActivityClass(JoinMailActivity.class, R.anim.animation_fade_in,  R.anim.animation_stop_short);
    }

    public void NaverLoginClicked(View v) {
        showToastMessage("naver");
    }

    public void KakaoLoginClicked(View v) {
        kakao_LoginButton.performClick();
    }

    public void GoogleLoginClicked(View v) {
        showToastMessage("google");
    }

    //endregion

    //region Api
    private class TestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=login_mail");
                RequestBody formBody = new FormBody.Builder()
                        .add("mail", mail)
                        .add("pw", pw)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    private class LoginMailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=login_mail");
                RequestBody formBody = new FormBody.Builder()
                        .add("mail", mail)
                        .add("pw", pw)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        checkLoginResult(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , jsonObject.getString("nick")
                                , jsonObject.getInt("db_state"));
                        break;

                    case CODE_MAIL_WRONG:
                        showUserMsg(R.string.msg_login_mail_wrong);
                        mail_EditTxt.requestFocus();
                        break;

                    case CODE_PW_WRONG:
                        showUserMsg(R.string.msg_login_pw_wrong);
                        pw_EditTxt.requestFocus();
                        break;

                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    private class LoginNaverTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=login_naver");
                RequestBody formBody = new FormBody.Builder()
                        .add("naver_id", naverId)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        checkLoginResult(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , jsonObject.getString("nick")
                                , jsonObject.getInt("db_state"));
                        break;

                    case CODE_NAVER_ID_WRONG:
                        // TODO ????????? ????????? ?????? ??? ??? ??????. ????????????! (?????? ????????? ??????)
                        new JoinNaverTask().execute();

                        break;

                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    private class JoinNaverTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=join_naver");
                RequestBody formBody = new FormBody.Builder()
                        .add("naver_id", naverId)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        checkLoginResult(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , USER_NICK_DEFAULT
                                , USER_DB_STATE_DEFAULT);
                        break;

                    case CODE_NAVER_ID_DUPLICATE:
                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    private class LoginKakaoTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=login_kakao");
                RequestBody formBody = new FormBody.Builder()
                        .add("kakao_id", kakaoId)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "kl_Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        checkLoginResult(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , jsonObject.getString("nick")
                                , jsonObject.getInt("db_state"));
                        break;

                    case CODE_KAKAO_ID_WRONG:
                        // TODO ????????? ????????? ?????? ??? ??? ??????. ????????????! (?????? ????????? ??????)
                        new JoinKakaoTask().execute();

                        break;

                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    private class JoinKakaoTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=join_kakao");
                RequestBody formBody = new FormBody.Builder()
                        .add("kakao_id", kakaoId)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "kj_Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        checkLoginResult(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , USER_NICK_DEFAULT
                                , USER_DB_STATE_DEFAULT);
                        break;

                    case CODE_KAKAO_ID_DUPLICATE:
                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    private class LoginGoogleTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=login_google");
                RequestBody formBody = new FormBody.Builder()
                        .add("google_id", googleId)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        checkLoginResult(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , jsonObject.getString("nick")
                                , jsonObject.getInt("db_state"));
                        break;

                    case CODE_GOOGLE_ID_WRONG:
                        // TODO ????????? ????????? ?????? ??? ??? ??????. ????????????! (?????? ????????? ??????)
                        new JoinGoogleTask().execute();

                        break;

                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    private class JoinGoogleTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=join_google");
                RequestBody formBody = new FormBody.Builder()
                        .add("google_id", googleId)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        checkLoginResult(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , USER_NICK_DEFAULT
                                , USER_DB_STATE_DEFAULT);
                        break;

                    case CODE_GOOGLE_ID_DUPLICATE:
                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    //endregion

    //region KAKAO

    private void initKakao() {
        Session.getCurrentSession().addCallback(sessionKakaoCallback);
    }

    private ISessionCallback sessionKakaoCallback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Log.d("KAKAO_SESSION", "????????? ??????");
            getKakaoToken();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d("KAKAO_SESSION", "????????? ??????", exception);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ?????? ?????? ??????
        Session.getCurrentSession().removeCallback(sessionKakaoCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // ????????????|????????? ??????????????? ?????? ????????? ????????? SDK??? ??????
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    // TODO ?????? ?????? ?????? Override??? ?????? ????????? ????????? ????????? ?????? ????????? ????????? ???????????????
    // TODO ?????? ??????????????? ????????? ??????????????? ????????????.

    @Override
    public void resultKakaoToken(ErrorCodeEnum errorCodeEnum) {

        switch (errorCodeEnum) {
            case CODE_NORMAL:
                new LoginKakaoTask().execute();
                break;
            case CODE_KAKAO_SESSION_CLOSED:
            case CODE_KAKAO_TOKEN_FAIL:
            default:
                showServerErrorRedo(errorCodeEnum);
                break;
        }

    }

    //endregion

    private void showUserMsg(int resId) {
        userMsg_Txt.setVisibility(View.VISIBLE);
        userMsg_Txt.setText(resId);
    }

    private void checkLoginResult(String uuid, String partitionId, String nick, int dbState) {
        setDataLogin(uuid, partitionId, nick, dbState);

        if (isNickNotNull(userNick)) {
            if (userDbState < getDevicePreferences("available_db_state", USER_DB_STATE_DEFAULT)) {
                startActivityClass(DbCheckActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
            } else {
                startActivityClass(TodayWorkActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
            }

        } else {
            startActivityClass(NickSetActivity.class, R.anim.animation_fade_in,  R.anim.animation_stop_short);
        }
    }

    @Override
    public void clearFocusBundle() {
        mail_EditTxt.clearFocus();
        pw_EditTxt.clearFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
        }

        return true;
    }
}