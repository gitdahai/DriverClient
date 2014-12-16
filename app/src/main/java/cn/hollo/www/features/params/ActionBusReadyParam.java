package cn.hollo.www.features.params;

import com.android.volley.Request;

import cn.hollo.www.https.HttpRequestParams;
import cn.hollo.www.https.OnRequestListener;

/**
 * Created by orson on 14-12-15.
 * 车辆已经准备好了，准备发车
 PUT /users/<user_id>/tasks/<task_id> 开始任务
 */
public class ActionBusReadyParam implements HttpRequestParams {
    public OnRequestListener lisntener;
    public String user_id;
    public String task_id;

    public int getMethod() {return Request.Method.PUT;}
    public String getUrl() {return "/users/" + user_id + "/tasks/" + task_id;}
    public byte[] getBody() {return new byte[0];}
    public String getContentType() {return "";}
    public OnRequestListener getListener() {return lisntener;}
}
