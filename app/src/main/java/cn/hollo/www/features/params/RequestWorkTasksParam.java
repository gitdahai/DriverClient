package cn.hollo.www.features.params;

import com.android.volley.Request;

import cn.hollo.www.https.HttpRequestParams;
import cn.hollo.www.https.OnRequestListener;

/**
 * Created by orson on 14-11-25.
 * 请求工作任务
 */
public class RequestWorkTasksParam implements HttpRequestParams {
    public OnRequestListener lisntener;
    public String user_id;

    public int getMethod() {return Request.Method.GET;}
    public String getUrl() {return "/work_tasks/unfinished/" + user_id;}
    public byte[] getBody() {return new byte[0];}
    public String getContentType() {return "";}
    public OnRequestListener getListener() {return lisntener;}
}
