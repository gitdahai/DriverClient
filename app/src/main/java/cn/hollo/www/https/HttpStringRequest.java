package cn.hollo.www.https;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by orson on 14-11-25.
 * 所有的普通请求都需要继承该类
 */
public class HttpStringRequest extends StringRequest{
    private HttpRequestParams params;

    /**
     *
     * @param params
     */
    public HttpStringRequest(HttpRequestParams params) {
        super(params.getMethod(), HttpConstant.domain + params.getUrl(), new ResponseListener(params.getListener()), new ErrorListener(params.getListener()));
        this.params = params;
    }

    @Override
    public String getBodyContentType() {
        return params.getContentType();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return params.getBody();
    }
}
