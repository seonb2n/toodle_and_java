package com.origincurly.toodletoodle;

import android.os.Bundle;

public class TodayActivity extends BasicActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_work);
        setActivity(this, this);

        init();
    }

    private void init() {

    }
}
