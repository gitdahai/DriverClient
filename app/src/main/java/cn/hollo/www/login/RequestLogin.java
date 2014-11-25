package cn.hollo.www.login;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;

import cn.hollo.www.https.HttpRequestParams;
import cn.hollo.www.https.OnRequestListener;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-11-25.
 * 用户登录请求类
 */
public class RequestLogin implements HttpRequestParams{
    public String login_name;
    public String password;
    public OnRequestListener lisntener;

    public int getMethod() {return Request.Method.POST;}
    public String getUrl() {return "/user/login";}
    public String getContentType() {return "application/json";}
    public OnRequestListener getListener() {return lisntener;}

    public byte[] getBody() {
        Map<String, String> values = new HashMap<String, String>();
        values.put("login_name", login_name);
        values.put("password", password);
        return Util.convertJsonString(values).getBytes();
    }
}
