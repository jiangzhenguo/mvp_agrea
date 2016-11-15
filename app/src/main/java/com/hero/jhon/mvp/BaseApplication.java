package com.hero.jhon.mvp;

import android.app.Application;
import android.content.Context;

/**
 * Created by jhon on 2016/11/14.
 */

public class BaseApplication extends Application {

    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
