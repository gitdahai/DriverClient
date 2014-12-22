package cn.hollo.www.upyun.voice;

import cn.hollo.www.upyun.UploadData;
import cn.hollo.www.upyun.Utils;

/**
 * 上传语音类 
 * @author orson
 *
 */
public class UploadVoice extends UploadData {
	/**
	 * 构造
	 * @param userId		： 请求的资源名称
	 * @param filePath		： 上传文件的名称（必须是，含有完整路径文件名）
	 */
	public UploadVoice(String userId, String filePath) {
		super(Utils.createUriName(userId, filePath), filePath);
		super.upYun_bucket = "/hollo-audios";
	}
}
