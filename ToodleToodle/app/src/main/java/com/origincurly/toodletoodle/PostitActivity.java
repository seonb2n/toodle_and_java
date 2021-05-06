package com.origincurly.toodletoodle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.origincurly.toodletoodle.dialog.PostitCancelDialog;
import com.origincurly.toodletoodle.dialog.PostitCategorySelectDialog;
import com.origincurly.toodletoodle.list.PostitCategoryItem;
import com.origincurly.toodletoodle.list.PostitVerticalAdapter;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.origincurly.toodletoodle.util.CheckUtils.validContent;
import static com.origincurly.toodletoodle.util.CheckUtils.validLength;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;
import static com.origincurly.toodletoodle.util.TransformUtils.getDoubleNowTimeStamp;

public class PostitActivity extends BasicActivity {

    //region View

    private ImageView back_Img;
    private ListView postit_ListView;
    private RelativeLayout postitNull_Layout;
    private RelativeLayout addReady_Layout;
    private RelativeLayout add_Layout;
    private EditText add_EditTxt;
    private TextView addPostitCategory_Txt;
    private TextView addTextCount_Txt;
    private TextView postitMsg_Txt;

    //endregion

    //region Variable

    private PostitVerticalAdapter postitVerticalAdapter;
    private PostitCategorySelectDialog postitCategorySelectDialog;
    private boolean isAdd = false;
    private String contentJson;

