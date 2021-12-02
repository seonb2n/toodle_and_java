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
                , "닉네임"
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
         * TODO find 로직
         * 이메일을 입력하면
         * 이메일 존재 여부를 확인하고 (없으면 가입된 메일이 없습니다)
         * @@@ 에게 메일을 보냈습니다. >> 비밀번호 재 설정 하는 링크를 첨부한 메일을 보낸다.
         * 첨부한 링크에는 pw_reset = 1로 바뀌며, pw_code를 랜덤으로 발급해둡니다. + 현재시간 기준으로 1일을 유효시간으로 추가로 등록합니다.
         * 메일 링크로 가면 get으로 uuid와 pw_code를 기록한 url이 있으며
         * 이 링크로 들어오면 uuid와 pw_code로 무결성 여부를 확인합니다. 무결성 여부가 통과되면, 유효시간을 보고 판단하여 만료된 요청인지 아닌지를 확인합니다.
         * 만료된 요청이면 튕기게 하고 (만료되었으니 다시 신청해라)
         * 무결성에 에러가 있으면 (비정상적인 접근입니다. 다시 신청하거나 확인해라)
         * 이 모든게 통과되면 비밀번호를 변경할 수 있게 하고, 변경하면
         * pw_reset = 0, code도 리셋, 유효시간은 그대로 두고 pw를 변경하며 종결됩니다.
         * pw가 변경되었습니다. 로그인으로 가셔서 계속 이용하시면 됩니다. (web이 있다면 로그인 페이지로, 없다면 확인페이지로 닫는다.)
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
                        // TODO 분기를 하고자 하면 할 수 있다. 가입해라! (약관 등등이 필요)
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
                        // TODO 분기를 하고자 하면 할 수 있다. 가입해라! (약관 등등이 필요)
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
                        // TODO 분기를 하고자 하면 할 수 있다. 가입해라! (약관 등등이 필요)
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
            Log.d("KAKAO_SESSION", "로그인 성공");
            getKakaoToken();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d("KAKAO_SESSION", "로그인 실패", exception);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionKakaoCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    // TODO 아래 토큰 함수 Override와 함께 로그인 할때의 가입과 탈퇴 등등의 로직을 설계해둔다
    // TODO 이와 마찬가지로 소셜도 연결해두면 좋습니다.

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