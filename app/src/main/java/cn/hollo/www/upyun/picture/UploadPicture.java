package cn.hollo.www.upyun.picture;

import java.io.File;

import cn.hollo.www.upyun.UploadData;

/**
 * 上传照片，图像等文件
 * @author orson
 *
 */
public class UploadPicture extends UploadData {
	/**
	 * 
	 * @param uriName
	 * @param upLoadFile
	 */
	public UploadPicture(String uriName, File upLoadFile) {
		super(uriName, upLoadFile);	
		this.upYun_bucket = "/hollo-photos";
	}
	
	/**
	 * 
	 * @param uriName
	 * @param filePath
	 */
	public UploadPicture(String uriName, String filePath) {
		super(uriName, filePath);	
		this.upYun_bucket = "/hollo-photos";
	}
}
