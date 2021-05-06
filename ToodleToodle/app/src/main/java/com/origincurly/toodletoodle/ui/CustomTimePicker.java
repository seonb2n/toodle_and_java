package com.origincurly.toodletoodle.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

public class CustomTimePicker extends TimePicker {

    private final int m_iColor = 0xFF000000;

    public CustomTimePicker(Context context) {
        super (context);
        Create (context, null);
    }

    public CustomTimePicker (Context context, AttributeSet attrs) {
        super (context, attrs);
        Create (context, attrs);
    }

    public CustomTimePicker (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Create(context, attrs);
    }

    private void Create(Context clsContext, AttributeSet attrs) {
        try {
            Class<?> clsParent = Class.forName("com.android.internal.R$id");
            NumberPicker clsAmPm = findViewById(clsParent.getField("amPm").getInt(null));
            NumberPicker clsHour = findViewById(clsParent.getField("hour").getInt(null));
            NumberPicker clsMin = findViewById(clsParent.getField("minute").getInt(null));
            Class<?> clsNumberPicker = Class.forName ("android.widget.NumberPicker");
            Field clsSelectionDivider = clsNumberPicker.getDeclaredField("mSelectionDivider");

            clsSelectionDivider.setAccessible(true);
            ColorDrawable clsDrawable = new ColorDrawable(m_iColor);

            clsSelectionDivider.set(clsAmPm, clsDrawable);
            clsSelectionDivider.set(clsHour, clsDrawable);
            clsSelectionDivider.set(clsMin, clsDrawable);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}