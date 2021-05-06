package com.origincurly.toodletoodle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import static com.origincurly.toodletoodle.util.CheckUtils.validMail;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class FindActivity extends BasicActivity {

    //region View

    private EditText mail_EditTxt;
    private RelativeLayout end_Layout;

    //endregion

    //region Variable

    private String mail;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        setActivity(this, this);

        init();
    }

    private void init() {
        mail_EditTxt = findViewById(R.id.mail_EditTxt);
        mail_EditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        end_Layout = findViewById(R.id.end_Layout);
    }

    //region Button

    public void FindClicked(View v) {
        mail = mail_EditTxt.getText().toString();

        if (!isNotNull(mail)) {
            showToastMessage(R.string.msg_user_mail_null);
            mail_EditTxt.requestFocus();
            return;
        }

        if (!validMail(mail)) {
            showToastMessage(R.string.msg_user_mail_regex_wrong);
            mail_EditTxt.requestFocus();
            return;
        }

        new MailResetTask().execute();
    }

    public void FindEndClicked(View v) {
        backActivity();
    }

    //endregion

    //region Api

    private class MailResetTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=reset_pw");
                RequestBody formBody = new FormBody.Builder()
                        .add("mail", mail)
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
                        end_Layout.setVisibility(View.VISIBLE);
                        break;

                    case CODE_PW_RESET_MAIL_NULL:
                        showToastMessage(R.string.msg_find_mail_wrong);
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

    @Override
    public void clearFocusBundle() {
        mail_EditTxt.clearFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backActivity();
        }

        return true;
    }
}