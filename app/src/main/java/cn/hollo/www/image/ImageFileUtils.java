package cn.hollo.www.image;

import java.util.Calendar;

/**
 * 图片工具类
 * @author orson
 *
 */
public class ImageFileUtils {
	/**
	 * 截取出最短文件名
	 * @param url
	 * @return
	 */
	public static String getShortName(String url){
		int index = url.lastIndexOf('/');
		String result = null;
		
		if (index  != -1)
			result = url.substring(index + 1);
		else
			result = url;
		
		return result;
	}
	
	/**
	 * 获取存在本地缩略图的名称
	 * @param url
	 * @return
	 */
	static String getSmallName(String url){
		String shortName = getShortName(url);
		
		return "small_" + shortName;
	}
	
	/**
	 * 获取本地完整图的名称
	 * @param url
	 * @return
	 */
	static String getFullName(String url){
		String shortName = getShortName(url);
		
		return "full_" + shortName;
	}
	
	/**
	 * 66ac7208f9771e911ede96be552c340b/2014/3/66ac7208f9771e911ede96be552c340b_1397525868865.jpg
	 * 生成一个新的，完整的文件名称
	 * @param userName	: 用户名称
	 * @param exName	: 扩展名
	 * /2014/3/21/_1398051600901./2014/3/21/_1398051564646.jpg
	 * @return
	 */
	static String createNewImageName(String userName, String exName){
		return "/" + userName + "/" + getTimeDataString() + "/" + createImageName(userName, exName);
	}
	
	/**
	 * 获取日期字符串
	 * @return
	 */
	private static String getTimeDataString(){
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		return year + "/" + month + "/" + day;				
	}
	
	/**
	 * 生成一个与时间相关的文件名
	 * @param userName
	 * @param exName
	 * @return
	 */
	private static String createImageName(String userName, String exName){
		long timeSpan = System.currentTimeMillis();
		return userName + "_" + timeSpan + "." + exName;
	}
}
