package cn.hollo.www.image;

/**
 * Created by orson on 14-12-25.
 */
public class ImageUtils {
    /*********************************************************
     * 从文件路径中抽取文件名称
     * @param filePathName
     * @return
     */
    public static String extractFileName(String filePathName){
        String fileName = null;
        int index = filePathName.lastIndexOf('/');
        //如果没有找到，则整个都作为文件名
        if (index < 0)
            fileName = filePathName;
        //否则，则进行截取
        else
            fileName = filePathName.substring(index + 1);

        return fileName;
    };
}
