package cn.hollo.www.upyun.voice;

import java.io.File;

import cn.hollo.www.upyun.DownloadData;


/**
 * 下载语音数据
 * @author orson
 *
 */
public class DownloadVoice extends DownloadData {

	/**
	 * 构造方法
	 * @param uriName	: 请求的资源名
	 * @param saveFile	： 保存的文件对象
	 */
	public DownloadVoice(String uriName, File saveFile) {
		super(uriName, saveFile);
//		super.upYun_bucket = "/hollo-audios";
		super.host = "hollo-audios.b0.upaiyun.com";
		super.upYun_bucket = "";
	}
	
	/**
	 * 构造方法
	 * @param uriName	: 请求的资源名
	 * @param saveName	： 保存的文件路径（含有绝对路径的文件名）
	 */
	public DownloadVoice(String uriName, String saveName) {
		super(uriName, saveName);
//		super.upYun_bucket = "/hollo-audios";
		super.host = "hollo-audios.b0.upaiyun.com";
		super.upYun_bucket = "";
	}
}
