package com.origincurly.toodletoodle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.origincurly.toodletoodle.dialog.BeginEndCancelDialog;
import com.origincurly.toodletoodle.ui.CustomTimePicker;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

public class BeginEndSetActivity extends BasicActivity {

    //region View

    private TextView beginEndContent_Txt;

    private RelativeLayout begin_Layout;
    private RelativeLayout beginTimePicker_Layout;
    private CustomTimePicker begin_TimePicker;
    private NumberPicker beginAmPm_Picker;
    private NumberPicker beginHour_Picker;
    private NumberPicker beginMinute_Picker;
    private RelativeLayout beginAdd_Layout;
    private RelativeLayout beginSet_Layout;
    private TextView beginTime_Txt;

    private RelativeLayout end_Layout;
    private RelativeLayout endTimePicker_Layout;
    private CustomTimePicker end_TimePicker;
    private NumberPicker endAmPm_Picker;
    private NumberPicker endHour_Picker;
    private NumberPicker endMinute_Picker;
    private RelativeLayout endAdd_Layout;
    private RelativeLayout endSet_Layout;
    private TextView endTime_Txt;

    private TextView userMsg_Txt;
    private RelativeLayout beginEndSetBtn_Layout;
    private ImageView beginEndSetBtn_Img;

    //endregion

    //region Variable

    private String nick;

