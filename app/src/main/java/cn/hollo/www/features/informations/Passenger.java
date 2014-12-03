package cn.hollo.www.features.informations;

import android.content.ContentValues;
import android.database.Cursor;

import cn.hollo.www.content_provider.OpenHelperPassenger;

/**
 * Created by orson on 14-12-3.
 */
public class Passenger {
    public String task_id;
    public String user_id;
    public String avatar;
    public String nickname;
    public String contract_id;  //合约id
    public int    condition;    //乘客的状态(0=默认, 1=上车, 2=下车)

    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(OpenHelperPassenger.TASK_ID, task_id);
        values.put(OpenHelperPassenger.USER_ID, user_id);
        values.put(OpenHelperPassenger.NICKNAME, nickname);
        values.put(OpenHelperPassenger.AVATAR, avatar);
        values.put(OpenHelperPassenger.CONTRACT_ID, contract_id);
        values.put(OpenHelperPassenger.CONDITION, condition);
        return values;
    }

    public static Passenger createPassenger(Cursor cursor){
        Passenger passenger = new Passenger();

        passenger.task_id     = cursor.getString(OpenHelperPassenger.COL_INDEX_TASK_ID);
        passenger.user_id     = cursor.getString(OpenHelperPassenger.COL_INDEX_USER_ID);
        passenger.avatar      = cursor.getString(OpenHelperPassenger.COL_INDEX_AVATAR);
        passenger.nickname    = cursor.getString(OpenHelperPassenger.COL_INDEX_NICKNAME);
        passenger.contract_id = cursor.getString(OpenHelperPassenger.COL_INDEX_CONTRACT_ID);
        passenger.condition   = cursor.getInt(OpenHelperPassenger.COL_INDEX_CONDITION);

        return passenger;
    }
}
