package com.origincurly.toodletoodle.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.origincurly.toodletoodle.BasicFragment;
import com.origincurly.toodletoodle.DbCheckActivity;
import com.origincurly.toodletoodle.JoinMailActivity;
import com.origincurly.toodletoodle.R;
import com.origincurly.toodletoodle.dialog.ProjectSelectDialog;
import com.origincurly.toodletoodle.list.PostitCategoryItem;
import com.origincurly.toodletoodle.list.ProjectItem;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.origincurly.toodletoodle.util.CheckUtils.isNotNull;
import static com.origincurly.toodletoodle.util.CheckUtils.validLength;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;
import static com.origincurly.toodletoodle.util.TransformUtils.colorId2CircleResId;
import static com.origincurly.toodletoodle.util.TransformUtils.date2TimeStamp;
import static com.origincurly.toodletoodle.util.TransformUtils.dayOfWeekInt2String;
import static com.origincurly.toodletoodle.util.TransformUtils.getDoubleNowTimeStamp;
import static com.origincurly.toodletoodle.util.TransformUtils.int2TwoChar;

public class AddFastScheduleFragment extends BasicFragment {

    //region View

    private EditText title_EditTxt;
    private RelativeLayout titleDelete_Layout;

    private TextView selectDate_Txt;
    private ImageView datePickerFoldBtn_Img;
    private TextView dayLater_Txt;
    private View dateLine_View;

    private RelativeLayout datePicker_Layout;
    private NumberPicker dateYear_Picker;
    private NumberPicker dateMonth_Picker;
    private NumberPicker dateDay_Picker;
    private TextView dayOfWeek_Txt;

    private TextView selectTime_Txt;
    private ImageView timePickerBtn_Img;
    private View timeLine_View;

    private RelativeLayout projectAdd_Layout;
    private RelativeLayout projectSet_Layout;
    private ImageView selectProject_Img;
    private TextView selectProject_Txt;

    private RelativeLayout contentAdd_Layout;
    private RelativeLayout contentSet_Layout;
    private EditText content_EditTxt;
    //endregion View


    //region Variable

    public boolean isInit = false;
    private boolean isProjectLoad = false;

    private double nowTimeStamp, selectTimeStamp;
    private boolean isDataPickerShow = false;

    private String title, content;
    private int selectYear, selectMonth, selectDay, selectHour, selectMinute;
    private int selectProjectId;

    private ArrayList<ProjectItem> projectItems;
    private ProjectSelectDialog projectSelectDialog;

    //endregion

    public AddFastScheduleFragment (Context context, Activity activity) {
        setFragment(context, activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_schedule, container, false);

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

        selectDate_Txt = v.findViewById(R.id.selectDate_Txt);
        datePickerFoldBtn_Img = v.findViewById(R.id.datePickerFoldBtn_Img);
        dayLater_Txt = v.findViewById(R.id.dayLater_Txt);
        dateLine_View = v.findViewById(R.id.dateLine_View);

        datePicker_Layout = v.findViewById(R.id.datePicker_Layout);
        dateYear_Picker = v.findViewById(R.id.dateYear_Picker);
        dateMonth_Picker = v.findViewById(R.id.dateMonth_Picker);
        dateDay_Picker = v.findViewById(R.id.dateDay_Picker);
        dayOfWeek_Txt = v.findViewById(R.id.dayOfWeek_Txt);

        setDatePicker(dateYear_Picker, dateMonth_Picker, dateDay_Picker);
        nowTimeStamp = getDoubleNowTimeStamp();

        selectTime_Txt = v.findViewById(R.id.selectTime_Txt);
        timePickerBtn_Img = v.findViewById(R.id.timePickerBtn_Img);
        timeLine_View = v.findViewById(R.id.timeLine_View);

        projectAdd_Layout = v.findViewById(R.id.projectAdd_Layout);
        projectSet_Layout = v.findViewById(R.id.projectSet_Layout);
        selectProject_Img = v.findViewById(R.id.selectProject_Img);
        selectProject_Txt = v.findViewById(R.id.selectProject_Txt);

        contentAdd_Layout = v.findViewById(R.id.contentAdd_Layout);
        contentSet_Layout = v.findViewById(R.id.contentSet_Layout);
        content_EditTxt = v.findViewById(R.id.content_EditTxt);
        content_EditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        selectHour = 0;
        selectMinute = 0;

        selectProjectId = PROJECT_NO_ID;

        isInit = true;

        new ProjectIdListTask().execute();
    }

    //region Button

    public void ScheduleTitleDeleteClicked() {
        title_EditTxt.setText("");
        title_EditTxt.requestFocus();
        showSoftKeyboard(mActivity, title_EditTxt);
    }

    public void DatePickerClicked() {
        if (isDataPickerShow) {
            isDataPickerShow = false;
            hideDatePicker();
        } else {
            isDataPickerShow = true;
            showDatePicker();
        }
    }

    public void TimePickerClicked() {
    }

    public void ScheduleProjectAddClicked() {
        projectAdd_Layout.setVisibility(View.GONE);
        projectSet_Layout.setVisibility(View.VISIBLE);
    }

