package com.origincurly.toodletoodle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.origincurly.toodletoodle.dialog.NickCancelDialog;
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
import static com.origincurly.toodletoodle.util.CheckUtils.validNick;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class NickSetActivity extends BasicActivity {

    //region View

    private EditText nick_EditTxt;
    private TextView userMsg_Txt;
    private RelativeLayout joinEmailBtn_Layout;
    private ImageView joinEmailBtn_Img;

    //endregion

    //region Variable

    private String nick;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_set);
        setActivity(this, this);

        init();
    }

    private void init() {
        nick_EditTxt = findViewById(R.id.nick_EditTxt);
        nick_EditTxt.addTextChangedListener(new TextWatcher() {
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
        nick_EditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        userMsg_Txt = findViewById(R.id.userMsg_Txt);
        joinEmailBtn_Layout = findViewById(R.id.joinEmailBtn_Layout);
        joinEmailBtn_Img = findViewById(R.id.joinEmailBtn_Img);
    }

    private void setInputResult() {
        if (nick_EditTxt.length() < 1) {
            joinEmailBtn_Layout.setBackgroundResource(R.drawable.custom_submit_btn_default);
            joinEmailBtn_Img.setImageResource(R.drawable.ic_next_default_20);

        } else {
            joinEmailBtn_Layout.setBackgroundResource(R.drawable.custom_submit_btn_active);
            joinEmailBtn_Img.setImageResource(R.drawable.ic_next_active_20);
        }
    }


    //region Button

    public void NickSetClicked(View v) {
        nick = nick_EditTxt.getText().toString();

        if (!isNotNull(nick)) {
            showUserMsg(R.string.msg_user_nick_null);
            nick_EditTxt.requestFocus();
            return;
        }

        if (!validLength(nick, NICK_LENGTH_MIN, NICK_LENGTH_MAX)) {
            showUserMsg(R.string.msg_user_nick_length_wrong);
            nick_EditTxt.requestFocus();
            return;
        }

        if (!validNick(nick)) {
            showUserMsg(R.string.msg_user_nick_regex_wrong);
            nick_EditTxt.requestFocus();
            return;
        }

        if (nick.contains(USER_NICK_DEFAULT)) {
            showUserMsg(R.string.msg_user_nick_wrong);
            nick_EditTxt.requestFocus();
            return;
        }

        new NickSetTask().execute();

    }

    //endregion

    //region Api

    private class NickSetTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=set_nick");


                RequestBody formBody = new FormBody.Builder()
                        .add("uuid", userUuid)
                        .add("nick", nick)
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
                        setUserPreferences("user_nick", nick);

                        if (userDbState < getDevicePreferences("available_db_state", USER_DB_STATE_DEFAULT)) {
                            startActivityClass(DbCheckActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
                        } else {
                            startActivityClass(TodayWorkActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
                        }
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
        nick_EditTxt.clearFocus();
    }

    @Override
    public void backActivity() {
        nickCancelDialog = new NickCancelDialog(mContext, nickCancelNegativeClickListener, nickCancelPositiveClickListener);
        nickCancelDialog.show();
    }

    private NickCancelDialog nickCancelDialog;
    public View.OnClickListener nickCancelNegativeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nickCancelDialog.dismiss();
        }
    };
    public View.OnClickListener nickCancelPositiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nickCancelDialog.dismiss();
            startActivityClass(LoginActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backActivity();
        }

        return true;
    }
}
