package cn.hollo.www.upyun;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 删除数据操作类
 * @author orson
 *
 */
public abstract class DeleteData extends UpYunBaseClass {
	private OnDeleteFinishedListener listener;
	private Object attach;
	
	/**
	 * 构造方法 
	 * @param uriName ： 删除的资源名称
	 */
	public DeleteData(String uriName){
		this.uriName = uriName;
	}
	
	/**
	 * 设置，删除完成后的事件通知
	 * @param l
	 */
	public void setOnDeleteFinishedListener(OnDeleteFinishedListener l){
		listener = l;
	}
	
	/**
	 * 设置附件的附件
	 * @param attach
	 */
	public void setAttach(Object attach){
		this.attach = attach;
	}
	
	/**
	 * 执行请求操作
	 */
	public void excuteRequest(){
		new Thread(new ExcuteThread()).start();
	}

	/**
	 * 线程执行器
	 * @author orson
	 *
	 */
	private class ExcuteThread implements Runnable {
		public void run() {
			String url = http + host + upYun_bucket + uriName;
			HttpDelete httpDelete = (HttpDelete)getHttpUriRequest(url, DELETE, 0);

			httpDelete.addHeader("Content-Length", "0");			
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			
			int responseCode = -1;
			String resultString = null;
			
			try {
				HttpResponse httpResponse = httpClient.execute(httpDelete);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				
				//取得数据记录
				HttpEntity entity = httpResponse.getEntity();
				resultString = EntityUtils.toString(entity);
			} 
			catch (ClientProtocolException e) { e.printStackTrace();}
			catch (IOException e) { e.printStackTrace(); }

			//回调通知
			if (listener != null)
				listener.onDeleteFinished(responseCode, resultString, attach);
		}		
	}
	
	/**
	 * 事件监听器
	 * @author orson
	 *
	 */
	public abstract static class OnDeleteFinishedListener{
		public abstract void onDeleteFinished(int code, String resultString, Object attach);
	}
}
