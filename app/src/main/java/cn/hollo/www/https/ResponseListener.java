package cn.hollo.www.https;

import com.android.volley.Response;

/**
 * Created by orson on 14-11-25.
 *
 */
class ResponseListener implements Response.Listener<String> {
    private OnRequestListener listener;

    ResponseListener(OnRequestListener listener){
        this.listener = listener;
    }

    @Override
    public void onResponse(String s) {
        try{
            if (listener != null)
                listener.onResponse(200, s);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
