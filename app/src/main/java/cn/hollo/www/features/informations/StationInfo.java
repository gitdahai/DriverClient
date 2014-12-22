package cn.hollo.www.features.informations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orson on 14-11-27.
 * 工作任务详情数据
 */
public class StationInfo {
    public String path_id;
    public String task_id;      //唯一的任务id
    public String name;         //
    public List<Station>  stations = new ArrayList<Station>();

    /**************************************************
     * 站点数据
     */
    public class Station{
        public List<Passenger> off_users = new ArrayList<Passenger>();     //下车用户
        public List<Passenger> on_users  = new ArrayList<Passenger>();     //上车用户
        public Location   location  = new Location();            //站点的经纬度
        public long arrived_at;                                  //到达时间
        public String name;                                      //站点名称
        public String _id;                                       //站点的id
    }

    /*************************************************
     * 经纬度
     */
    public class Location{
        public double lat;
        public double lng;
    }

    public Station newStationInstance(){return new Station();}
}
