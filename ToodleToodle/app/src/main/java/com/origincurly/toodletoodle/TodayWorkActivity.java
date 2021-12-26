package com.origincurly.toodletoodle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.origincurly.toodletoodle.list.PostitHorizontalAdapter;
import com.origincurly.toodletoodle.list.TodayWorkAdapter;
import com.origincurly.toodletoodle.list.TodayWorkCardViewItem;
import com.origincurly.toodletoodle.list.TodayWorkToDoItem;
import com.origincurly.toodletoodle.ui.CustomSeekBar;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private RecyclerView todoCardView_RecyclerView;

    //endregion

    //region Variable

    private PostitHorizontalAdapter postitHorizontalAdapter;
    private TodayWorkAdapter todayWorkAdapter;

    private TextView todayTimeNowTextView;

    private LinearLayoutManager todayCardViewLayoutManager;
    private CustomSeekBar todayTimeSeekBar;
    private RelativeLayout cardView_null_layout;

    //endregion

    //cardView 용 mock data
    private List<TodayWorkCardViewItem> todayWorkCardViewItems;

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

        todoCardView_RecyclerView = findViewById(R.id.today_work_RecyclerView);
        todayCardViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        todoCardView_RecyclerView.setLayoutManager(todayCardViewLayoutManager);

        new TodayWorkTask().execute();
        todayTimeNowTextView = findViewById(R.id.today_time_start_textView);
        todayTimeSeekBar = findViewById(R.id.today_time_seekBar);
        todayTimeSeekBar.setEnabled(false);
        cardView_null_layout = findViewById(R.id.cardView_null_layout);
    }

    private void showWorkTime(String startTime, String endTime) {
        String result = startTime + " - " + endTime;
        todayTimeNowTextView.setText(result);
    }

    private String convertDateTimeToString(LocalDateTime localDateTime) {
        return String.valueOf(localDateTime.getHour())+":" + String.valueOf(localDateTime.getMinute());
    }

    private int convertDateTimeToInt(LocalDateTime localDateTime) {
        return (int)localDateTime.getHour();
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

    //today work task
    private class TodayWorkTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);
            setCardView(null);
        }
    }

    private void setCardView(JSONArray jsonArray) {

        if(jsonArray ==  null) {
            cardView_null_layout.setVisibility(View.VISIBLE);
        } else {
            //mock data
            cardView_null_layout.setVisibility(View.GONE);
            todayWorkCardViewItems = new ArrayList<>();
            todayWorkAdapter = new TodayWorkAdapter(mContext, todayWorkCardViewItems);
        }

        new CardViewTask().execute();

    }

    private class CardViewTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return "0";
        }

        protected void onPostExecute(String value) {
            setTodayWorkCardView(null);
        }
    }

    private void setTodayWorkCardView (JSONArray jsonArray) {
        todoCardView_RecyclerView.setAdapter(todayWorkAdapter);
        todoCardView_RecyclerView.addOnScrollListener(cardViewOnScrollListener);
    }

    //recyclerview item 가운데에 자동으로 맞춰지게 하는 코드
    private RecyclerView.OnScrollListener cardViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //scroll 이 멈추면 아래 코드 실행
                int firstPos = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstCompletelyVisibleItemPosition();
                int secondPos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int selectedPos = Math.max(firstPos, secondPos);

                if(selectedPos != -1 && selectedPos != (todayWorkCardViewItems.size() - 1) && (firstPos - secondPos) != 0) {
                    View viewItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findViewByPosition(selectedPos);
                    if(viewItem != null) {
                        int itemMargin = (recyclerView.getMeasuredWidth() - viewItem.getMeasuredWidth()) / 2;
                        recyclerView.smoothScrollBy((int) viewItem.getX() - itemMargin, 0);
                    }
                }

                //cardview 움직임에 따라 시간과 seek bar progress 변경
                if(((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstCompletelyVisibleItemPosition() != -1) {
                    int nowPos = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstCompletelyVisibleItemPosition();
                    String nowStartTime = convertDateTimeToString(todayWorkCardViewItems.get(nowPos).startAt);
                    String nowEndTime = convertDateTimeToString(todayWorkCardViewItems.get(nowPos).endAt);
                    showWorkTime(nowStartTime, nowEndTime);
                    todayTimeSeekBar.setOverlayText(nowStartTime);
                    todayTimeSeekBar.setProgress(convertDateTimeToInt(todayWorkCardViewItems.get(nowPos).startAt), true);
                }
            }
        }
    };

    //endregion

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
        }

        return true;
    }
}