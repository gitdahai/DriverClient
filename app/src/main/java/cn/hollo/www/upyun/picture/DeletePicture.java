package cn.hollo.www.upyun.picture;
import cn.hollo.www.upyun.DeleteData;

/**
 * 删除照片，图像等资源
 * @author orson
 *
 */
public class DeletePicture extends DeleteData {
	public DeletePicture(String uriName) {
		super(uriName);
		this.upYun_bucket = "/hollo-photos";
	}
}
