package cn.hollo.www.https;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

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
    protected Response<String> parseNetworkResponse(NetworkResponse response){
        try {
            String responseString = new String(response.data, "UTF-8");
            return Response.success(responseString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            Log.w("HL-DEBUG", e);
            return super.parseNetworkResponse(response);
        }
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
