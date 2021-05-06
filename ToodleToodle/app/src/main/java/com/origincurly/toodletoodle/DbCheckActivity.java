package com.origincurly.toodletoodle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.origincurly.toodletoodle.ui.ProgressBarAnimation;
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

import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class DbCheckActivity extends BasicActivity {

    //region View

    private ProgressBar progress_PrgBar;
    private TextView title_Txt, content_Txt, progress_Txt;

    //endregion

    //region Variable

    private int maxIndex, nowIndex, prevIndex, availableDbState;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_check);
        setActivity(this, this);

        init();
    }

    private void init() {
        title_Txt = findViewById(R.id.title_Txt);
        content_Txt = findViewById(R.id.content_Txt);
        progress_PrgBar = findViewById(R.id.progress_PrgBar);
        progress_Txt = findViewById(R.id.progress_Txt);

        maxIndex = DB_CHECK_SET_STATE;
        nowIndex = DB_CHECK_UUID;
        prevIndex = nowIndex;

        checkDbState();
    }

    private void checkDbState() {
        int from = 100 * prevIndex / maxIndex;
        int to = 100 * nowIndex / maxIndex;
        ProgressBarAnimation anim = new ProgressBarAnimation(progress_PrgBar, from, to);
        anim.setDuration(DB_CHECK_ANIMATION_DURATION);
        progress_PrgBar.startAnimation(anim);
        prevIndex = nowIndex;

        progress_Txt.setText(to+"%");

        switch (nowIndex) {
            case DB_CHECK_UUID:
                title_Txt.setText(R.string.db_check_title_0);
                content_Txt.setText(R.string.db_check_title_0);
                new CheckUserTask().execute();
                break;
            case DB_CHECK_POSTIT:
                title_Txt.setText(R.string.db_check_title_1);
                content_Txt.setText(R.string.db_check_title_1);
                new CheckPostitTask().execute();
                break;
            case DB_ADD_POSTIT:
                title_Txt.setText(R.string.db_check_title_2);
                content_Txt.setText(R.string.db_check_title_2);
                new AddPostitTask().execute();
                break;
            case DB_CHECK_POSTIT_CATEGORY:
                title_Txt.setText(R.string.db_check_title_3);
                content_Txt.setText(R.string.db_check_title_3);
                new CheckPostitCategoryTask().execute();
                break;
            case DB_ADD_POSTIT_CATEGORY:
                title_Txt.setText(R.string.db_check_title_4);
                content_Txt.setText(R.string.db_check_title_4);
                new AddPostitCategoryTask().execute();
                break;
            case DB_CHECK_PROJECT_ID_LIST:
                title_Txt.setText(R.string.db_check_title_5);
                content_Txt.setText(R.string.db_check_title_5);
                new CheckProjectIdListTask().execute();
                break;
            case DB_ADD_PROJECT_ID_LIST:
                title_Txt.setText(R.string.db_check_title_6);
                content_Txt.setText(R.string.db_check_title_6);
                new AddProjectIdListTask().execute();
                break;
            case DB_CHECK_SET_STATE:
                title_Txt.setText(R.string.db_check_title_7);
                content_Txt.setText(R.string.db_check_title_7);

                new SetUserStateTask().execute();
                break;
        }

    }

    private void setDelayTask() {
        delayTaskHandler.sendEmptyMessageDelayed(0, DB_CHECK_DELAY_TIME);
    }

    private Handler delayTaskHandler = new Handler() {
        public void handleMessage(Message msg) {
            //region delayTaskHandler
            checkDbState();
            //endregion
        }
    };

    //region Api
    private class CheckUserTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/db.php?mode=check_user");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
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
                        userPartition = jsonObject.getString("partition_id");
                        userDbState = jsonObject.getInt("db_state");
                        availableDbState = jsonObject.getInt("available_db_state");
                        setDevicePreferences("available_db_state", availableDbState);

                        if (userDbState < availableDbState) {
                            nowIndex = DB_CHECK_POSTIT;
                            setDelayTask();
                        } else {
                            startActivityClass(TodayWorkActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
                        }
                        break;

                    case CODE_DB_CHECK_UUID_WRONG:
                    default:
                        dbCheckError();
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                dbCheckError();
            }
        }
    }

    private class CheckPostitTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/db.php?mode=check_postit");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
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
                        nowIndex = DB_CHECK_POSTIT_CATEGORY;
                        setDelayTask();
                        break;

                    case CODE_DB_CHECK_POSTIT_WRONG:
                        nowIndex = DB_ADD_POSTIT;
                        setDelayTask();

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

    private class AddPostitTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/db.php?mode=add_postit");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
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
                        nowIndex = DB_CHECK_POSTIT_CATEGORY;
                        setDelayTask();
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

    private class CheckPostitCategoryTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/db.php?mode=check_postit_category");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
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
                        nowIndex = DB_CHECK_PROJECT_ID_LIST;
                        setDelayTask();
                        break;

                    case CODE_DB_CHECK_POSTIT_CATEGORY_WRONG:
                        nowIndex = DB_ADD_POSTIT_CATEGORY;
                        setDelayTask();

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

    private class AddPostitCategoryTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/db.php?mode=add_postit_category");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
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
                        nowIndex = DB_CHECK_PROJECT_ID_LIST;
                        setDelayTask();
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

    private class CheckProjectIdListTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/db.php?mode=check_project_id_list");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
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
                        nowIndex = DB_CHECK_SET_STATE;
                        setDelayTask();
                        break;

                    case CODE_DB_CHECK_PROJECT_ID_LIST_WRONG:
                        nowIndex = DB_ADD_PROJECT_ID_LIST;
                        setDelayTask();

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

    private class AddProjectIdListTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/db.php?mode=add_project_id_list");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
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
                        nowIndex = DB_CHECK_SET_STATE;
                        setDelayTask();
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

    private class SetUserStateTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + "user.php?mode=set_db_state");
                RequestBody formBody = new FormBody.Builder()
                        .add("uuid", userUuid)
                        .add("db_state", String.valueOf(availableDbState))
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
                        setUserPreferences("user_db_state", availableDbState);
                        startActivityClass(TodayWorkActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
                        break;

                    default:
                        dbCheckError();
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                dbCheckError();
            }
        }
    }

    //endregion

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
        }

        return true;
    }

    private void dbCheckError() {
        showToastMessage(R.string.msg_db_check_user_wrong);
        startActivityClass(LoginActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
    }
}
