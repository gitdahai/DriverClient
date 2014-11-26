package cn.hollo.www.https;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by orson on 14-11-25.
 */
class ErrorListener implements Response.ErrorListener {
    private OnRequestListener listener;
    ErrorListener(OnRequestListener listener ){this.listener = listener;}

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        int code = -1;
        String msg = null;

        if (volleyError != null){
            if (volleyError.networkResponse != null){
                code = volleyError.networkResponse.statusCode;
                msg = new String(volleyError.networkResponse.data);
            }
            else
                msg =  volleyError.getMessage();
        }

        try{
            if (listener != null)
                listener.onResponse(code, msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
