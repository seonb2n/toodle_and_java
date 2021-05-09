package com.origincurly.toodletoodle.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.origincurly.toodletoodle.BasicFragment;
import com.origincurly.toodletoodle.DbCheckActivity;
import com.origincurly.toodletoodle.R;
import com.origincurly.toodletoodle.dialog.ProjectSelectDialog;
import com.origincurly.toodletoodle.list.ActionItem;
import com.origincurly.toodletoodle.list.ProjectItem;
import com.origincurly.toodletoodle.list.TaskDuringHorizontalAdapter;
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

import static com.origincurly.toodletoodle.util.CheckUtils.isNotNull;
import static com.origincurly.toodletoodle.util.CheckUtils.validLength;
import static com.origincurly.toodletoodle.util.CheckUtils.validNumber;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;
import static com.origincurly.toodletoodle.util.TransformUtils.colorId2CircleResId;
import static com.origincurly.toodletoodle.util.TransformUtils.getDoubleNowTimeStamp;
import static com.origincurly.toodletoodle.util.TransformUtils.int2TwoChar;
import static com.origincurly.toodletoodle.util.TransformUtils.removeNotNumber;
import static com.origincurly.toodletoodle.util.TransformUtils.timeStamp2DateString;

public class AddFastTaskFragment extends BasicFragment {

    //region View

    private ScrollView addTask_ScrollView;

    private EditText title_EditTxt;
    private RelativeLayout titleDelete_Layout;

    private RelativeLayout taskImportanceSelect5_Layout;
    private RelativeLayout taskImportanceSelect3_Layout;
    private RelativeLayout taskImportanceSelect1_Layout;

    private ImageView selectProject_Img;
    private TextView selectProject_Txt;

    private TextView taskEndDate_Txt;
    private TextView taskDuring_Txt;
    private RelativeLayout taskDuringEdit_Layout;
    private EditText taskDuring_EditTxt;
    private RelativeLayout taskDuringShadow_Layout;
    private RecyclerView taskDuring_RecyclerView;

    private RelativeLayout action_Layout;
    private LinearLayout actionItem_Layout;
    private TextView addTaskAction_Txt;

    //endregion View


    //region Variable

    public boolean isInit = false;
    private boolean isProjectLoad = false;

    private String title;
    private int selectImportance;
    private int selectProjectId;
    private String contentJson;
    private int isSetDate;
    private double nowTimeStamp, selectTimeStamp;
    private int selectDuring, selectYear, selectMonth, selectDay;
    private int actionIdCount;

    private ArrayList<ProjectItem> projectItems;
    private ProjectSelectDialog projectSelectDialog;
    private TaskDuringHorizontalAdapter taskDuringHorizontalAdapter;
    private ArrayList<View> actionItemLayoutList;
    private ArrayList<EditText> actionTitleEditTxtList;
    private ArrayList<ActionItem> actionItemList;

    //endregion

    public AddFastTaskFragment (Context context, Activity activity) {
        setFragment(context, activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_task, container, false);

        init(v);

        return v;
    }

