package cn.hollo.www.upyun.voice;
import cn.hollo.www.upyun.DeleteData;

/**
 * 删除语音文件
 * @author orson
 *
 */
public class DeleteVoice extends DeleteData {
	/**
	 * 构造方法
	 * @param uriName	: 资源名
	 */
	public DeleteVoice(String uriName) {
		super(uriName);
		super.upYun_bucket = "/hollo-audios/";
	}
}
