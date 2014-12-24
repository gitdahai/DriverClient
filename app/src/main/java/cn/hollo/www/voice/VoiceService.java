package cn.hollo.www.voice;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.hollo.www.R;


/**
 * 录音，播放录音服务类
 * @author orson
 *
 */
public class VoiceService implements VoiceRecorder.OnRecorderFinishListener {
	private VoiceRecorder recorder;
	private VoicePlay	  voicePlay;
	private Context       context;
	
	private static VoiceService instance;
	private VoiceRecorder.OnRecorderFinishListener recorderFinishListener;
	
	private View 	  		 dialogView;	//对话框的试图
	public ImageView 		 voiceView;		//录音效果试图
	private ClipDrawable 	 clipDrawable;

	/**
	 * 私有构造方法
	 * @param context
	 */
	private VoiceService(Context context, LinearLayout contentView){
		this.context 	 = context;
		
		recorder = VoiceRecorder.getInstance();
		voicePlay = new VoicePlay();
		
		recorder.setOnRecorderDecibelListener(recorderListener);
		recorder.setOnRecorderFinishListener(this);

        dialogView =  View.inflate(context, R.layout.voice_dialog_layout, null);
        voiceView = (ImageView)dialogView.findViewById(R.id.voiceSpeekView);
        clipDrawable = (ClipDrawable)voiceView.getDrawable();

        //清空以前的容器
        if (contentView != null){
            contentView.removeAllViews();
            contentView.addView(dialogView);
            dialogView.setVisibility(View.GONE);
        }
	}
	
	/**
	 * 返回该类的实例对象
	 * @param context
	 * @param contentView : 显示录音对话框的容器
	 * @return
	 */
	public static VoiceService getInstance(Context context, LinearLayout contentView){
		if (instance == null){
			instance = new VoiceService(context, contentView);
		}

		return instance;
	}

	/**
	 * 开始录音
	 * @param path : 保存的文件名
	 */
	public void startVoiceRecorder(String path){
        dialogView.setVisibility(View.VISIBLE);
		recorder.setPath(path);
		recorder.start();
	}
	
	/**
	 * 停止录音
	 */
	public void stopVoiceRecorder(){
        dialogView.setVisibility(View.GONE);
		recorder.stop();
	}
	
	/**
	 * 开始播放
	 * @param path
	 */
	public void startPlay(String path){
		voicePlay.setFilePath(path);		
		voicePlay.play();
	}
	
	/**
	 * 开始播放
	 * @param path
	 */
	public void startPlay(String path, int attachId){
		voicePlay.setFilePath(path);		
		voicePlay.setPlayId(attachId);
		voicePlay.play();
	}
	/**
	 * 停止播放
	 */
	public void stopPlay(){
		voicePlay.stop();
	}
	
	/**
	 * 设置播放事件监听器
	 * @param l
	 */
	public void setOnPlayListener(VoicePlay.OnPlayFinishListener l){
		voicePlay.setOnPlayFinishListener(l);
	}
	
	/**
	 * 设置录音完成监听器
	 * @param l
	 */
	public void setOnRecorderFinishListener(VoiceRecorder.OnRecorderFinishListener l){
		recorderFinishListener = l;
	}
	
	/**
	 * 释放所有的资源:
	 * 如果调用该方法后，所有的资源将不再可用，需要重新构造对象来使用
	 */
	public void release(){
		try{
			recorder.stop();
			recorder.release();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			voicePlay.stop();
			voicePlay.release();			
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		
		instance = null;
	}

	/**
	 * 获得录音的分贝
	 */
	private VoiceRecorder.OnRecorderDecibelListener recorderListener = new VoiceRecorder.OnRecorderDecibelListener(){
		public void onRecorderDecibel(int db) {
			Message msg = myHandler.obtainMessage();
			msg.what = 0;
			msg.arg1 = db;			
			myHandler.sendMessage(msg);

            System.out.println("==============msg=========111");
		}		
	};
	
	
	private float decibel = 0f;
	private int   dbLevel = 0;
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == 0){
				decibel = msg.arg1;
				dbLevel = (int)(10000 * (decibel / 100));	
				clipDrawable.setLevel(dbLevel);
			}
			/*else if (msg.what == 1){
				boolean isSuccess = (msg.arg1 == 0 ? false : true);
				String  path = (String)msg.obj;
				int  millis  = msg.arg2;
				
				if (recorderFinishListener != null)
					recorderFinishListener.onRecorderFinsihListener(isSuccess, path, millis);
				
				//隐藏话筒图标
				dialogView.setVisibility(View.GONE);
			}*/
		}		
	};

	@Override
	public void onRecorderFinsihListener(boolean isSuccess, String path, int millis) {	
		Message msg = myHandler.obtainMessage();
		msg.arg1 = (isSuccess == true ? 1 : 0);
		msg.arg2 = millis;
		msg.obj  = path;
		msg.what = 1;
		myHandler.sendMessage(msg);			
	}
}
