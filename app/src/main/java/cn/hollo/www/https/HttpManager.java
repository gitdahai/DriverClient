package cn.hollo.www.https;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by orson on 14-11-25.
 * http请求管理者
 */
public class HttpManager {
    private static HttpManager instance;
    private RequestQueue mQueue;

    public static HttpManager getInstance(){
        if (instance == null)
            instance = new HttpManager();

        return instance;
    }

    /**
     * 创建
     * @param context
     */
    public void create(Context context){
        mQueue = Volley.newRequestQueue(context);
    }

    /**
     * 销毁
     */
    public void destroy(){
        instance = null;
        mQueue = null;
    }

    /**
     * 添加爱执行任务
     * @param request
     */
    public void addRequest(Request request){
        mQueue.add(request);
    }
}
