package cn.hollo.www.features.params;

import com.android.volley.Request;

import cn.hollo.www.https.HttpRequestParams;
import cn.hollo.www.https.OnRequestListener;

/**
 * Created by orson on 14-12-15.
 * 车辆已经到达了某个站点
 * PUT/users/<user_id>/tasks/<task_id>/stations/<station_id> 到达站点
 */
public class ActionArriveStationParam implements HttpRequestParams {
    public OnRequestListener lisntener;
    public String user_id;      //用户id
    public String task_id;      //任务id
    public String station_id;   //站点id

    public int getMethod() {return Request.Method.PUT;}
    public String getUrl() {return "/users/" + user_id + "/tasks/" + task_id + "/stations/" + station_id;}
    public byte[] getBody() {return new byte[0];}
    public String getContentType() {return "";}
    public OnRequestListener getListener() {return lisntener;}
}
