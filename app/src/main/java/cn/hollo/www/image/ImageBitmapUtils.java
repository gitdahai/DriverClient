package cn.hollo.www.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 图片（位图）工具类
 * @author orson
 *
 */
class ImageBitmapUtils {
	/**
	 * 从文件中构造bitmap对象
	 * @param file
	 * @return
	 */
	static Bitmap createBitmap(File file){
		Bitmap bm = null;
		
		if (file.exists()){
			FileInputStream ins = null;
			try {
				ins = new FileInputStream(file);
				bm = BitmapFactory.decodeStream(ins);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}	
			finally{
				if (ins != null)try {ins.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
		
		return bm;
	}
	
	/**
	 * 创建Bitmap对象
	 * @param pathName
	 * @return
	 */
	static Bitmap createBitmap(String pathName){
		Bitmap bm = null;
		File file = new File(pathName);
		bm = createBitmap(file);
		return bm;
	}
}
