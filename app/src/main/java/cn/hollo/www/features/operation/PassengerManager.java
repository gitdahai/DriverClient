package cn.hollo.www.features.operation;

import android.content.Context;

import java.util.List;

import cn.hollo.www.content_provider.OpenHelperPassenger;
import cn.hollo.www.content_provider.ProviderPassenger;
import cn.hollo.www.features.informations.Passenger;
import cn.hollo.www.features.informations.WorkTaskDetail;

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
}