    private boolean isBeginSet = false, isEndSet = false;
    private boolean isBeginShow = true, isEndShow = false;
    private int beginAmPm, endAmPm;
    private int beginHour, beginMinute, endHour, endMinute;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_end_set);
        setActivity(this, this);

        init();
    }

    private void init() {
        beginEndContent_Txt = findViewById(R.id.beginEndContent_Txt);

        begin_Layout = findViewById(R.id.begin_Layout);
        beginTimePicker_Layout = findViewById(R.id.beginTimePicker_Layout);
        begin_TimePicker = findViewById(R.id.begin_TimePicker);
        beginAmPm_Picker = findViewById(R.id.beginAmPm_Picker);
        beginHour_Picker = findViewById(R.id.beginHour_Picker);
        beginMinute_Picker = findViewById(R.id.beginMinute_Picker);
        beginAdd_Layout = findViewById(R.id.beginAdd_Layout);
        beginSet_Layout = findViewById(R.id.beginSet_Layout);
        beginTime_Txt = findViewById(R.id.beginTime_Txt);


        end_Layout = findViewById(R.id.end_Layout);
        endTimePicker_Layout = findViewById(R.id.endTimePicker_Layout);
        end_TimePicker = findViewById(R.id.end_TimePicker);
        endHour_Picker = findViewById(R.id.endHour_Picker);
        endMinute_Picker = findViewById(R.id.endMinute_Picker);
        endAdd_Layout = findViewById(R.id.endAdd_Layout);
        endSet_Layout = findViewById(R.id.endSet_Layout);
        endTime_Txt = findViewById(R.id.endTime_Txt);

        userMsg_Txt = findViewById(R.id.userMsg_Txt);
        beginEndSetBtn_Layout = findViewById(R.id.joinEmailBtn_Layout);
        beginEndSetBtn_Img = findViewById(R.id.joinEmailBtn_Img);
    }

    private void setInputResult() {
//        if (nick_EditTxt.length() < 1) {
//            beginEndSetBtn_Layout.setBackgroundResource(R.drawable.custom_submit_btn_off);
//            beginEndSetBtn_Img.setImageResource(R.drawable.ic_next_off);
//
//        } else {
//            beginEndSetBtn_Layout.setBackgroundResource(R.drawable.custom_submit_btn_on);
//            beginEndSetBtn_Img.setImageResource(R.drawable.ic_next_on);
//        }
    }

    private void setTimePicker() {
        setTimePickerInterval(begin_TimePicker);
        setTimePickerInterval(end_TimePicker);
    }


    private void setTimePickerInterval(CustomTimePicker timePicker) {
        try {
            Class<?> classForId = Class.forName("com.android.internal.R$id");
            Field field = classForId.getField("minute");
            NumberPicker minutePicker = timePicker.findViewById(field.getInt(null));
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(11);
            List<String> displayedValues = new ArrayList();
            for (int i = 0; i < 60; i += TIME_PICKER_MINUTE_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
            minutePicker.setWrapSelectorWheel(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //region Button

    public void BeginAddClicked(View v) {
        showBeginTimePicker();
    }

    public void BeginSetClicked(View v) {
        hideBeginTimePicker();
    }

    public void EndAddClicked(View v) {
        showEndTimePicker();
    }

    public void EndSetClicked(View v) {
        hideEndTimePicker();
    }

    public void BeginEndSetClicked(View v) {
//        nick = nick_EditTxt.getText().toString();
//
//        if (!isNotNull(nick)) {
//            showUserMsg(R.string.msg_user_nick_null);
//            nick_EditTxt.requestFocus();
//            return;
//        }
//
//        if (!validLength(nick, NICK_LENGTH_MIN, NICK_LENGTH_MAX)) {
//            showUserMsg(R.string.msg_user_nick_length_wrong);
//            nick_EditTxt.requestFocus();
//            return;
//        }
//
//        if (!validNick(nick)) {
//            showUserMsg(R.string.msg_user_nick_regex_wrong);
//            nick_EditTxt.requestFocus();
//            return;
//        }
//
//        if (nick.contains(USER_NICK_DEFAULT)) {
//            showUserMsg(R.string.msg_user_nick_wrong);
//            nick_EditTxt.requestFocus();
//            return;
//        }
//
//        new NickSetTask().execute();

    }

    //endregion

    //region TimePicker

    private void showBeginTimePicker() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_picker_show);
        beginTimePicker_Layout.clearAnimation();
        beginTimePicker_Layout.startAnimation(animation);
        beginTimePicker_Layout.setVisibility(View.VISIBLE);
        beginSet_Layout.setVisibility(View.VISIBLE);
        beginAdd_Layout.setVisibility(View.GONE);
    }

    private void hideBeginTimePicker() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_picker_hide);
        beginTimePicker_Layout.clearAnimation();
        beginTimePicker_Layout.startAnimation(animation);
        hideBeginTimePickerHandler.sendEmptyMessageDelayed(0, DATE_PICKER_FOLD_ANIMATION_TIME);
        beginSet_Layout.setVisibility(View.GONE);
        beginAdd_Layout.setVisibility(View.VISIBLE);
    }
    private Handler hideBeginTimePickerHandler = new Handler() {
        public void handleMessage(Message msg) {
            //region hideBeginTimePickerHandler
            beginTimePicker_Layout.setVisibility(View.GONE);
            //endregion
        }
    };

    private void showEndTimePicker() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_picker_show);
        endTimePicker_Layout.clearAnimation();
        endTimePicker_Layout.startAnimation(animation);
        endTimePicker_Layout.setVisibility(View.VISIBLE);
        endSet_Layout.setVisibility(View.VISIBLE);
        endAdd_Layout.setVisibility(View.GONE);
    }

    private void hideEndTimePicker() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_picker_hide);
        endTimePicker_Layout.clearAnimation();
        endTimePicker_Layout.startAnimation(animation);
        hideEndTimePickerHandler.sendEmptyMessageDelayed(0, DATE_PICKER_FOLD_ANIMATION_TIME);
        endSet_Layout.setVisibility(View.GONE);
        endSet_Layout.setVisibility(View.VISIBLE);
    }
    private Handler hideEndTimePickerHandler = new Handler() {
        public void handleMessage(Message msg) {
            //region hideEndTimePickerHandler
            endTimePicker_Layout.setVisibility(View.GONE);
            //endregion
        }
    };

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


//    @Override
//    public void clearFocusBundle() {
//        nick_EditTxt.clearFocus();
//    }

    @Override
    public void backActivity() {
        beginEndCancelDialog = new BeginEndCancelDialog(mContext, beginEndCancelNegativeClickListener, beginEndCancelPositiveClickListener);
        beginEndCancelDialog.show();
    }

    private BeginEndCancelDialog beginEndCancelDialog;
    public View.OnClickListener beginEndCancelNegativeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            beginEndCancelDialog.dismiss();
        }
    };
    public View.OnClickListener beginEndCancelPositiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            beginEndCancelDialog.dismiss();
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