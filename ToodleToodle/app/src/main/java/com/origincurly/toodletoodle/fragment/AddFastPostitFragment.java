package com.origincurly.toodletoodle.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.origincurly.toodletoodle.BasicFragment;
import com.origincurly.toodletoodle.DbCheckActivity;
import com.origincurly.toodletoodle.R;
import com.origincurly.toodletoodle.list.PostitCategoryItem;
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

import static com.origincurly.toodletoodle.util.CheckUtils.validContent;
import static com.origincurly.toodletoodle.util.CheckUtils.validLength;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;
import static com.origincurly.toodletoodle.util.TransformUtils.getDoubleNowTimeStamp;
import static com.origincurly.toodletoodle.util.TransformUtils.iconId2IconResId;

public class AddFastPostitFragment extends BasicFragment {

    //region View

    private EditText title_EditTxt;
    private RelativeLayout titleDelete_Layout;

    private RelativeLayout postitCategoryAdd_Layout;
    private RelativeLayout postitCategorySet_Layout;
    private ImageView postitCategoryIcon_Img;
    private TextView postitCategoryTitle_Txt;
    private TextView postitCategoryContent_Txt;
    private LinearLayout postitCategoryRow_Layout;

    private ArrayList<RelativeLayout> postitCategory_Layouts;
    private ArrayList<ImageView> postitCategory_Imgs;
    private ArrayList<RelativeLayout> postitCategorySelect_Layouts;

    //endregion View


    //region Variable

    public boolean isInit = false;
    private boolean isPostitCategoryLoad = false;

    private String title;
    private int selectPostitCategoryId;
    private String contentJson;

    private ArrayList<PostitCategoryItem> postitCategoryItems;
    private ArrayList<PostitItem> postitItems;

    //endregion

    public AddFastPostitFragment (Context context, Activity activity) {
        setFragment(context, activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_postit, container, false);

        init(v);

        return v;
    }

