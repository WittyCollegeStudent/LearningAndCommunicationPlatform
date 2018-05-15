package com.weianyang.learningplatform.tool;

import android.app.Application;
import android.content.Context;

/**
 * Created by weianyang on 18-2-25.
 */

public class LCPApplication extends Application {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public Context getContext(){
        return context;
    }
}
