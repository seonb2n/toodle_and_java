package com.origincurly.toodletoodle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.origincurly.toodletoodle.R;

public class NickCancelDialog extends Dialog {

    private View.OnClickListener negativeClickListener;
    private View.OnClickListener positiveClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_nick_cancel);

        Button negative_Btn = findViewById(R.id.negative_Btn);
        Button positive_Btn = findViewById(R.id.positive_Btn);

        negative_Btn.setOnClickListener(negativeClickListener);
        positive_Btn.setOnClickListener(positiveClickListener);
    }

    public NickCancelDialog(Context context, View.OnClickListener negativeClickListener, View.OnClickListener positiveClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.negativeClickListener = negativeClickListener;
        this.positiveClickListener = positiveClickListener;
    }
}