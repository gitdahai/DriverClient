package cn.hollo.www.others;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;

import cn.hollo.www.https.HttpRequestParams;
import cn.hollo.www.https.OnRequestListener;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-11-26.
 * 修改用户密码参数类
 */
public class RequestModifyPasswordPram implements HttpRequestParams {
    public String old_password;
    public String new_password;
    public String user_id;
    public OnRequestListener listener;

    public int getMethod() {return Request.Method.PUT;}
    public String getUrl() {return "/users/" + user_id + "/new_password";}
    public String getContentType() {return "application/json";}
    public OnRequestListener getListener() {return listener;}
    public byte[] getBody() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("old_password", old_password);
        params.put("new_password", new_password);
        return Util.convertJsonString(params).getBytes();
    }
}
