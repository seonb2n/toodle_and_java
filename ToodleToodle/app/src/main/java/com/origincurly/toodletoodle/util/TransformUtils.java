package com.origincurly.toodletoodle.util;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.R;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.origincurly.toodletoodle.GlobalValue.TAG;

public class TransformUtils implements GlobalValue {

    public static String int2StringFormat(int value, String format) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return String.format(format, df.format(value));
    }

    public static String int2String(int value) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(value);
    }

    public static String int2PureStringFormat(int value, String format) {
        return String.format(format, value);
    }

    public static int iconId2IconResId(int iconId) {
        switch (iconId) {
            case 1:
            case 2:
                return R.drawable.account;
            default:
                return R.drawable.btn_x;
        }
    }

    public static int colorId2CircleResId(int colorId) {
        switch (colorId) {
            case 1:
            case 2:
                return R.drawable.custom_project_circle_1;
            default:
                return R.drawable.custom_project_circle_null;
        }
    }

    public static String timeStamp2String(Context context, double timestamp, double nowTimeStamp) {
        double interval = nowTimeStamp - timestamp;
        if (interval < TIME_ONE_HOUR*3) {
            return context.getString(R.string.postit_time_just);

        } else if (interval < TIME_ONE_DAY*1) {
            return context.getString(R.string.postit_time_today);

        } else if (interval < TIME_ONE_DAY*2) {
            return context.getString(R.string.postit_time_yesterday);

        } else if (interval < TIME_ONE_DAY*4) {
            Log.d(TAG, "interval:"+interval + "interval:"+timestamp + "interval:"+nowTimeStamp);
            int day = (int)Math.floor(interval / TIME_ONE_DAY);
            return String.format(context.getString(R.string.postit_time_ago), day);

        } else {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis((long)timestamp * 1000L);
            return DateFormat.format("yyyy.MM.dd", cal).toString();

        }
    }

    public static String timeStamp2DateString(double timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis((long)timestamp * 1000L);
        String date = DateFormat.format("yyyy.MM.dd", cal).toString();
        return date;
    }

    public static Timestamp getNowTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal = Calendar.getInstance();
        String today = formatter.format(cal.getTime());
        return Timestamp.valueOf(today);
    }

    public static double getDoubleNowTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal = Calendar.getInstance();
        String today = formatter.format(cal.getTime());
        long timeStampMilli = Timestamp.valueOf(today).getTime();
        return (double)(timeStampMilli/1000);
    }

    public static double date2TimeStamp(int year, int realMonth, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, realMonth-1, day, 0, 0);
        return (double)(cal.getTimeInMillis()/1000L);
    }

    public static String dayOfWeekInt2String(int dayOfWeek) {
        switch (dayOfWeek) {
            case 2: return "월";
            case 3: return "화";
            case 4: return "수";
            case 5: return "목";
            case 6: return "금";
            case 7: return "토";
            case 1:
            default: return "일";
        }
    }

    public static String int2TwoChar(int value) {
        if (value < 10) {
            return "0"+value;
        } else {
            return String.valueOf(value);
        }

    }

    public static String removeNotNumber(String input) {
        String result = input.replaceAll("[^0-9-]","");
        if (result.length() == 1) {
            if (result.contains("-")) {
                return "0";
            }
        } else if (result.length() < 1) {
            return "0";
        }
        return result;
    }
}