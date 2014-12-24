package cn.hollo.www.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;

import cn.hollo.www.upyun.DownloadData;
import cn.hollo.www.upyun.picture.DownloadPicture;


/**
 * 图片提供者
 * @author orson
 *
 */
public class ImageProvider {
	/**缩略图*/
	private static final int THUMBNAIL = 1;
	/**完整图*/
	private static final int FULL = 2;
	
	private static ImageProvider instance;
	private Context context;
	
	private ImageProvider(Context context){
		this.context = context;
	}
	
	/**
	 * 获取该类的实例
	 * @param context
	 * @return
	 */
	public static ImageProvider getInstance(Context context){
		if (instance == null)
			instance = new ImageProvider(context);
		
		return instance;
	}
	
	/**
	 * 获取缩略图图片
	 * @param url 	  : 图片的地址
	 * @param rceiver : 图片的接收者
	 */
	public void getThumbnailImage(String url, ImageReceiver rceiver){
		if (url == null || "".equals(url))
			return;
		
		new DoTask(THUMBNAIL, url, rceiver, null).start();
	}
	
	/**
	 * 获取缩略图图片
	 * @param url
	 * @param rceiver
	 */
	public void getThumbnailImage(String url, ImageReceiver rceiver, Object data){
		if (url == null || "".equals(url))
			return;
		
		new DoTask(THUMBNAIL, url, rceiver, data).start();
	}
	
	/**
	 * 获取完整图像
	 * @param url
	 * @param rceiver
	 */
	public void getFullImage(String url, ImageReceiver rceiver, Object attach){
		if (url == null || "".equals(url))
			return;
		
		new DoTask(FULL, url, rceiver, attach).start();
	}
	
	/**
	 * 做任务
	 * @author orson
	 *
	 */
	private class DoTask extends Thread {
		private int type;		//图片的类型（）
		private String url;
		private ImageReceiver rceiver;
		private Object data;
		
		/**
		 * 构造方法
		 * @param type	：　图片的类型
		 * @param url	:  图片的名称
		 * @param rceiver　：　图片的接收者
		 */
		private DoTask(int type, String url, ImageReceiver rceiver, Object data){
			this.type = type;
			this.url = url;
			this.rceiver = rceiver;
			this.data = data;
		}
		
		/**
		 * 线程执行方法
		 */
		public void run() {
			if (type == THUMBNAIL)
				getThumbnail();
			else if (type == FULL)
				getFull();
		}
		
		/**
		 * 获取缩略图
		 */
		private void getThumbnail(){
			String smallName = ImageFileUtils.getSmallName(url);
			loadBitmap(smallName, DownloadPicture.PICTURE_TYPE_SMALL);
		}
		
		/**
		 * 获取完整图像
		 */
		private void getFull(){
			String fullName = ImageFileUtils.getFullName(url);
			loadBitmap(fullName, DownloadPicture.PICTURE_TYPE_FULL);
		}
		
		/**
		 * 加载Bitmap对象
		 * @param fileName
		 * @param type
		 */
		private void loadBitmap(String fileName, String type){
			File file = context.getFileStreamPath(fileName);
			
			//如果本地存在文件，则生成bitmap对象
			if (file.exists() && file.isFile()){
				Bitmap bm = ImageBitmapUtils.createBitmap(file);
				
				if (bm != null){
					Attach attach = new Attach();
					attach.bm = bm;
					attach.data = data;
					attach.rceiver = rceiver;
					resultBitmap(0, attach);
				}
				else{
					String savaPath = file.getAbsolutePath();
					remoteLoadBitmap(url, type, savaPath, rceiver, data);
				}	
			}				
			else{
				try {
					file.createNewFile();
					String savaPath = file.getAbsolutePath();
					remoteLoadBitmap(url, type, savaPath, rceiver, data);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}
	
	
	/**
	 * 远程加载图片
	 * @param url		：　图片的远程地址
	 * @param type		：　图片的类型
	 * @param savePath	:  图片的保存路径（完整路径）
	 * @param rceiver   :  图片接收者
	 */
	private void remoteLoadBitmap(String url, String type, String savePath, ImageReceiver rceiver, Object data){
		DownloadPicture download = new DownloadPicture(url, savePath, type);
		Attach attach = new Attach();
		attach.rceiver = rceiver;
		attach.data = data;
		download.setAttachment(attach);
		download.setOnDownloadFinishListener(downloadListener);
		download.excuteRequest();
	}
	
	/**
	 * 返回bitmap对象
	 */
	private void resultBitmap(int code, Object attach){
		Message msg = mHandler.obtainMessage();
		msg.arg1 = code;
		msg.obj = attach;
		mHandler.sendMessage(msg);
	}
	
	/**
	 * 通知前台，数据到达
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			Attach attach = (Attach)msg.obj;
			attach.rceiver.onImagePush(msg.arg1, attach.bm, attach.data);
		}		
	};
	
	/**
	 * 下载图像返回事件
	 */
	private DownloadData.OnDownloadFinishListener downloadListener = new DownloadData.OnDownloadFinishListener(){
		public void onDownloadFinish(int code, boolean isSave, String saveName, Object attach) {
			Bitmap bm = ImageBitmapUtils.createBitmap(saveName);
			Attach data = (Attach)attach;
			data.bm = bm;
			resultBitmap(code, attach);			
		}		
	};
	
	/**
	 * 附件类
	 * @author orson
	 *
	 */
	private class Attach{
		private Bitmap bm;
		private ImageReceiver rceiver;
		private Object data;
	}
}
