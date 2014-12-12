package cn.hollo.www.features.operation;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.content_provider.OpenHelperPassenger;
import cn.hollo.www.content_provider.ProviderPassenger;
import cn.hollo.www.features.informations.Passenger;
import cn.hollo.www.features.informations.WorkTaskDetail;
import cn.hollo.www.thread_pool.ThreadPool;
import cn.hollo.www.xmpp.XMPPManager;
import cn.hollo.www.xmpp.XMPPService;

/**
 * Created by orson on 14-12-3.
 * 站点消息管理
 */
public class PassengerManager {
    private static PassengerManager instance;
    private PassengerManager(){}

    public static PassengerManager getInstance(){
        if (instance == null)
            instance = new PassengerManager();

        return instance;
    }

    /*******************************************
     * 添加乘客到数据库中
     * @param detail
     */
    public void addAllPassengers(Context context, WorkTaskDetail detail){
        List<WorkTaskDetail.Station> stations = detail.stations;
        List<Passenger> users = null;

        for (WorkTaskDetail.Station station : stations){
            users = station.on_users;

            for (Passenger user : users)
                context.getContentResolver().insert(ProviderPassenger.CONTENT_URI, user.getContentValues());

        }
    }

    /******************************************
     * 删除下车的乘客
     * @param context
     * @param users
     */
    public void deletePassengers(Context context, List<Passenger> users){
        String where = OpenHelperPassenger.USER_ID + "=?";
        String[] args = new String[1];

        for (Passenger passenger : users){
            args[0] = passenger.user_id;
            context.getContentResolver().delete(ProviderPassenger.CONTENT_URI, where, args);
        }
    }

    /*****************************************
     * 跟新乘客为上车状态
     * @param context
     * @param users
     */
    public void updatePassersAboardCondition(Context context, List<Passenger> users){
        String where = OpenHelperPassenger.USER_ID + "=?";
        String[] args = new String[1];

        for (Passenger passenger : users){
            args[0] = passenger.user_id;
            passenger.condition = 1;
            context.getContentResolver().update(ProviderPassenger.CONTENT_URI, passenger.getContentValues(), where, args);
        }
    }

    /****************************************
     * 更新乘客为下车状态
     * @param context
     * @param users
     */
    public void updatePassersDebusCondition(Context context, List<Passenger> users){
        String where = OpenHelperPassenger.USER_ID + "=?";
        String[] args = new String[1];

        for (Passenger passenger : users){
            args[0] = passenger.user_id;
            passenger.condition = 2;
            context.getContentResolver().update(ProviderPassenger.CONTENT_URI, passenger.getContentValues(), where, args);
        }
    }

    /****************************************
     * 向所有车上和未上车的乘客发送消息
     * @param context
     * @param vehicleCode   : 车辆编号
     * @param message       : 消息描述
     */
    public void sendMessageToPassenger(Context context, String vehicleCode, String message){
        //首先查询数据库，把所有的已经上车，或者还没上车的用户搜索出来
        String selection = OpenHelperPassenger.CONDITION + "!=?";
        String[] selectionArgs = {"2"};
        Cursor cursor = context.getContentResolver().query(ProviderPassenger.CONTENT_URI, null, selection, selectionArgs, null);

        if (cursor == null)
            return;

        if (cursor.getCount() == 0){
            cursor.close();
            return;
        }

        //生成发送任务,并且进行发送消息
        List<Passenger> passengers = readPassengerFromCursor(cursor);
        ChatTask task = new ChatTask(passengers, vehicleCode, message);
        task.startTask(context);

        //安全地关闭Cursor
        cursor.close();
    }

    /****************************************
     * 从Cursor中读取Passenger数据
     * @param cursor
     * @return
     */
    private List<Passenger> readPassengerFromCursor(Cursor cursor){
        List<Passenger> passengers = new ArrayList<Passenger>();
        Passenger passenger = null;

        while (cursor.moveToNext()){
            passenger = Passenger.createPassenger(cursor);
            passengers.add(passenger);
        }

        return passengers;
    }

    /****************************************
     * 消息任务
     */
    private class ChatTask implements Runnable{
        private List<Passenger> passengers;
        private String  vehicleCode;
        private String       message;
        private XMPPManager  xmppManager;

        /**
         *
         * @param passengers
         * @param vehicleCode
         * @param message
         */
        private ChatTask(List<Passenger> passengers, String vehicleCode, String message){
            this.passengers  = passengers;
            this.vehicleCode = vehicleCode;
            this.message     = message;
        }

        /**
         * 启动任务
         */
        private void startTask(final Context context){
            ServiceManager serviceManager = ServiceManager.getInstance(context);
            serviceManager.getXmppBinder(new ServiceManager.OnXmppBinder(){
                public void onBinder(XMPPService.XmppBinder binder) {
                    if (binder != null){
                        xmppManager = binder.getXMPPManager();
                        //当成功获取到xmppManager对象，则可以发送消息了
                        if (xmppManager != null)
                            runTask(context);
                    }
                }
            });
        }

        /**
         * 运行任务，开始实际发送消息
         */
        private void runTask(Context context){
            ServiceManager manager = ServiceManager.getInstance(context);
            ThreadPool pool = manager.getThreadPool();

            if (pool == null){
                try {
                    throw new Exception("没有可用的任务执行线程!");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            else
                pool.addTask(this);
        }

        @Override
        public void run() {
            for (Passenger p : passengers){
                StationMessage sm = new StationMessage();
                sm.to = p.user_id;
                sm.contract_id = p.contract_id;
                sm.describe = message;
                sm.vehicle_code = vehicleCode;
                //发送
                xmppManager.sendSingleChat(sm);
            }
        }
    }
}