    public void ProjectSetClicked() {
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

    public void ScheduleContentAddClicked() {
        content_EditTxt.setText("");
        contentAdd_Layout.setVisibility(View.GONE);
        contentSet_Layout.setVisibility(View.VISIBLE);
    }

    public void ScheduleContentDeleteClicked() {
        content_EditTxt.setText("");
        contentAdd_Layout.setVisibility(View.VISIBLE);
        contentSet_Layout.setVisibility(View.GONE);
    }

    public void ScheduleAddClicked() {
        title = title_EditTxt.getText().toString();
        content = content_EditTxt.getText().toString();

        if (!isNotNull(title)) {
            showToastMessage(R.string.msg_schedule_title_null);
            title_EditTxt.requestFocus();
            return;
        }

        if (!validLength(title, 0, SCHEDULE_TITLE_LENGTH_MAX)) {
            showToastMessage(R.string.msg_schedule_title_length_wrong);
            title_EditTxt.requestFocus();
            return;
        }

        if (!isNotNull(content)) {
            content = "";
        }

        if (!validLength(content, 0, SCHEDULE_CONTENT_LENGTH_MAX)) {
            showToastMessage(R.string.msg_schedule_content_length_wrong);
            content_EditTxt.requestFocus();
            return;
        }

        new ScheduleAddTask().execute();
    }

    //endregion

    //region DATE Picker

    private void setDatePicker(final NumberPicker yearPicker, final NumberPicker monthPicker, NumberPicker dayPicker) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int realMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setMinValue(year-PICKER_YEAR_MAX_OFFSET);
        yearPicker.setMaxValue(year+PICKER_YEAR_MAX_OFFSET);
        yearPicker.setValue(year);

        monthPicker.setWrapSelectorWheel(false);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(realMonth);

        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(day);

        setDate();
        setDayMax();
        setDayOfWeek();

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDate();
                setDayMax();
                setDayOfWeek();
            }
        });
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDate();
                setDayMax();
                setDayOfWeek();
            }
        });
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDate();
                setDayOfWeek();
            }
        });
    }
    private int getDayMax(int year, int realMonth) {
        switch (realMonth) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (year % 400 == 0) {
                    return 29;
                } else if (year % 100 == 0) {
                    return 28;
                } else if (year % 4 == 0) {
                    return 29;
                } else {
                    return 28;
                }
        }
        return 0;
    }
    private void setDayMax() {
        int year = dateYear_Picker.getValue();
        int month = dateMonth_Picker.getValue();
        dateDay_Picker.setMaxValue(getDayMax(year, month));
    }
    private void setDayOfWeek() {
        String dayOfWeek = "일";
        int year = dateYear_Picker.getValue();
        int realMonth = dateMonth_Picker.getValue();
        int day = dateDay_Picker.getValue();

        dayOfWeek = dayOfWeekInt2String(getDayOfWeek(year, realMonth, day));

        dayOfWeek_Txt.setText(dayOfWeek);
    }
    private int getDayOfWeek(int year, int realMonth, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, realMonth-1, day, 0, 0);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    //endregion

    private void setDate() {
        selectYear = dateYear_Picker.getValue();
        selectMonth = dateMonth_Picker.getValue();
        selectDay = dateDay_Picker.getValue();

        selectTimeStamp = date2TimeStamp(selectYear, selectMonth, selectDay);
        int day = ((int)Math.floor((selectTimeStamp - nowTimeStamp) / TIME_ONE_DAY)) + 1;
        dayLater_Txt.setText(String.format(mContext.getString(R.string.add_value_date_day), day));

        selectDate_Txt.setText(String.format(mContext.getString(R.string.add_value_date),
                String.valueOf(selectYear),
                int2TwoChar(selectMonth),
                int2TwoChar(selectDay),
                dayOfWeekInt2String(getDayOfWeek(selectYear, selectMonth, selectDay))));
    }

    private void showDatePicker() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_picker_show);
        datePicker_Layout.clearAnimation();
        datePicker_Layout.startAnimation(animation);
        datePicker_Layout.setVisibility(View.VISIBLE);
        dateLine_View.setBackgroundColor(mContext.getColor(R.color.add_value_select_line));
        datePickerFoldBtn_Img.setImageResource(R.drawable.ic_dropup_20);
    }

    private void hideDatePicker() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_picker_hide);
        datePicker_Layout.clearAnimation();
        datePicker_Layout.startAnimation(animation);
        hideDatePickerHandler.sendEmptyMessageDelayed(0, DATE_PICKER_FOLD_ANIMATION_TIME);
        dateLine_View.setBackgroundColor(mContext.getColor(R.color.add_value_hint_line));
        datePickerFoldBtn_Img.setImageResource(R.drawable.ic_dropdown_20);

        setDate();
    }
    private Handler hideDatePickerHandler = new Handler() {
        public void handleMessage(Message msg) {
            //region hideDatePickerHandler
            datePicker_Layout.setVisibility(View.GONE);
            //endregion
        }
    };

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

                    item.numberId = jsonObject.getInt("number_id");
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

    private class ScheduleAddTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/schedule.php?mode=add");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
                        .add("project_id", String.valueOf(selectProjectId))
                        .add("title", String.valueOf(selectProjectId))
                        .add("project_id", String.valueOf(selectProjectId))
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
                        showToastMessage(R.string.msg_schedule_add_end);
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
            content_EditTxt.clearFocus();
        }
    }
}