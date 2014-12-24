package cn.hollo.www.image;

import android.graphics.Bitmap;

/**
 * 缩略图接收接口：
 * 该接口产生的图片，适合头像，icon等小的图片
 * @author orson
 *
 */
public interface ImageReceiver {
	public void onImagePush(int code, Bitmap bm, Object attach);
}
