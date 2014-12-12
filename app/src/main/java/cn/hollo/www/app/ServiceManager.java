package cn.hollo.www.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

import cn.hollo.www.UserInfo;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.location.ServiceLocation;
import cn.hollo.www.location.UploadLocation;
import cn.hollo.www.thread_pool.ThreadPool;
import cn.hollo.www.xmpp.XMPPManager;
import cn.hollo.www.xmpp.XMPPService;

/**
 * Created by orson on 14-11-26.
 * 服务管理者
 */
public class ServiceManager {
    private static ServiceManager instance;
    private Context context;
    private ThreadPool threadPool;
    private ServiceLocation.LocationBinder  locationBinder; //位置服务Binder对象
    private XMPPService.XmppBinder          xmppBinder;     //xmpp服务的Binder对象
    private List<OnLocaBinder> locaBinders;
    private List<OnXmppBinder> xmppBinders;

    private ServiceManager(Context context){
        locaBinders = new ArrayList<OnLocaBinder>();
        xmppBinders = new ArrayList<OnXmppBinder>();
        this.context = context.getApplicationContext();
    };

    /*******************************************
     * 返回该类的实例对象
     * @return
     */
    public static ServiceManager getInstance(Context context){
        if (instance == null)
            instance = new ServiceManager(context);

        return instance;
    }

    /**
     * 获取位置服务的Binder
     * @param olb
     */
    public void getLocationBinder(OnLocaBinder olb){
        if (olb == null)
            return;

        if (locationBinder != null)
            olb.onBinder(locationBinder);
        else
            locaBinders.add(olb);
    }

    /**
     * 获取xmpp的Binder
     * @param oxb
     */
    public void getXmppBinder(OnXmppBinder oxb){
        if (oxb == null)
            return ;

        if (xmppBinder != null)
            oxb.onBinder(xmppBinder);
        else
            xmppBinders.add(oxb);
    }

    /**
     * 返回线程池对象
     * @return
     */
    public ThreadPool getThreadPool(){
        return threadPool;
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
     * 启动服务
     * 目前调用的位置在：用户登录成功后
     */
    public void startService(){
        //如果线程池对象不存在，则生成一个对象
        if (threadPool == null)
            threadPool = ThreadPool.getInstance();

        Context ctx = context.getApplicationContext();

        UserInfo userInfo = UserInfo.getInstance(context);
        if (userInfo.getUserId() == null || userInfo.getUserPassword() == null){
            try {
                throw new Exception("start_service error:找不到当前用户信息!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            //启动位置服务
            ServiceLocation.startService(ctx);
            //启动xmpp服务
            XMPPService.startService(ctx, userInfo.getUserId(), userInfo.getUserPassword());

            //绑定位置服务
            Intent locationIntent = new Intent(context, ServiceLocation.class);
            ctx.bindService(locationIntent, locaConnection, Context.BIND_AUTO_CREATE);

            //绑定xmpp服务
            Intent xmppIntent = new Intent(context, XMPPService.class);
            ctx.bindService(xmppIntent, xmppConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 停止服务:
     * 目前停止的位置在：１退出应用时，　２在退出帐号时
     */
    public void stopService(){
        //停止线程池
        threadPool.cancel();
        threadPool = null;

        Context ctx = context.getApplicationContext();
        try{
            //移除定位数据监听器
            if (locationBinder != null)
                locationBinder.removeLocationListener("UploadLocation");

            ctx.unbindService(locaConnection);
            ctx.unbindService(xmppConnection);

            //停止位置服务
            ServiceLocation.stopService(ctx);
            //停止xmpp服务
            XMPPService.stopService(ctx);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /************************************************
     *  绑定服务返回后结果
     */
    private ServiceConnection locaConnection = new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationBinder = (ServiceLocation.LocationBinder)service;

            for (OnLocaBinder olb :locaBinders)
                olb.onBinder(locationBinder);

            locaBinders.clear();

            //进行自动发布位置订阅信息
            autoSendSubscribeLocation(locationBinder);
        }

        public void onServiceDisconnected(ComponentName name) {
            locationBinder = null;
        }
    };

    /**************************************************
     * xmpp服务绑定成功
     */
    private ServiceConnection xmppConnection = new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder service) {
            xmppBinder = (XMPPService.XmppBinder)service;

            for (OnXmppBinder oxb : xmppBinders)
                oxb.onBinder(xmppBinder);

            xmppBinders.clear();
        }

        public void onServiceDisconnected(ComponentName name) {
            xmppBinder  = null;
        }
    };

    /***********************************************
     * 自动发送司机端的位置订阅信息
     */
    private void autoSendSubscribeLocation(ServiceLocation.LocationBinder locationBinder){
        locationBinder.addLoactionListener("UploadLocation",new ServiceLocation.OnLocationListener(){
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (xmppBinder != null){
                    UploadLocation uploadLocation = UploadLocation.getInstance();
                    UserInfo userInfo = UserInfo.getInstance(context);
                    XMPPManager xmppManager = xmppBinder.getXMPPManager();
                    uploadLocation.onLocationChanged(xmppManager, aMapLocation, userInfo.getUserId());
                }
            }
        } );
    }

    /***********************************************
     * 获取位置服务的Binder
     */
    public interface OnLocaBinder{
        public void onBinder(ServiceLocation.LocationBinder binder);
    }

    /***********************************************
     *  获取xmpp服务的Binder
     */
    public interface OnXmppBinder{
        public void onBinder(XMPPService.XmppBinder binder);
    }
}
