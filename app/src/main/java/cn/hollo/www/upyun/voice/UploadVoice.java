package cn.hollo.www.upyun.voice;

import cn.hollo.www.upyun.UploadData;

/**
 * 上传语音类 
 * @author orson
 *
 */
public class UploadVoice extends UploadData {
	/**
	 * 构造
	 * @param uriName		： 请求的资源名称
	 * @param filePath		： 上传文件的名称（必须是，含有完整路径文件名）
	 */
	public UploadVoice(String uriName, String filePath) {
		super(uriName, filePath);
		super.upYun_bucket = "/hollo-audios";
	}
}
