package com.tomclaw.minimonster;

import android.app.Application;

/**
 * Created by solkin on 13.05.15.
 */
public class MonsterApplication extends Application {

    @Override
    public void onCreate() {
        MonstersController.getInstance().openStorage(this);
        super.onCreate();
    }
}
