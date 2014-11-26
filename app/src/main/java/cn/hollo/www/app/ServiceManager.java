package cn.hollo.www.app;

import android.content.Context;

import cn.hollo.www.https.HttpManager;
import cn.hollo.www.location.ServiceLocation;

/**
 * Created by orson on 14-11-26.
 * 服务管理者
 */
public class ServiceManager {
    private static ServiceManager instance;
    private ServiceManager(){};

    public static ServiceManager getInstance(){
        if (instance == null)
            instance = new ServiceManager();

        return instance;
    }

    /**
     * 创建http
     */
    public void createHttp(Context context){
        HttpManager http = HttpManager.getInstance();
        http.create(context);
    }

    /**
     * 销毁Http
     */
    public void destroyHttp(){
        HttpManager http = HttpManager.getInstance();
        http.destroy();
    }

    /**
     * 启动位置服务
     * @param context
     */
    public void startLocationService(Context context){
        ServiceLocation.startService(context);
    }

    /**
     * 停止位置服务
     * @param context
     */
    public void stopLocationService(Context context){
        ServiceLocation.stopService(context);
    }
}
