package cn.hollo.www.features.informations;

import android.content.ContentValues;

import java.io.Serializable;

import cn.hollo.www.content_provider.OpenHelperWorkTask;

/**
 * Created by orson on 14-11-25.
 * 司机的工作任务
 */
public class WorkTask implements Serializable {
    public String task_id;              //任务id
    public String voiture_number;       //车辆编号
    public String voiture_type;         //车辆类型
    public String departure_station;    //起点站名称
    public String destination_station;  //到达站点名称
    public long   time;                 //日期和时间

    /**
     *
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(OpenHelperWorkTask.TASK_ID,              task_id);
        values.put(OpenHelperWorkTask.VOITURE_NUMBER,       voiture_number);
        values.put(OpenHelperWorkTask.VOITURE_TYPE,         voiture_type);
        values.put(OpenHelperWorkTask.DEPARTURE_STATION,    departure_station);
        values.put(OpenHelperWorkTask.DESTINATION_STATION,  destination_station);
        values.put(OpenHelperWorkTask.DATE_TIME,            time);
        return values;
    }
}
