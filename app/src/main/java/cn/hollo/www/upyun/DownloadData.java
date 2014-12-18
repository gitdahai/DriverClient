package cn.hollo.www.upyun;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 下载数据
 * @author orson
 * 
	http://hollo-photos.b0.upaiyun.com/91/201401/13...
 *
 */
public abstract class DownloadData extends UpYunBaseClass {
	private OnDownloadFinishListener listener;	//下载完成监听器
	private Object attach;		//请求时，携带的附件
	private File saveFile;	//保存的文件对象
	
	/**
	 * 
	 * @param uriName	： 请求的资源名称
	 * @param saveFile  :  保存下载后的文件对象
	 */
	public DownloadData(String uriName, File saveFile){
		setParams();
		super.uriName = uriName;
		this.saveFile = saveFile;
	}
	
	/**
	 * 
	 * @param uriName	: 请求资源的名称
	 * @param saveName	：保存下载后的文件名（含完整路径的文件名）
	 */
	public DownloadData(String uriName, String saveName){
		setParams();
		super.uriName = uriName;
		this.saveFile = new File(saveName);
	}
	
	/**
	 * 设置请求参数
	 */
	private void setParams(){
		super.host = "hollo-photos.b0.upaiyun.com";
		super.upYun_bucket = "";
	}
	
	/**
	 * 设置监听器对象
	 * @param l
	 */
	public void setOnDownloadFinishListener(OnDownloadFinishListener l){
		listener = l;
	}
	
	/**
	 * 设置携带的附件对象
	 * @param attach
	 */
	public void setAttachment(Object attach){
		this.attach = attach;
	}
	
	/**
	 * 执行请求操作
	 */
	public void excuteRequest(){
		new Thread(new RequestExcuteThread()).start();
	}
	
	/**
	 * 请求线程
	 * @author orson
	 *
	 */
	private class RequestExcuteThread implements Runnable {
		public void run() {
			String url = http + host + upYun_bucket + uriName;
			HttpGet httpGet = (HttpGet)getHttpUriRequest(url, GET, 0);
			httpGet.addHeader("Content-Length", "0");
            httpGet.addHeader("User-Agent", "Pinche/");
			//HttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpClient httpClient = new DefaultHttpClient();

			int responseCode = -1;
			InputStream is = null;
			FileOutputStream fos = null;
			DataOutputStream dos = null;
			DataInputStream dis = null;
			
			boolean isSuccess = false;
			
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				
				//取得数据记录
				HttpEntity entity = httpResponse.getEntity();
				
				if(responseCode == HttpStatus.SC_OK){
			        //取得数据记录内容
			        is = entity.getContent();
			        
			        int totalCount = is.available();
			        
			        fos = new FileOutputStream(saveFile);
			        dos = new DataOutputStream(fos);
			        dis = new DataInputStream(is);
			        
			        int totalReadCount = 0;
			        byte[] buffer = new byte[8192];
			        int count = 0;
			        
			        //输出数据到文件
			        while ((count = dis.read(buffer)) > 0){
			        	dos.write(buffer, 0, count);
			        	totalReadCount += count;
			        }    
			        
			        //System.out.println("download size = " + totalReadCount);
			        //判断保存的数据是否完整			   
			        isSuccess = true;
			    }
				
			} 
			catch (ClientProtocolException e) { e.printStackTrace();}
			catch (IOException e) { e.printStackTrace(); }
			finally{
				try { if (dis != null) dis.close(); } catch (IOException e) { e.printStackTrace();}
				try { if (is  != null) is.close();  } catch (IOException e) { e.printStackTrace();}
				try { if (dos != null) dos.close(); } catch (IOException e) { e.printStackTrace();}
				try { if (fos != null) fos.close(); } catch (IOException e) { e.printStackTrace();}
			}			
			
			//回调通知
			if (listener != null)
				listener.onDownloadFinish(responseCode, isSuccess, saveFile.getAbsolutePath(), attach);
		}	
	}
	
	/**
	 * 下载监听器
	 * @author orson
	 *
	 */
	public static abstract class OnDownloadFinishListener{
		public abstract void onDownloadFinish(int code, boolean isSave, String saveName, Object attach);
	}
}
