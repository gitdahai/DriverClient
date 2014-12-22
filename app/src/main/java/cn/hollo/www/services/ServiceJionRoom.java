package cn.hollo.www.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

import cn.hollo.www.UserInfo;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.thread_pool.ThreadPool;
import cn.hollo.www.xmpp.XMPPManager;
import cn.hollo.www.xmpp.XMPPService;

/**
 * Created by orson on 14-12-22.
 * 加入聊天室服务
 */
public class ServiceJionRoom extends Service{
    private List<String> roomIds;

    /***************************************************
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        roomIds = intent.getStringArrayListExtra("RoomId");
        //如果存在需要加入聊天室的任务，则进行下一步的操作
        if (roomIds != null && roomIds.size() != 0){
            ServiceManager manager = ServiceManager.getInstance(this);
            manager.getXmppBinder(xmppBinder);
        }
        //否则自动停止服务
        else
            this.stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    /****************************************************
     * 销毁资源
     */
    public void onDestroy() {
        super.onDestroy();
    }

    /****************************************************
     *
     * @param intent
     * @return
     */
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*****************************************************
     * 等待XmppBinder对象的到来
     */
    private ServiceManager.OnXmppBinder xmppBinder = new ServiceManager.OnXmppBinder(){
        public void onBinder(XMPPService.XmppBinder binder) {
            XMPPManager xmppManager = binder.getXMPPManager();
            UserInfo userInfo = UserInfo.getInstance(ServiceJionRoom.this);

            //如果成功得到xmpp管理对象,则开启新的线程，进行加入聊天室操作
            if (xmppManager != null && userInfo != null){
                ThreadPool pool = ThreadPool.getInstance();
                pool.addTask(new DoJionRoomRunnable(xmppManager, roomIds, userInfo.getUserId()));
            }

            //停止服务
            ServiceJionRoom.this.stopSelf();
        }
    };

    /*******************************************************
     * 进行加入房间操作
     */
    private class DoJionRoomRunnable implements Runnable {
        private XMPPManager xmppManager;
        private List<String> roomIds;
        private String userId;

        /**================================================
         *
         * @param xmppManager
         * @param roomIds
         */
        private DoJionRoomRunnable(XMPPManager xmppManager, List<String> roomIds, String userId){
            this.xmppManager = xmppManager;
            this.roomIds = roomIds;
            this.userId = userId;
        }

        /**===============================================
         *
         */
        public void run() {
            xmppManager.jionInRooms(roomIds, userId);

            //执行完成后，销毁资源
            xmppManager = null;
            roomIds.clear();
        }
    }
}
