package cn.hollo.www.features.informations;

import java.io.Serializable;

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
    public long   date;                 //日期和时间
}
