package com.theteamgo.fancywatch;

import android.app.Application;

/**
 * Created by jesse on 16/1/16.
 */
public class MyApplication extends Application {
    MainActivity mainActivity = null;
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public MainActivity getMainActivity() {
        return this.mainActivity;
    }
}
