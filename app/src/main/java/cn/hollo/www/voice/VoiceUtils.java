package cn.hollo.www.voice;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import cn.hollo.www.UserInfo;

/**
 * 语音操作辅助类（单例）
 * @author orson
 *
 */
public class VoiceUtils{
	private static VoiceUtils instance;
	private Context context;
	
	private VoiceUtils(Context context){
		this.context = context;
	}
	
	public static VoiceUtils getInstance(Context context){
		if (instance == null)
			instance = new VoiceUtils(context);
		
		return instance;
	}
	
	/**
	 * 根据文件最短名，生成一个远程上传的地址
	 * @return
	 */
	public static String createVoiceName(Context context){
		Calendar cd = Calendar.getInstance();
		int year = cd.get(Calendar.YEAR);
		int month = cd.get(Calendar.MONTH);
		
		UserInfo userInfo = UserInfo.getInstance(context);
		StringBuilder sb = new StringBuilder();
		sb.append("/");
		sb.append(userInfo.getUserId() + "/");
		sb.append(year + "/");
		sb.append(month + "/");
		sb.append(System.currentTimeMillis() + ".arm");
			
		return sb.toString();		
	}

    /**
     *
     * @param context
     * @param voiceFullName
     * @return
     */
    public static String createCatchName(Context context, String voiceFullName){
        String result = null;
        String voiceShortName = getShortName(voiceFullName);
        String catchDir = context.getCacheDir().getAbsolutePath();
        //String catchDir = context.getFilesDir().getAbsolutePath();
        result = catchDir + "/" + voiceShortName;

        return result;
    }


	/**
	 * 获取文件最短名
	 * @param voiceName
	 * @return
	 */
	public static String getShortName(String voiceName){
		String result = null;
		int index = voiceName.lastIndexOf('/');
		
		if (index != -1)
			result = voiceName.substring(index + 1);
		else
			result = voiceName;
		
		return result;
	}
	
	/**
	 * 判断录音是否存在
	 * @param localVoiceName
	 * @return
	 */
	public boolean isVoiceFileExist(String localVoiceName){
		boolean result = true;
		String pathName = getShortName(localVoiceName);
		FileInputStream file = null;
		
		try {
			file = context.openFileInput(pathName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = false;
		}
		finally{		
			try {
				if (file != null)	file.close();					
			} catch (IOException e) {e.printStackTrace();}				
		}
		
		return result;
	}

	/**
	 * 上传语音文件
	 * @param localVoiceName	: 录音时的保存文件名
	 * @param l
	 * @param attach
	 */
	/*public void uploadVoice(String localVoiceName, OnUploadFinishListener l, Object attach){
		String shortName = getShortName(localVoiceName);
		String uriName = createRemoteVoiceName(shortName);
		UploadVoice upload = new UploadVoice(uriName, localVoiceName);
		upload.setOnUploadFinishListener(l);
		upload.setAttachment(attach);
		upload.excuteRequest();
	}*/
	
	/**
	 * 上传语音文件
	 * @param remoteName	: 录音时的保存文件名
	 * @param l
	 * @param attach
	 */
	/*public void uploadVoiceToUpyun(String remoteName, OnUploadFinishListener l, Object attach){
		String shortName = getShortName(remoteName);
		String localVoiceName = createLocalVoiceName(shortName);

		UploadVoice upload = new UploadVoice(remoteName, localVoiceName);
		upload.setOnUploadFinishListener(l);
		upload.setAttachment(attach);
		upload.excuteRequest();
	}*/
	
	/**
	 * 下载录音文件
	 * @param remoteVoiceName
	 * @param listener
	 * @param attach
	 */
	/*public  void downloadVoice(String remoteVoiceName, OnDownloadFinishListener listener, Object attach){
		String shortName = getShortName(remoteVoiceName);
		String saveName = getFullVoiceName(shortName);
		DownloadVoice download = new DownloadVoice(remoteVoiceName, saveName);
		download.setOnDownloadFinishListener(listener);
		download.setAttachment(attach);
		download.excuteRequest();
	}*/
}
