package com.origincurly.toodletoodle;

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

import static com.origincurly.toodletoodle.util.CheckUtils.isNotNull;
import static com.origincurly.toodletoodle.util.CheckUtils.validLength;
import static com.origincurly.toodletoodle.util.CheckUtils.validMail;
import static com.origincurly.toodletoodle.util.CheckUtils.validPw;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class JoinMailActivity extends BasicActivity {

    //region View

    private EditText mail_EditTxt, pw_EditTxt, pwRe_EditTxt;
    private ImageView pwShow_Img, pwReShow_Img;
    private TextView userMsg_Txt;
    private RelativeLayout joinEmailBtn_Layout;
    private ImageView joinEmailBtn_Img;

    //endregion

    //region Variable

    private String mail, pw, pwRe;
    private boolean isPwShow = false, isPwReShow = false, isAgree = true;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_mail);
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
        pwRe_EditTxt = findViewById(R.id.pwRe_EditTxt);
        pwRe_EditTxt.addTextChangedListener(new TextWatcher() {
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
        pwRe_EditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        pwReShow_Img = findViewById(R.id.pwReShow_Img);
        userMsg_Txt = findViewById(R.id.userMsg_Txt);
        joinEmailBtn_Layout = findViewById(R.id.joinEmailBtn_Layout);
        joinEmailBtn_Img = findViewById(R.id.joinEmailBtn_Img);

    }

    private void setInputResult() {
        if (mail_EditTxt.length() < 1 && pw_EditTxt.length() < 1 && pwRe_EditTxt.length() < 1 && isAgree) {
            joinEmailBtn_Layout.setBackgroundResource(R.drawable.custom_submit_btn_default);
            joinEmailBtn_Img.setImageResource(R.drawable.ic_check_default_20);

        } else {
            joinEmailBtn_Layout.setBackgroundResource(R.drawable.custom_submit_btn_active);
            joinEmailBtn_Img.setImageResource(R.drawable.ic_check_active_20);
        }
    }

    //region Button

    public void PwShowClicked(View v) {
        if (isPwShow) {
            isPwShow = false;
            pw_EditTxt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            pwShow_Img.setImageResource(R.drawable.ic_task_look_on_20);
            clearFocusBundle();

        } else {
            isPwShow = true;
            pw_EditTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            pwShow_Img.setImageResource(R.drawable.ic_task_look_off_20);
            clearFocusBundle();

        }
    }

    public void PwReShowClicked(View v) {
        if (isPwReShow) {
            isPwReShow = false;
            pwRe_EditTxt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            pwReShow_Img.setImageResource(R.drawable.ic_task_look_on_20);
            clearFocusBundle();

        } else {
            isPwReShow = true;
            pwRe_EditTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            pwReShow_Img.setImageResource(R.drawable.ic_task_look_off_20);
            clearFocusBundle();

        }
    }

    public void JoinMailClicked(View v) {
        mail = mail_EditTxt.getText().toString();
        pw = pw_EditTxt.getText().toString();
        pwRe = pwRe_EditTxt.getText().toString();

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

        if (!validLength(pw, PW_LENGTH_MIN, PW_LENGTH_MAX)) {
            showUserMsg(R.string.msg_user_pw_length_wrong);
            pw_EditTxt.requestFocus();
            return;
        }

        if (!validPw(pw)) {
            showUserMsg(R.string.msg_user_pw_regex_wrong);
            pw_EditTxt.requestFocus();
            return;
        }

        if (!isNotNull(pwRe)) {
            showUserMsg(R.string.msg_user_pw_re_null);
            pwRe_EditTxt.requestFocus();
            return;
        }

        if (!pwRe.equals(pw)) {
            showUserMsg(R.string.msg_user_pw_re_wrong);
            pwRe_EditTxt.requestFocus();
            return;
        }

        new JoinMailTask().execute();
    }

    //endregion

    //region Api

    private class JoinMailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=join_mail");
                RequestBody formBody = new FormBody.Builder()
                        .add("mail", mail)
                        .add("pw", pw)
                        .add("pw_re", pwRe)
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
                        setDataLogin(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , USER_NICK_DEFAULT
                                , USER_DB_STATE_DEFAULT);
                        startActivityClass(NickSetActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
                        break;

                    case CODE_MAIL_DUPLICATE:
                        showUserMsg(R.string.msg_join_mail_mail_duplicate);
                        mail_EditTxt.requestFocus();
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

    //endregion

    private void showUserMsg(int resId) {
        userMsg_Txt.setVisibility(View.VISIBLE);
        userMsg_Txt.setText(resId);
    }

    @Override
    public void clearFocusBundle() {
        mail_EditTxt.clearFocus();
        pw_EditTxt.clearFocus();
        pwRe_EditTxt.clearFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backActivity();
        }

        return true;
    }
}
