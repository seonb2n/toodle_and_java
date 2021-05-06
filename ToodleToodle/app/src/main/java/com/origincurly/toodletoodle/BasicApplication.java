package com.origincurly.toodletoodle;

import android.app.Application;
import android.content.Context;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class BasicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // time 정상화
        AndroidThreeTen.init(this);

        // SDK 초기화
        KakaoSDK.init(new KakaoAdapter() {

            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return BasicApplication.this;
                    }
                };
            }
        });
    }
}