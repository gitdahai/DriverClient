package cn.hollo.www.voice;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * 语音录制类
 * @author orson
 *
 */
public class VoiceRecorder {
	private static VoiceRecorder instance;
	private OnRecorderDecibelListener listener;				//录音时的分贝监听器对象
	private OnRecorderFinishListener  finishRecorderListener;
	private MediaRecorder 			  audioRecorder;		//录音对象
	private boolean 				  isRecording;			//表示当前是否录音的状态
	private String 					  filePath;				//录音保存文件
	private Thread				  	  timerThread;
	private TimerTask				  timerTask;
	private int 					  timeInterval = 100;	//定时器的启动的间隔时间为20毫秒
	private int errorCount = 0;
	private int maxSeconds = 60000;	//最大录音时常60秒
	private int minSeconds = 5000;	//最小录音时常5秒
	private int currSeconds = 0;
	private boolean isSuccess;
	
	/**
	 * 构造方法
	 */
	private VoiceRecorder(){
		audioRecorder = new MediaRecorder();
		timerTask     = new TimerTask();
	}
	
	/**
	 * 获取该类的实例对象 
	 * @return
	 */
	public static VoiceRecorder getInstance(){
		if (instance == null)
			instance = new VoiceRecorder();
		
		return instance;
	}
	
	/**
	 * 设置存储路径（完整路径）
	 * @param filePath
	 */
	public void setPath(String filePath){
		this.filePath = filePath;		
	}
	
	/**
	 * 设置最大录音长度
	 * @param seconds
	 */
	public void setMaxSeconds(int seconds){
		this.maxSeconds = seconds;
	}
	
	/**
	 * 设置最小录音时常
	 * @param seconds
	 */
	public void setMinSeconds(int seconds){
		this.minSeconds = seconds;
	}
	
	/**
	 * 设置分贝返回监听器
	 * @param l
	 */
	public void setOnRecorderDecibelListener(OnRecorderDecibelListener l){
		listener = l;
	}
	
	/**
	 * 设置停止录音监听器
	 * @param l
	 */
	public void setOnRecorderFinishListener(OnRecorderFinishListener l){
		finishRecorderListener = l;
	}
	
	/**
	 * 开始
	 * @throws java.io.IOException
	 * @throws IllegalStateException 
	 */
	public void start(){
		
		Exception exception = null;
		
		if (!isRecording){
		    if(audioRecorder == null)
		        audioRecorder = new MediaRecorder();
		    
		    //录音时常，开始为0
		    currSeconds = 0;
		    //录音标志设置为true,以便与可获取录音的分贝
			isRecording = true;
			//开始录音前，该标志为false
			isSuccess = false;
			
			try {
				audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
				audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                audioRecorder.setAudioSamplingRate(8000);
				audioRecorder.setAudioChannels(2);
				audioRecorder.setOutputFile(filePath);				
				audioRecorder.prepare();
				audioRecorder.start();

				/*
				 * 当，获取分贝的监听器，不为null时，才启动定时器
				 * 启动定时器
				 */
				if (listener != null){					
					timerThread = new Thread(timerTask);					
					timerThread.start();
				}
					
				isSuccess = true;
			} 
			catch (IllegalStateException e) {exception = e;} 				
			catch (IOException e) {exception = e;} 				
			catch(Exception e){exception = e;}
			
			if (exception != null){
				 clear();
				 
				 //通知，启动录音失败
				 if (finishRecorderListener != null)
						finishRecorderListener.onRecorderFinsihListener(isSuccess, filePath, currSeconds);
			}
		}
	}
	
	private void clear(){
	    if(audioRecorder != null){
	        audioRecorder.release();
	        audioRecorder = null;
	    }
        isRecording = false;    
        if(listener != null && timerThread != null){
            timerThread.interrupt();
        }
	}
	/**
	 * 停止
	 */
	public void stop(){
		//如果已经停止了，则不执行
		if (!isRecording)
			return;
		
		isRecording = false;
		
		if(audioRecorder != null){
		    
		    
		    try {
		        
	            audioRecorder.stop();

	            
	        } catch (RuntimeException e) {
	            // the recording did not succeed
	            Log.w("SMACK", "Failed to record", e);
	        } finally {
	            audioRecorder.reset();
	        }   
//			  audioRecorder.stop();
//            audioRecorder.reset();
		}
			
		if(listener != null && timerThread != null){
		    timerThread.interrupt();
		}
		
		//通知，录音结束
		if (finishRecorderListener != null)
			finishRecorderListener.onRecorderFinsihListener(isSuccess, filePath, currSeconds);
	}
	
//	
//	/**
//	 * 释放录音资源
//	 */
//	 void releaseMediaRecorder() {
//	        if (this.audioRecorder != null) {
//	            this.audioRecorder.reset(); // clear configuration (optional here)
//	          //  this.audioRecorder.release();
//	            this.audioRecorder = null;
//	        }
//	    }
//	
	
	/**
	 * 释放资源
	 */
	public void release(){
		if (isRecording)
			stop();
			
		if(audioRecorder != null){
		    audioRecorder.release();
		    audioRecorder = null;
		}
		instance = null;
	}
	
	/**
	 * 定时任务
	 */
	private class TimerTask implements Runnable{
		public void run() {
			int amp = 0;
			int dB = 0;
			currSeconds = 0;
			
			while (isRecording){
				amp = audioRecorder.getMaxAmplitude();
				dB = (int)(10 * Math.log10(amp));
				
				if (dB >= 0)
					listener.onRecorderDecibel(dB);	

				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//记录当前录音的时常
				currSeconds += timeInterval;
				
				//当录音时常超过最大时常，则停止录音
				if (currSeconds >= maxSeconds){
					stop();
					return;
				}					
			}					
		}		
	};
	
	/**
	 * 返回录音的分贝
	 * @author orson
	 *
	 */
	public static abstract class OnRecorderDecibelListener{
		public abstract void onRecorderDecibel(int db);
	}
	/**
	 * 准备给停止录音的监听用的.
	 * @author liuhanru
	 *
	 */
	public interface OnRecorderFinishListener{
		/**
		 * 返回
		 * @param path		: 保存录音文件的路径（用用户传递进来的）
		 * @param seconds   : 录音的时常
		 * @param isSuccess : 录音是否成功
		 */
	    public abstract void onRecorderFinsihListener(boolean isSuccess, String path, int seconds);
	}
}
