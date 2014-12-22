package cn.hollo.www.features.informations;

import android.content.ContentValues;

import java.io.Serializable;

import cn.hollo.www.content_provider.OpenHelperWorkTask;

/**
 * Created by orson on 14-11-25.
 * 司机的工作任务
 */
public class Mission implements Serializable {
    public String task_id;              //任务id
    public String room_id;              //聊天室的id
    public String shuttle_name;         //车辆编号
    public String _type;                //车辆类型
    public String departure;            //起点站名称
    public String destination;          //到达站点名称
    public long   depart_at;            //日期和时间

    /**
     *
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(OpenHelperWorkTask.TASK_ID,              task_id);
        values.put(OpenHelperWorkTask.ROOM_ID,              room_id);
        values.put(OpenHelperWorkTask.VOITURE_NUMBER,       shuttle_name);
        values.put(OpenHelperWorkTask.VOITURE_TYPE,         _type);
        values.put(OpenHelperWorkTask.DEPARTURE_STATION,    departure);
        values.put(OpenHelperWorkTask.DESTINATION_STATION,  destination);
        values.put(OpenHelperWorkTask.DATE_TIME,            depart_at);
        return values;
    }
}