    private void init(View v) {
        title_EditTxt = v.findViewById(R.id.title_EditTxt);
        title_EditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus) {
                    titleDelete_Layout.setVisibility(View.VISIBLE);

                } else {
                    titleDelete_Layout.setVisibility(View.GONE);

                }
            }
        });
        title_EditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        titleDelete_Layout = v.findViewById(R.id.titleDelete_Layout);

        postitCategoryAdd_Layout = v.findViewById(R.id.postitCategoryAdd_Layout);
        postitCategorySet_Layout = v.findViewById(R.id.postitCategorySet_Layout);
        postitCategoryIcon_Img = v.findViewById(R.id.postitCategoryIcon_Img);
        postitCategoryTitle_Txt = v.findViewById(R.id.postitCategoryTitle_Txt);
        postitCategoryContent_Txt = v.findViewById(R.id.postitCategoryContent_Txt);

        postitCategoryRow_Layout = v.findViewById(R.id.postitCategoryRow_Layout);

        postitCategory_Layouts = new ArrayList<>();
        postitCategory_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategory1_Layout));
        postitCategory_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategory2_Layout));
        postitCategory_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategory3_Layout));
        postitCategory_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategory4_Layout));
        postitCategory_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategory5_Layout));
        postitCategory_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategory6_Layout));
        postitCategory_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategory7_Layout));
        postitCategory_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategory8_Layout));

        postitCategory_Imgs = new ArrayList<>();
        postitCategory_Imgs.add((ImageView) v.findViewById(R.id.postitCategory1_Img));
        postitCategory_Imgs.add((ImageView) v.findViewById(R.id.postitCategory2_Img));
        postitCategory_Imgs.add((ImageView) v.findViewById(R.id.postitCategory3_Img));
        postitCategory_Imgs.add((ImageView) v.findViewById(R.id.postitCategory4_Img));
        postitCategory_Imgs.add((ImageView) v.findViewById(R.id.postitCategory5_Img));
        postitCategory_Imgs.add((ImageView) v.findViewById(R.id.postitCategory6_Img));
        postitCategory_Imgs.add((ImageView) v.findViewById(R.id.postitCategory7_Img));
        postitCategory_Imgs.add((ImageView) v.findViewById(R.id.postitCategory8_Img));

        postitCategorySelect_Layouts = new ArrayList<>();
        postitCategorySelect_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategorySelect1_Layout));
        postitCategorySelect_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategorySelect2_Layout));
        postitCategorySelect_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategorySelect3_Layout));
        postitCategorySelect_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategorySelect4_Layout));
        postitCategorySelect_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategorySelect5_Layout));
        postitCategorySelect_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategorySelect6_Layout));
        postitCategorySelect_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategorySelect7_Layout));
        postitCategorySelect_Layouts.add((RelativeLayout)v.findViewById(R.id.postitCategorySelect8_Layout));

        selectPostitCategoryId = POSTIT_CATEGORY_NO_ID;

        isInit = true;

        new PostitCategoryTask().execute();
    }


    //region Button

    public void PostitTitleDeleteClicked() {
        title_EditTxt.setText("");
        title_EditTxt.requestFocus();
        showSoftKeyboard(mActivity, title_EditTxt);
    }

    public void ProjectCategory1Clicked() {
        setPostitCategory(1);
    }

    public void ProjectCategory2Clicked() {
        setPostitCategory(2);
    }

    public void ProjectCategory3Clicked() {
        setPostitCategory(3);
    }

    public void ProjectCategory4Clicked() {
        setPostitCategory(4);
    }

    public void ProjectCategory5Clicked() {
        setPostitCategory(5);
    }

    public void ProjectCategory6Clicked() {
        setPostitCategory(6);
    }

    public void ProjectCategory7Clicked() {
        setPostitCategory(7);
    }

    public void ProjectCategory8Clicked() {
        setPostitCategory(8);
    }

    public void PostitAddClicked() {
        if (isPostitCategoryLoad) {
            title = title_EditTxt.getText().toString();

            if (title.length() < 1) {
                showToastMessage(R.string.msg_postit_null);

            } else if (!validLength(title, 0, POSTIT_LENGTH_MAX)) {
                showToastMessage(R.string.msg_postit_length_wrong);

            } else if (!validContent(title)) {
                showToastMessage(R.string.msg_postit_regex_wrong);

            } else {
                PostitItem item = new PostitItem();

                item.categoryId = selectPostitCategoryId;
                item.title = title;
                item.content = "";
                item.time = getDoubleNowTimeStamp();

                postitItems.add(item);

                contentJson = getPostitJsonArrayString();

                new PostitSaveTask().execute();
            }
        } else {
            showToastMessage(R.string.msg_postit_category_load);
        }
    }

    //endregion

    private void setPostitCategory(int postitCategoryIndex) {
        if (isPostitCategoryLoad) {
            postitCategoryIndex--;
            for (RelativeLayout relativeLayout: postitCategorySelect_Layouts) {
                relativeLayout.setVisibility(View.GONE);
            }

            if (postitCategoryIndex < postitCategoryItems.size()) {
                PostitCategoryItem item = postitCategoryItems.get(postitCategoryIndex);

                if (item.id == selectPostitCategoryId) {
                    postitCategoryAdd_Layout.setVisibility(View.VISIBLE);
                    postitCategorySet_Layout.setVisibility(View.GONE);

                    selectPostitCategoryId = POSTIT_CATEGORY_NO_ID;

                } else {
                    postitCategoryTitle_Txt.setText(item.title);
                    postitCategoryIcon_Img.setImageResource(item.iconResId);
                    postitCategoryContent_Txt.setText(item.content);
                    postitCategoryAdd_Layout.setVisibility(View.GONE);
                    postitCategorySet_Layout.setVisibility(View.VISIBLE);

                    postitCategorySelect_Layouts.get(postitCategoryIndex).setVisibility(View.VISIBLE);

                    selectPostitCategoryId = item.id;

                }
            }

        } else {
            showToastMessage(R.string.msg_postit_category_load);

        }
    }

    private String getPostitJsonArrayString() {
        StringBuilder stringBuilder = new StringBuilder("[");

        for (int i = postitItems.size()-1; i >= 0; i--) {
            PostitItem item = postitItems.get(i);
            String itemString
                    = "{\"category_id\":\""
                    + item.categoryId
                    + "\", \"title\":\""
                    + item.title
                    + "\", \"content\":\""
                    + item.content
                    + "\", \"time\":"
                    + (long)item.time
                    + "},";
            stringBuilder.append(itemString);
        }
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append("]");

        } else {
            stringBuilder = new StringBuilder("[{}]");
        }

        return stringBuilder.toString();
    }


    //region api

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
        postitCategoryItems = new ArrayList<>();
        for (RelativeLayout relativeLayout: postitCategory_Layouts) {
            relativeLayout.setVisibility(View.INVISIBLE);
        }

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if (i == 8) {
                        break;
                    }

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PostitCategoryItem item = new PostitCategoryItem();

                    item.id = jsonObject.getInt("category_id");
                    item.iconId = jsonObject.getInt("icon_id");
                    item.title = jsonObject.getString("title");
                    item.content = jsonObject.getString("content");

                    item.iconResId = iconId2IconResId(item.iconId);

                    postitCategoryItems.add(item);

                    postitCategory_Imgs.get(i).setImageResource(item.iconResId);
                    postitCategory_Layouts.get(i).setVisibility(View.VISIBLE);

                } catch (JSONException e) { Log.d(TAG, "unexpected JSON exception", e); }
            }
        }

        if (postitCategoryItems.size() > 4) {
            postitCategoryRow_Layout.setVisibility(View.VISIBLE);

        } else {
            postitCategoryRow_Layout.setVisibility(View.GONE);

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
        postitItems = new ArrayList<>();

        if (jsonArray.length() < 1) {
            isPostitCategoryLoad = true;

        } else if (jsonArray.toString().contains("[{}]")) {
            isPostitCategoryLoad = true;

        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PostitItem item = new PostitItem();

                    item.categoryId = jsonObject.getInt("category_id");
                    item.title = jsonObject.getString("title");
                    item.content = jsonObject.getString("content");
                    item.time = jsonObject.getDouble("time");

                    postitItems.add(item);

                } catch (JSONException e) { Log.d(TAG, "unexpected JSON exception", e); }
            }
        }

        isPostitCategoryLoad = true;
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
                        finishActivity(R.anim.animation_stop_short, R.anim.animation_fade_out);
                        showToastMessage(R.string.msg_postit_add_end);
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

    public void clearFocusBundle() {
        if (isInit) {
            title_EditTxt.clearFocus();
        }
    }
}