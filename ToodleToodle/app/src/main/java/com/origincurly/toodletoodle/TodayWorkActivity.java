package com.origincurly.toodletoodle;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.origincurly.toodletoodle.list.PostitCategoryItem;
import com.origincurly.toodletoodle.list.PostitHorizontalAdapter;
import com.origincurly.toodletoodle.list.PostitItem;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class TodayWorkActivity extends BasicActivity {

    //region View

    private RecyclerView postit_RecyclerView;
    private RelativeLayout postitNull_Layout;

    //endregion

    //region Variable

    private PostitHorizontalAdapter postitHorizontalAdapter;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_work);
        setActivity(this, this);

        init();
    }

    private void init() {
        pageInt = TODAY_PAGE;
        initFooterLayout();
        setFooterLayout();

        postit_RecyclerView = findViewById(R.id.postit_RecyclerView);
        postitNull_Layout = findViewById(R.id.postitNull_Layout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        postit_RecyclerView.setLayoutManager(layoutManager);
    }

    private View.OnClickListener onClickPostitItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityClass(PostitActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        new PostitCategoryTask().execute();
    }

    //region Button

    public void PostitNullClicked(View v) {
        startActivityClass(PostitActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
    }

    public void AddFastClicked(View v) {
        startActivityClass(AddFastActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
    }

    //endregion

    //region Api
    private class PostitCategoryTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/postit_category.php?mode=one_uuid");
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
                        String content_json = jsonObject.getString("content_json");
                        JSONArray jsonArray = new JSONArray(content_json);
                        setPostitCategory(jsonArray);
                        break;

                    case CODE_POSTIT_CATEGORY_WRONG:
                        showToastMessage(R.string.msg_db_check_need);
                        startActivityClass(DbCheckActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
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

    private void setPostitCategory(JSONArray jsonArray) {
        postitHorizontalAdapter = new PostitHorizontalAdapter(mContext, onClickPostitItem);

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int category_id = jsonObject.getInt("category_id");
                    int icon_id = jsonObject.getInt("icon_id");
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");

                    postitHorizontalAdapter.addCategory(category_id, icon_id, title, content);

                } catch (JSONException e) { Log.d(TAG, "unexpected JSON exception", e); }
            }
        }

        new PostitTask().execute();
    }

    private class PostitTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/postit.php?mode=one_uuid");
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
                        String content_json = jsonObject.getString("content_json");
                        JSONArray jsonArray = new JSONArray(content_json);
                        setPostit(jsonArray);
                        break;

                    case CODE_POSTIT_WRONG:
                        showToastMessage(R.string.msg_db_check_need);
                        startActivityClass(DbCheckActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
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
    private void setPostit (JSONArray jsonArray) {
        Log.d(TAG, "length"+jsonArray.length());
        if (jsonArray.length() < 1) {
            postitNull_Layout.setVisibility(View.VISIBLE);

        } else if (jsonArray.toString().contains("[{}]")) {
            postitNull_Layout.setVisibility(View.VISIBLE);

        } else {
            postitNull_Layout.setVisibility(View.GONE);

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int category_id = jsonObject.getInt("category_id");
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");
                    double time = jsonObject.getDouble("time");

                    postitHorizontalAdapter.addItem(category_id, title, content, time);

                } catch (JSONException e) { Log.d(TAG, "unexpected JSON exception", e); }
            }

            postit_RecyclerView.setAdapter(postitHorizontalAdapter);
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
}