    private void init(View v) {
        addTask_ScrollView = v.findViewById(R.id.addTask_ScrollView);

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

        taskImportanceSelect5_Layout = v.findViewById(R.id.taskImportanceSelect5_Layout);
        taskImportanceSelect3_Layout = v.findViewById(R.id.taskImportanceSelect3_Layout);
        taskImportanceSelect1_Layout = v.findViewById(R.id.taskImportanceSelect1_Layout);
        setImportance(TASK_IMPORTANCE_MEDIUM);

        selectProject_Img = v.findViewById(R.id.selectProject_Img);
        selectProject_Txt = v.findViewById(R.id.selectProject_Txt);

        taskEndDate_Txt = v.findViewById(R.id.taskEndDate_Txt);
        taskDuring_Txt = v.findViewById(R.id.taskDuring_Txt);
        taskDuringEdit_Layout = v.findViewById(R.id.taskDuringEdit_Layout);
        taskDuring_EditTxt = v.findViewById(R.id.taskDuring_EditTxt);
        taskDuring_EditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus) {
                    taskDuringEdit_Layout.setVisibility(View.VISIBLE);

                } else {
                    taskDuringEdit_Layout.setVisibility(View.INVISIBLE);

                }
            }
        });
        taskDuring_EditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        taskDuring_EditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!validNumber(input)) {
                    taskDuring_EditTxt.setText(removeNotNumber(input));
                    taskDuring_EditTxt.setSelection(taskDuring_EditTxt.length());

                } else {
                    setEndDate(Integer.parseInt(input));

                }
            }
        });

        taskDuringShadow_Layout = v.findViewById(R.id.taskDuringShadow_Layout);

        taskDuring_RecyclerView = v.findViewById(R.id.taskDuring_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        taskDuring_RecyclerView.setLayoutManager(layoutManager);

        taskDuringHorizontalAdapter = new TaskDuringHorizontalAdapter(mContext, onClickTaskDuringItem);
        taskDuringHorizontalAdapter.addItem(TASK_DURING_NO);
        taskDuringHorizontalAdapter.addItem(5);
        taskDuringHorizontalAdapter.addItem(10);
        taskDuringHorizontalAdapter.addItem(20);
        taskDuringHorizontalAdapter.addItem(30);

        taskDuring_RecyclerView.setAdapter(taskDuringHorizontalAdapter);

        nowTimeStamp = getDoubleNowTimeStamp();

        isSetDate = 1;
        setEndDate(TASK_DURING_DEFAULT);
        taskDuring_EditTxt.setText(String.valueOf(selectDuring));


        action_Layout = v.findViewById(R.id.action_Layout);
        actionItem_Layout = v.findViewById(R.id.actionItem_Layout);
        addTaskAction_Txt = v.findViewById(R.id.addTaskAction_Txt);

        actionIdCount = 0;
        actionItemLayoutList = new ArrayList<>();
        actionTitleEditTxtList = new ArrayList<>();
        actionItemList = new ArrayList<>();

        selectProjectId = PROJECT_NO_ID;

        isInit = true;

        new ProjectIdListTask().execute();
    }

    private View.OnClickListener onClickTaskDuringItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setEndDate((int)v.getTag());
        }
    };

    private void setEndDate(int during) {
        selectDuring = during;

        if (selectDuring == TASK_DURING_NO) {
            isSetDate = 0;
            taskDuringShadow_Layout.setVisibility(View.VISIBLE);
            taskDuringHorizontalAdapter.setSelectItem(selectDuring);

        } else {
            if (selectDuring > TASK_DURING_MAX) {
                taskDuring_EditTxt.setText(String.valueOf(TASK_DURING_MAX));
                showToastMessage(R.string.msg_task_length_max_wrong);

            } else if (selectDuring < TASK_DURING_MIN) {
                taskDuring_EditTxt.setText(String.valueOf(TASK_DURING_MIN));
                showToastMessage(R.string.msg_task_length_min_wrong);

            } else {
                isSetDate = 1;

                String duringText = String.format(mContext.getString(R.string.add_task_during_day), selectDuring);
                taskDuring_Txt.setText(duringText);

                selectTimeStamp = nowTimeStamp + TIME_ONE_DAY * selectDuring;
                String date = timeStamp2DateString(selectTimeStamp);
                Log.d(TAG, "date:"+date);
                String[] dateSlice = date.split("\\.");
                selectYear = Integer.parseInt(dateSlice[0]);
                selectMonth = Integer.parseInt(dateSlice[1]);
                selectDay = Integer.parseInt(dateSlice[2]);
                String endDate = String.format(mContext.getString(R.string.add_task_end_date), int2TwoChar(selectMonth), int2TwoChar(selectDay));
                taskEndDate_Txt.setText(endDate);

                taskDuringHorizontalAdapter.setSelectItem(selectDuring);

                taskDuringShadow_Layout.setVisibility(View.GONE);

            }
        }
    }

    //region Button

    public void TaskTitleDeleteClicked() {
        title_EditTxt.setText("");
        title_EditTxt.requestFocus();
        showSoftKeyboard(mActivity, title_EditTxt);
    }

    public void TaskImportance5Clicked() {
        setImportance(TASK_IMPORTANCE_HIGH);
    }

    public void TaskImportance3Clicked() {
        setImportance(TASK_IMPORTANCE_MEDIUM);
    }

    public void TaskImportance1Clicked() {
        setImportance(TASK_IMPORTANCE_LOW);
    }
    private void setImportance(int importance) {
        selectImportance = importance;
        taskImportanceSelect5_Layout.setVisibility(View.GONE);
        taskImportanceSelect3_Layout.setVisibility(View.GONE);
        taskImportanceSelect1_Layout.setVisibility(View.GONE);
        switch (importance) {
            case TASK_IMPORTANCE_HIGH:
                taskImportanceSelect5_Layout.setVisibility(View.VISIBLE);
                break;
            case TASK_IMPORTANCE_MEDIUM:
                taskImportanceSelect3_Layout.setVisibility(View.VISIBLE);
                break;
            case TASK_IMPORTANCE_LOW:
                taskImportanceSelect1_Layout.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void TaskProjectSetClicked() {
        if (isProjectLoad) {
            projectSelectDialog = new ProjectSelectDialog(projectItems, selectProjectId, projectItemClicked);
            projectSelectDialog.show(getFragmentManager(), "projectSelect");

        } else {
            showToastMessage(R.string.msg_project_id_list_load);

        }
    }
    private AdapterView.OnItemClickListener projectItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ProjectItem item = projectItems.get(position);
            selectProjectId = item.id;
            selectProject_Txt.setText(item.title);
            selectProject_Img.setImageResource(item.colorResId);
            projectSelectDialog.dismiss();
        }
    };

    public void EndDatePlusClicked() {
        selectDuring++;
        taskDuring_EditTxt.setText(String.valueOf(selectDuring));
    }

    public void EndDateMinusClicked() {
        selectDuring--;
        taskDuring_EditTxt.setText(String.valueOf(selectDuring));
    }

    public void TaskDuringShadowClicked() {
    }

    public void ActionAddClicked() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_action_edit_vertical, null);
        final EditText title_EditTxt = view.findViewById(R.id.title_EditTxt);
        Button titleDelete_Btn = view.findViewById(R.id.titleDelete_Btn);

        actionItemLayoutList.add(view);
        actionTitleEditTxtList.add(title_EditTxt);

        ActionItem item = new ActionItem();

        item.id = actionIdCount;
        item.title = "";
        item.state = ACTION_STATE_NEW;

        actionItemList.add(item);

        title_EditTxt.setTag(actionIdCount);
        title_EditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > ACTION_TITLE_LENGTH_MAX) {
                    title_EditTxt.setText(s.toString().substring(0, ACTION_TITLE_LENGTH_MAX));
                    title_EditTxt.setSelection(title_EditTxt.length());
                    showToastMessage(R.string.msg_action_title_length_wrong);

                }
            }
        });
        title_EditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                setActionItemFocus((int)v.getTag(), gainFocus);
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

        titleDelete_Btn.setTag(actionIdCount);
        titleDelete_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeActionItem((int)v.getTag());
            }
        });

        actionItem_Layout.addView(view);
        action_Layout.setVisibility(View.VISIBLE);
        addTaskAction_Txt.setText(R.string.add_task_action_add_more);

        title_EditTxt.requestFocus();
        showSoftKeyboard(mActivity, title_EditTxt);

        actionIdCount++;
    }
    public void TaskAddClicked() {
        title = title_EditTxt.getText().toString();
        if (!isNotNull(title)) {
            showToastMessage(R.string.msg_schedule_title_null);
            title_EditTxt.requestFocus();
            return;
        }

        if (!validLength(title, 0, TASK_TITLE_LENGTH_MAX)) {
            showToastMessage(R.string.msg_task_title_length_wrong);
            title_EditTxt.requestFocus();
            return;
        }

        contentJson = getJsonArrayString();

        //new TaskAddTask().execute();
    }

    //endregion

    //region Action Items

    private void setActionItemFocus(int actionId, boolean gainFocus) {
        int position = 0;
        for (ActionItem item:actionItemList) {
            if (item.id == actionId) {
                break;
            }
            position++;
        }

        if (!gainFocus) {
            String title = actionTitleEditTxtList.get(position).getText().toString();

            if (title.length() < 1) {
                removeActionItem(actionId);
            } else {
                saveActionItem(actionId);
            }

            addTask_ScrollView.fullScroll(View.FOCUS_DOWN);
            clearFocusBundle();
        }
    }
    private void removeActionItem(int actionId) {
        boolean isExist = false;
        int position = 0;
        for (ActionItem item:actionItemList) {
            if (item.id == actionId) {
                isExist = true;
                break;
            }
            position++;
        }

        if (isExist) {
            View view = actionItemLayoutList.get(position);
            ((ViewGroup)view.getParent()).removeView(view);

            actionItemLayoutList.remove(position);
            actionTitleEditTxtList.remove(position);
            actionItemList.remove(position);

            if (actionItemList.size() < 1) {
                action_Layout.setVisibility(View.GONE);
                addTaskAction_Txt.setText(R.string.add_task_action_add);

            }
        }
    }
    private void saveActionItem(int actionId) {
        boolean isExist = false;
        int position = 0;
        for (ActionItem item:actionItemList) {
            if (item.id == actionId) {
                isExist = true;
                break;
            }
            position++;
        }

        if (isExist) {
            ActionItem item = actionItemList.get(position);
            item.title = actionTitleEditTxtList.get(position).getText().toString();
            actionItemList.set(position, item);
        }
    }

    private String getJsonArrayString() {
        StringBuilder stringBuilder = new StringBuilder("[");

        for (int i = actionItemList.size()-1; i >= 0; i--) {
            ActionItem item = actionItemList.get(i);
            if (item.state == POSTIT_STATE_NORMAL || item.state == POSTIT_STATE_NEW) {
                String itemString
                        = "{\"title\":\""
                        + item.title
                        + "\", \"state\":\""
                        + item.state
                        + "},";
                stringBuilder.append(itemString);
            }
        }
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append("]");

        } else {
            stringBuilder = new StringBuilder("[{}]");
        }

        return stringBuilder.toString();
    }

    //endregion

    //region api

    private int projectIndex, projectId;
    private String numberId;

    private class ProjectIdListTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/project_id_list.php?mode=one_uuid");
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
                        setProjectList(jsonArray);
                        break;

                    case CODE_PROJECT_ID_LIST_WRONG:
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
    private void setProjectList(JSONArray jsonArray) {
        projectItems = new ArrayList<>();
        ProjectItem defaultItem = new ProjectItem();

        defaultItem.id = PROJECT_NO_ID;
        defaultItem.title = mContext.getString(R.string.project_no_project);
        defaultItem.colorId = PROJECT_NO_ID;
        defaultItem.colorResId = colorId2CircleResId(PROJECT_NO_ID);

        projectItems.add(defaultItem);

        if (jsonArray.toString().contains("[{}]")) {

        } else if (jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    ProjectItem item = new ProjectItem();

                    item.numberId = jsonObject.getString("number_id");
                    item.id = jsonObject.getInt("project_id");

                    projectItems.add(item);

                } catch (JSONException e) { Log.d(TAG, "unexpected JSON exception", e); }
            }
        }

        projectIndex = 1;
        getProjectInfo();
    }
    private void getProjectInfo() {
        if (projectIndex < projectItems.size()) {
            ProjectItem item = projectItems.get(projectIndex);

            numberId = item.numberId;
            projectId = item.id;

            new ProjectInfoTask().execute();

        } else {
            isProjectLoad = true;

        }
    }

    private class ProjectInfoTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + numberId + "/project.php?mode=one_uuid");
                RequestBody formBody = new FormBody.Builder()
                        .add("id", String.valueOf(projectId))
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
                        ProjectItem item = projectItems.get(projectIndex);

                        item.title = jsonObject.getString("title");
                        item.colorId = jsonObject.getInt("color_id");
                        item.colorResId = colorId2CircleResId(item.colorId);

                        projectItems.set(projectIndex, item);
                        projectIndex++;
                        getProjectInfo();
                        break;

                    case CODE_PROJECT_WRONG:
                    case CODE_PROJECT_AUTH_WRONG:
                        projectIndex++;
                        getProjectInfo();
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

    private class TaskAddTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + numberId + "/project.php?mode=one_uuid");
                RequestBody formBody = new FormBody.Builder()
                        .add("id", String.valueOf(projectId))
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
                        ProjectItem item = projectItems.get(projectIndex);

                        item.title = jsonObject.getString("title");
                        item.colorId = jsonObject.getInt("color_id");
                        item.colorResId = colorId2CircleResId(item.colorId);

                        projectItems.set(projectIndex, item);
                        projectIndex++;
                        getProjectInfo();
                        break;

                    case CODE_PROJECT_WRONG:
                    case CODE_PROJECT_AUTH_WRONG:
                        projectIndex++;
                        getProjectInfo();
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
            for (EditText editText : actionTitleEditTxtList) {
                editText.clearFocus();
            }
            title_EditTxt.clearFocus();
            taskDuring_EditTxt.clearFocus();
        }
    }
}