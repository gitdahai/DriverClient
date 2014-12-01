package cn.hollo.www.features.informations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orson on 14-11-27.
 * 工作任务详情数据
 */
public class WorkTaskDetail {
    public String path_id;
    public String task_id;      //唯一的任务id
    public List<Station>  stations = new ArrayList<Station>();

    /**************************************************
     * 站点数据
     */
    public class Station{
        public List<User> off_users = new ArrayList<User>();     //下车用户
        public List<User> on_users  = new ArrayList<User>();     //上车用户
        public Location   location  = new Location();            //站点的经纬度
        public long arrived_at;                                  //到达时间
        public String name;                                      //站点名称

    }

    /*************************************************
     * 经纬度
     */
    public class Location{
        public double lat;
        public double lng;
    }

    /*************************************************
     * 用户
     */
    public class User{
        public String user_id;
        public String avatar;
        public String nickname;
    }

    public User newUserInstance(){return new User();}
    public Station newStationInstance(){return new Station();}
}
