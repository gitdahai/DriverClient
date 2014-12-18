package cn.hollo.www.upyun.picture;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.hollo.www.upyun.UpYunBaseClass;


/**
 * 上传Bitmap类:
 * 该类不是异步
 * @author orson
 *
 */
public class UploadBitmap extends UpYunBaseClass {
	private OnUploadFinishListener finishListener;
	private File bmFile;			//上传的位图
	private Object attach;		    //携带额附件
	
	/*
	 * 构造方法
	 */
	public UploadBitmap(File bmFile, String uriName) {
		super.uriName 	= uriName;
		super.upYun_bucket = "/hollo-photos";
		this.bmFile = bmFile;
	}
	
	/**
	 * 设置上传完成事件监听器
	 * @param l
	 */
	public void setOnUploadFinishListener(OnUploadFinishListener l){
		finishListener = l;
	}
	
	/**
	 * 设置携带的附件
	 * @param attach
	 */
	public void setAttachment(Object attach){
		this.attach = attach;
	}
	
	/**
	 * 执行上传操作
	 */
	public void excuteRequest(){
		upLoad();
	}
	
	/**
	 * 上传方法
	 */
	public int upLoad(){
		int code = -1;
		try {
			
			//获取输入文件的长度
			long fileLength = bmFile.length();
			//请求地址
			String url = http + host + upYun_bucket + uriName;
			//获取请求链接实体对象
			HttpUriRequest request = getHttpUriRequest(url, PUT, fileLength);
			//
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			//构造输入流实体对象
			FileEntity entity = new FileEntity(bmFile, "image/jpeg");
			//设置到传输对象上
			((HttpPut)request).setEntity(entity);
			//执行请求操作
			HttpResponse httpResponse = httpClient.execute(request);
			//请求返回码
			code = httpResponse.getStatusLine().getStatusCode();
			//请求返回的输入流
			//InputStream ins = httpResponse.getEntity().getContent();
			
			//String resultStr = null;
			
			//读取返回信息
			/*if (ins != null){
				DataInputStream dis = new DataInputStream(ins);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				byte[] buffer = new byte[4096];
				int count = 0;
				
				//读取
				while ((count = dis.read(buffer)) > 0)
					baos.write(buffer, 0, count);

				resultStr = baos.toString();
				
				dis.close();
				baos.close();
			}*/

			//ins.close();				
			
			//回调通知
			/*if (finishListener != null)
				finishListener.onUploadFinish(code, resultStr, attach);*/
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return code;
	}
	
	/**
	 * 上传事件回调
	 * @author orson
	 *
	 */
	public abstract static class OnUploadFinishListener{
		public abstract void onUploadFinish(int code, String fileName, Object attach);
	}
	
}