    private int selectPostitCategoryId;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postit);
        setActivity(this, this);

        init();
    }

    private void init() {
        back_Img = findViewById(R.id.back_Img);
        postit_ListView = findViewById(R.id.postit_ListView);
        postitNull_Layout = findViewById(R.id.postitNull_Layout);
        addReady_Layout = findViewById(R.id.addReady_Layout);
        add_Layout = findViewById(R.id.add_Layout);
        add_EditTxt = findViewById(R.id.add_EditTxt);
        addPostitCategory_Txt = findViewById(R.id.addPostitCategory_Txt);
        addTextCount_Txt = findViewById(R.id.addTextCount_Txt);
        postitMsg_Txt = findViewById(R.id.postitMsg_Txt);

        add_EditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > POSTIT_LENGTH_MAX) {
                    add_EditTxt.setText(s.toString().substring(0, POSTIT_LENGTH_MAX));
                    add_EditTxt.setSelection(add_EditTxt.length());
                    String count = POSTIT_LENGTH_MAX+"/"+POSTIT_LENGTH_MAX;
                    addTextCount_Txt.setText(count);

                } else {
                    String count = s.length()+"/"+POSTIT_LENGTH_MAX;
                    addTextCount_Txt.setText(count);

                }
            }
        });
        add_EditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        postit_ListView.setDividerHeight(0);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isAdd && !isPostitEdit()) {
            new PostitCategoryTask().execute();
        }
    }

    //region Button

    public void SaveClicked(View v) {
        if (isAdd) {
            addPostit();

        } else if (isPostitEdit()) {
            savePostit();

        } else {
            showPostitMessage(R.string.msg_postit_save);

        }
    }

    public void AddClicked(View v) {
        if (!isAdd) {
            showAddLayout();
        }
    }

    public void CategoryClicked(View v) {
        if (isAdd) {
            postitCategorySelectDialog = new PostitCategorySelectDialog(postitVerticalAdapter.getPostCategoryList(), selectPostitCategoryId, postitCategoryItemClicked);
            postitCategorySelectDialog.show(getSupportFragmentManager(), "postitCategorySelect");
        }
    }
    private AdapterView.OnItemClickListener postitCategoryItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PostitCategoryItem item = postitVerticalAdapter.getCategoryItem(position);
            selectPostitCategoryId = item.id;
            addPostitCategory_Txt.setText(item.title);
            addPostitCategory_Txt.setText(item.title);
            postitCategorySelectDialog.dismiss();
        }
    };

    public void MaskClicked(View v) {
        if (isAdd) {
            hideAddLayout();
        }
    }

    @Override
    public void BackClicked(View v) {
        if (!isAdd) {
            if (isPostitEdit()) {
                checkSaveOrNot();

            } else {
                backActivity();

            }
        }
    }

    //endregion

    private void showPostitMessage(int msgId) {
        postitMsg_Txt.setText(msgId);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_bottom_msg);
        postitMsg_Txt.clearAnimation();
        postitMsg_Txt.startAnimation(animation);
        postitMsg_Txt.setVisibility(View.VISIBLE);
    }

    //region AddLayout

    private void showAddLayout() {
        isAdd = true;
        selectPostitCategoryId = POSTIT_CATEGORY_NO_ID;
        add_EditTxt.setText("");
        addPostitCategory_Txt.setText(R.string.postit_category_hint);

        back_Img.setVisibility(View.GONE);
        addReady_Layout.setVisibility(View.GONE);
        add_Layout.setVisibility(View.VISIBLE);
        add_EditTxt.requestFocus();
        showSoftKeyboard(mActivity, add_EditTxt);
    }

    private void hideAddLayout() {
        isAdd = false;
        selectPostitCategoryId = POSTIT_CATEGORY_NO_ID;
        add_EditTxt.setText("");
        addPostitCategory_Txt.setText(R.string.postit_category_hint);

        back_Img.setVisibility(View.VISIBLE);
        addReady_Layout.setVisibility(View.VISIBLE);
        add_Layout.setVisibility(View.GONE);
        add_EditTxt.clearFocus();
    }

    private void addPostit() {
        String title = add_EditTxt.getText().toString();

        if (title.length() < 1) {
            showToastMessage(R.string.msg_postit_null);

        } else if (!validLength(title, 0, POSTIT_LENGTH_MAX)) {
            showToastMessage(R.string.msg_postit_length_wrong);

        } else if (!validContent(title)) {
            showToastMessage(R.string.msg_postit_regex_wrong);

        } else {
            postitVerticalAdapter.addItem(selectPostitCategoryId, title, "", getDoubleNowTimeStamp(), POSTIT_STATE_NEW);
            postitVerticalAdapter.setFirstAnimation();
            postitVerticalAdapter.notifyDataSetChanged();
            postit_ListView.smoothScrollToPosition(0);

            postitNull_Layout.setVisibility(View.GONE);

            hideAddLayout();

            showPostitMessage(R.string.msg_postit_add);

        }
    }

    private void savePostit() {
        contentJson = postitVerticalAdapter.getJsonArrayString();
        new PostitSaveTask().execute();
    }

    private boolean isPostitEdit() {
        if (postitVerticalAdapter == null ) {
            return false;

        } else {
            return postitVerticalAdapter.isEditItem();

        }
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
        postitVerticalAdapter = new PostitVerticalAdapter();
        postitVerticalAdapter.addPostitCategory(POSTIT_CATEGORY_NO_ID, POSTIT_CATEGORY_NO_ID, "", "");

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int category_id = jsonObject.getInt("category_id");
                    int icon_id = jsonObject.getInt("icon_id");
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");

                    postitVerticalAdapter.addPostitCategory(category_id, icon_id, title, content);

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

                    postitVerticalAdapter.addItem(category_id, title, content, time, POSTIT_STATE_NORMAL);

                } catch (JSONException e) { Log.d(TAG, "unexpected JSON exception", e); }
            }
        }

        postit_ListView.setAdapter(postitVerticalAdapter);
    }

    private class PostitSaveTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/postit.php?mode=edit");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
                        .add("content_json", contentJson)
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
                        showPostitMessage(R.string.msg_postit_save);
                        new PostitCategoryTask().execute();
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
        add_EditTxt.clearFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isAdd) {
                hideAddLayout();

            } else {
                if (isPostitEdit()) {
                    checkSaveOrNot();

                } else {
                    backActivity();

                }
            }
        }

        return true;
    }

    private void checkSaveOrNot() {
        postitCancelDialog = new PostitCancelDialog(mContext, postitCancelNegativeClickListener, postitCancelPositiveClickListener);
        postitCancelDialog.show();
    }

    private PostitCancelDialog postitCancelDialog;
    public View.OnClickListener postitCancelNegativeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postitCancelDialog.dismiss();
        }
    };
    public View.OnClickListener postitCancelPositiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postitCancelDialog.dismiss();
            backActivity();
        }
    };
}