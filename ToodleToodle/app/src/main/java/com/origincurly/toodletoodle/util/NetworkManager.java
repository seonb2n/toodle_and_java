package com.origincurly.toodletoodle.util;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class NetworkManager {

    private static NetworkManager _instance;

    public static NetworkManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new NetworkManager(context);
        }
        return _instance;
    }

    private OkHttpClient client;
    private SetCookieCache cookieCache = new SetCookieCache();
    private ClearableCookieJar cookieJar;

    private NetworkManager(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //Context context = CustomerApp.getInstance();
        cookieJar = new PersistentCookieJar(cookieCache, new SharedPrefsCookiePersistor(context));
        builder.cookieJar(cookieJar);

        File cacheDir = new File(context.getCacheDir(), "network");
        if (!cacheDir.exists()) {
            boolean ignore = cacheDir.mkdir();
    }

    Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);

        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);

        client = builder.build();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void clearSession() {
        cookieJar.clearSession();
    }
}
