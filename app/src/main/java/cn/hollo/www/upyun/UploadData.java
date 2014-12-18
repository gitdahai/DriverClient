package cn.hollo.www.upyun;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据上传
 * @author orson
 *
 */
public abstract class UploadData extends UpYunBaseClass {	
	private OnUploadFinishListener finishListener;
	private File upLoadFile;	//上传文件对象
	private Object attach;
	
	/**
	 * 构造方法
	 * @param uriName		: 请求的资源名
	 * @param upLoadFile	: 将要上传的文件
	 */
	public UploadData(String uriName, File upLoadFile) {
		this.uriName 	= uriName;
		this.upLoadFile = upLoadFile;
	}

	/**
	 * 
	 * @param uriName	: 请求的资源名
	 * @param filePath	: 需要上传的文件名称（完整路径）
	 */
	public UploadData(String uriName, String filePath) {
		this.uriName 	= uriName;
		this.upLoadFile = new File(filePath);
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
	
	public void excuteRequest(){
		new Thread(new UpLoadExcute()).start();
	}
	
	/**
	 * 上传执行器
	 * @author orson
	 *
	 */
	private class UpLoadExcute  implements Runnable {
		public void run() {
			try {
				//将要上传的文件对象流
				FileInputStream fis = new FileInputStream(upLoadFile);
				//获取输入文件的长度
				int fileLength = fis.available();
				//请求地址
				String url = http + host + upYun_bucket + uriName;
				//获取请求链接实体对象
				HttpUriRequest request = getHttpUriRequest(url, PUT, fileLength);
				//
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				//构造输入流实体对象
				InputStreamEntity entity = new InputStreamEntity(fis, fileLength);
				//设置到传输对象上
				((HttpPut)request).setEntity(entity);
				//执行请求操作
				HttpResponse httpResponse = httpClient.execute(request);
				//请求返回码
				int code = httpResponse.getStatusLine().getStatusCode();
				//请求返回的输入流
				InputStream ins = httpResponse.getEntity().getContent();
				
				String resultStr = null;
				
				//读取返回信息
				if (ins != null){
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
				}

				ins.close();
				fis.close();				
				
				//回调通知
				if (finishListener != null)
					finishListener.onUploadFinish(code, resultStr, attach);
				
			} catch (FileNotFoundException e) {
                if (finishListener != null)
                    finishListener.onUploadFinish(-1, "", attach);
			} catch (IOException e) {
                if (finishListener != null)
                    finishListener.onUploadFinish(-1, "", attach);

            }
		}		
	}
	
	/**
	 * 上传事件回调
	 * @author orson
	 *
	 */
	public abstract static class OnUploadFinishListener{
		public abstract void onUploadFinish(int code, String resultString, Object attach);
	}
}
