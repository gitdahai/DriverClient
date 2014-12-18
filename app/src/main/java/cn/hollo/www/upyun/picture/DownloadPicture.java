package cn.hollo.www.upyun.picture;

import java.io.File;

import cn.hollo.www.upyun.DownloadData;


public class DownloadPicture extends DownloadData {
	public static final String PICTURE_TYPE_FULL  = "full";			//指定大小的图片（640）
	public static final String PICTURE_TYPE_SMALL = "small";		//缩略图 (128)
	public static final String PICTURE_TYPE_ORIGIN= "origin";		//原图

	/**
	 * 
	 * @param uriName		： 请求的资源名称
	 * @param saveName		:  保存资源的名称（含有绝对路径的完整名称）
	 */
	public DownloadPicture(String uriName, String saveName){
		super(uriName, saveName);
	}
	
	/**
	 * 
	 * @param uriName
	 * @param saveName
	 * @param type
	 */
	public DownloadPicture(String uriName, String saveName, String type){
		super(uriName + "!" + type, saveName);
		//super.upYun_bucket = "/hollo-photos";
	}
	
	/**
	 * 
	 * @param uriName		: 请求的资源名称
	 * @param saveFile		: 保存资源的文件对象（必须是一个普通类型的文件对象，不能是路径或者其他特殊文件）
	 */
	public DownloadPicture(String uriName, File saveFile){
		super(uriName, saveFile);
		//super.upYun_bucket = "/hollo-photos";
	}	
}
