package cn.hollo.www.https;

/**
 * Created by orson on 14-11-25.
 */
public interface HttpRequestParams {
    public int getMethod();
    public String getUrl();
    public byte[] getBody();
    public String getContentType();
    public OnRequestListener getListener();
}
