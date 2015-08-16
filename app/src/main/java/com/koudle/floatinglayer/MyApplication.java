package com.koudle.floatinglayer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by koudel on 15/8/14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                FloatingLayer.getInstance(MyApplication.this).show(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {
                //FloatingLayer.getInstance(MyApplication.this).close();
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
