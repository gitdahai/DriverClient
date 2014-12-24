package cn.hollo.www.upyun;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by orson on 14-12-22.
 */
public class Utils {
    /*****************************************************
     * 构造服务器文件名
     * @param filePathName
     * @return
     */
    public static String createUriName(String userId, String filePathName){
        StringBuilder sb = new StringBuilder();
        //添加用户id段
        sb.append("/" + userId);
        //添加当前日期段
        sb.append("/" + getCurrentTimeString());
        //添加文件名
        sb.append("/" + extractFileName(filePathName));
        return sb.toString();
    }

    /*****************************************************
     * 由服务器文件名称，转换成本地存储文件名
     * @param context
     * @param uriName
     * @return
     */
    public static String getLocalFilePathFullName(Context context, String uriName){
        String localFullPathName = null;
        String shortName = extractFileName(uriName);
        String localDir = context.getCacheDir().getAbsolutePath();
        localFullPathName = localDir + "/" + shortName;

        return localFullPathName;
    }

    /*****************************************************
     * 获取当前时间路径名称
     * @return
     */
    private static String getCurrentTimeString(){
        String timeString = null;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        timeString = year + "/" + month;
        return timeString;
    }

    /*****************************************************
     * 从文件路径中抽取文件名称
     * @param filePathName
     * @return
     */
    private static String extractFileName(String filePathName){
        String result = null;
        int index = filePathName.lastIndexOf('/');
        result = filePathName.substring(index + 1);
        return result;
    }
}
