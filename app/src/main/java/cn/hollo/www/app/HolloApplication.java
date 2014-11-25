package cn.hollo.www.app;

import android.app.Application;

import cn.hollo.www.https.HttpManager;

/**
 * Created by orson on 14-11-25.
 */
public class HolloApplication extends Application {
    public void onCreate() {
        super.onCreate();
        //创建http服务
        createHttp();
    }

    /**
     * 创建http
     */
    public void createHttp(){
        HttpManager http = HttpManager.getInstance();
        http.create(this);
    }

    /**
     * 销毁Http
     */
    public void destroyHttp(){
        HttpManager http = HttpManager.getInstance();
        http.destroy();
    }
}
