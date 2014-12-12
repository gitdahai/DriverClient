package cn.hollo.www.app;

import android.app.Application;

/**
 * Created by orson on 14-11-25.
 */
public class HolloApplication extends Application {
    public void onCreate() {
        super.onCreate();

        ServiceManager SM = ServiceManager.getInstance(this);
        SM.createHttp(this);
    }
}
