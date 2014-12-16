package cn.hollo.www.features.params;

import com.android.volley.Request;

import cn.hollo.www.https.HttpRequestParams;
import cn.hollo.www.https.OnRequestListener;

/**
 * Created by orson on 14-12-2.
 * 通知服务器删除工作任务(任务完成)
 */
public class ActionWorkTaskDeleteParam implements HttpRequestParams {
    public String user_id;
    public String task_id;
    public OnRequestListener listener;

    public int getMethod() {return Request.Method.DELETE;}
    public String getUrl() {return "/users/" + user_id + "/work_tasks/" + task_id;}
    public byte[] getBody() {return new byte[0];}
    public String getContentType() {return "";}
    public OnRequestListener getListener() {return listener;}
}
