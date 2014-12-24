package cn.hollo.www.image;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 处理照片，图片的类
 * 当从相机，或者相册选择照片后，需要对该图片（照片）
 * 进行转存，缩放等操作，并且需要上传到云端
 * @author orson
 *
 */
public class ImageHandle {

	/**********************************************************************************************/
	
	/**
	 * 把bm对象，暂存到本地
	 * @param file
	 * @param src
	 * @throws java.io.FileNotFoundException
	 */
	private boolean saveBitmap(File file, Bitmap src){
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		boolean result = true;
		
		try {
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			src.compress(Bitmap.CompressFormat.JPEG, 100, dos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = false; 
		}
		finally{			
			if (fos != null) try {fos.close();} catch (IOException e) {e.printStackTrace();}
			if (dos != null) try {dos.close();} catch (IOException e) {e.printStackTrace();}
		}			
		
		return result;
	}
	
	/**
	 * 从文件路径中，抽取出文件的扩展名
	 * @return
	 */
	private String splitExName(String filePath){
		int index = filePath.lastIndexOf('.');
		return filePath.substring(index + 1);
	}
	
	
	/**
	 * 生成一个本地临时缓冲文件对象
	 * @return
	 */
	private File createTempFile(Context context){
		String tempFileName = "" + System.currentTimeMillis() + ".data";
		File localFile = context.getFileStreamPath(tempFileName);
		return localFile;
	}
	
	/**
	 * 线程返回
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			HandlerAttach attach = (HandlerAttach)msg.obj;
			attach.receiver.onHandleReceiver(msg.arg1, attach.thumbnail, attach.bmName, attach.attach);
		}		
	};
	
	/**
	 * 该类的附件类
	 * @author orson
	 *
	 */
	private class HandlerAttach{
		private ImageHandleReceiver receiver;
		private Bitmap thumbnail;
		private String bmName;
		private Object attach;
	}
	
	/**
	 * 照片，图像处理后返回的结果
	 * @author orson
	 *
	 */
	public interface ImageHandleReceiver{
		/**
		 * 当处理完毕通过该方法返回
		 * @param code	: 		处理的状态码
		 * @param thumbnail	: 	缩略图对象
		 * @param bmName	:　　图像存储的文件名称
		 * @param attach	:   附件对象
		 */
		public void onHandleReceiver(int code, Bitmap thumbnail, String bmName, Object attach);
	}
	
	/**
	 * 解析图像（照片）uri的实际存储位置
	 * @param mContext
	 * @param fileUrl
	 * @return
	 */
	private String getParserUrih(Context mContext, Uri fileUrl){
		String result = null;
		String[] projection = {MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(mContext, fileUrl, projection, null, null, null);
		Cursor cursor = loader.loadInBackground();
		
		if (cursor != null){
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			result = cursor.getString(column_index);
			cursor.close();
		}

		return result;
	}
}
