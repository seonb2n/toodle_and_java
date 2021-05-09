package com.origincurly.toodletoodle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.origincurly.toodletoodle.dialog.VersionUpdateDialog;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.origincurly.toodletoodle.util.CheckUtils.isNickNotNull;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_VERSION_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class IntroActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        setActivity(this, this);

        lang = Locale.getDefault().getLanguage();
        setDevicePreferences("lang", lang);

        /*
        String a = "q1111";
        Log.d(TAG, "FUCK TEST:"+validPw(a));
        */
        if (isDebug) {
            String keyHash = com.kakao.util.helper.Utility.getKeyHash(mContext);
            Log.d(TAG, "key_hash:"+keyHash);
        }

        new VersionCheckTask().execute();
    }

    //region Version

    private class VersionCheckTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "version_and.php");
                RequestBody formBody = new FormBody.Builder()
                        .add("name", PACKAGE_NAME)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
                serverErrorFinish(CODE_VERSION_ERROR);
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Version Task:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                ErrorCodeEnum errorCodeEnum = int2Enum(jsonObject.getInt("code"));

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        String availableVersionName = jsonObject.getString("available_version_name");
                        int availableDbState = jsonObject.getInt("available_db_state");
                        if (availableDbState != USER_DB_STATE_DEFAULT) {
                            setDevicePreferences("available_db_state", availableDbState);
                        }

                        if (isNeedUpdate(availableVersionName)) {
                            updateVersion();
                        } else {
                            getToken();
                        }
                        break;

                    default:
                        serverErrorFinish(errorCodeEnum);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                serverErrorFinish(CODE_JSON_ERROR);
            }
        }
    }

    private boolean isNeedUpdate(String availableVersionName) {
        String nowVersionName = NOW_VERSION_NAME;

        if (availableVersionName.length() > 1) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
                nowVersionName = packageInfo.versionName;
                setDevicePreferences("version_name", nowVersionName);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            String[] now = nowVersionName.split("\\.");
            String[] available = availableVersionName.split("\\.");

            if (now.length == available.length) {
                for (int i = 0; i < now.length; i++) {
                    if (Integer.parseInt(now[i]) < Integer.parseInt(available[i])) {
                        return true;
                    } else if (Integer.parseInt(now[i]) > Integer.parseInt(available[i])) {
                        break;
                    }
                }

            } else {
                return true;
            }
        }

        return false;
    }

    private void updateVersion() {
        versionUpdateDialog = new VersionUpdateDialog(mContext, versionUpdateNegativeClickListener, versionUpdatePositiveClickListener);
        versionUpdateDialog.show();
    }

    private VersionUpdateDialog versionUpdateDialog;
    public View.OnClickListener versionUpdateNegativeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            versionUpdateDialog.dismiss();
            ActivityCompat.finishAffinity(mActivity);
        }
    };
    public View.OnClickListener versionUpdatePositiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            versionUpdateDialog.dismiss();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME));
            startActivityIntentForResult(intent, GOOGLE_STORE_ACTIVITY);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GOOGLE_STORE_ACTIVITY:
                startActivityClass(IntroActivity.class);
                break;

            default:
                break;
        }
    }

    //endregion

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            getToken();
                            return;
                        }

                        try {
                            pushToken = task.getResult().getToken();

                            if (pushToken.length() > 1) {
                                setDevicePreferences("push_token", pushToken); // 늘 토큰 갱신
                                Log.d(TAG, pushToken);
                                checkLogin();
                            }
                        } catch (NullPointerException e) {
                            getToken();
                        }
                    }
                });
    }

    private void checkLogin() {
        if (loginState == LOGIN_EXIST) {
            new UserCheckTask().execute();

        } else {
            startDelayActivity(LoginActivity.class);
            //TODO help view 여부를 확인하여 Welcome Page를 이동하게 한다.
        }
    }

    private class UserCheckTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=check");
                RequestBody formBody = new FormBody.Builder()
                        .add("uuid", userUuid)
                        .add("push_token", pushToken)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
                startDelayActivity(LoginActivity.class);
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                ErrorCodeEnum errorCodeEnum = int2Enum(jsonObject.getInt("code"));

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        checkLoginResult(jsonObject.getString("uuid")
                                , jsonObject.getString("partition_id")
                                , jsonObject.getString("nick")
                                , jsonObject.getInt("db_state"));
                        break;

                    default:
                        startDelayActivity(LoginActivity.class);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                startDelayActivity(LoginActivity.class);
            }
        }
    }

    private void checkLoginResult(String uuid, String partitionId, String nick, int state) {
        setDataLogin(uuid, partitionId, nick, state);

        if (isNickNotNull(userNick)) {
            if (userDbState < getDevicePreferences("available_db_state", USER_DB_STATE_DEFAULT)) {
                startDelayActivity(DbCheckActivity.class);
            } else {
                startDelayActivity(TodayWorkActivity.class);
            }

        } else {
            startDelayActivity(NickSetActivity.class);
        }
    } 

    private Intent introActivityIntent;

    private void startDelayActivity(Class c) {
        introActivityIntent = new Intent(mContext, c);
        delayActivityHandler.sendEmptyMessageDelayed(0, INTRO_DELAY_TIME);
    }

    private Handler delayActivityHandler = new Handler() {
        public void handleMessage(Message msg) {
            //region delayActivityHandler
            startActivityIntent(introActivityIntent, R.anim.animation_fade_in, R.anim.animation_stop_short);
            //endregion
        }
    };

    //region Server

    private void serverErrorFinish(ErrorCodeEnum errorCodeEnum) {
        showServerError(errorCodeEnum);
        killApp();
    }


    //endregion

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false; // block
        }

        return true; // give next func
    }
}