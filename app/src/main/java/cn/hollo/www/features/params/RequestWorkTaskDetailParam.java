package cn.hollo.www.features.params;

import com.android.volley.Request;

import cn.hollo.www.https.HttpRequestParams;
import cn.hollo.www.https.OnRequestListener;

/**
 * Created by orson on 14-11-27.
 * 请求工作任务的参数
 *
 */
public class RequestWorkTaskDetailParam implements HttpRequestParams {
    public String user_id;
    public String task_id;
    public OnRequestListener listener;

    public int getMethod() {return Request.Method.GET;}
    public String getUrl() {return "/users/" + user_id + "/work_tasks/unfinished/" + task_id;}
    public byte[] getBody() {return new byte[0];}
    public String getContentType() {return "";}
    public OnRequestListener getListener() {return listener;}
}
