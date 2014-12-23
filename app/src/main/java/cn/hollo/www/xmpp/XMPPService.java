package cn.hollo.www.xmpp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.SmackAndroid;

/**
 * Created by orson on 14-11-12.
 * xmpp通信服务
 */
public class XMPPService extends Service {
    /**xmpp登录名称*/
    public static final String XMPP_LOGIN_NAME = "XmppLoginName";
    /**xmpp登录密码*/
    public static final String XMPP_LOGIN_PASSWORD = "XmppLoginPassword";

    private XmppBinder           mBinder;
    private XMPPManager          xmppManager;
    private MessageFilterManager filterManager;

    /**
     * 开启服务
     * @param context
     */
    public static void startService(Context context, String name, String password){
        Intent intent = new Intent(context, XMPPService.class);
        intent.putExtra(XMPP_LOGIN_NAME, name);
        intent.putExtra(XMPP_LOGIN_PASSWORD, password);
        context.startService(intent);
    }

    /**
     * 停止服务
     * @param context
     */
    public static void stopService(Context context){
        Intent intent = new Intent(context, XMPPService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xmpp服务
        SmackAndroid.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            Log.d(XMPPConstant.TAG, "启动　XMPPService　服务需要传递　Intent参数!");
            return START_NOT_STICKY;
        }

        if (xmppManager == null){
            String xmppLoginName = intent.getStringExtra(XMPP_LOGIN_NAME);
            String xmppLoginPassword = intent.getStringExtra(XMPP_LOGIN_PASSWORD);

            //判断传递进来的参数
            if (xmppLoginName == null || xmppLoginPassword == null){
                Log.d(XMPPConstant.TAG, "启动　XMPPService　服务需要传递　XmppLoginName 和　XmppLoginPassword　参数!");
                return START_NOT_STICKY;
            }

            xmppManager   = XMPPManager.getInstance(this);
            xmppManager.create(xmppLoginName, xmppLoginPassword);
        }

        if (filterManager == null)
            filterManager = MessageFilterManager.getInstance();

        if (mBinder == null)
            mBinder = new XmppBinder();

        //自动恢复，并且重启
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁xmpp链接
        if (xmppManager != null)
            xmppManager.destroy();

        xmppManager   = null;
        filterManager = null;
        mBinder       = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null)
            mBinder = new XmppBinder();

        return mBinder;
    }

    /****************************************************
     * xmpp服务对象
     */
    public class XmppBinder extends Binder {
        public XMPPManager getXMPPManager(){
            return xmppManager;
        };
        public MessageFilterManager getMessageFilterManager(){
            return filterManager;
        };
    }
}
