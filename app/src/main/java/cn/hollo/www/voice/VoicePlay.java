package cn.hollo.www.voice;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 语音播放
 * 
 * @author orson
 * 
 */
public class VoicePlay {
    private OnPlayFinishListener onFinishListener;
    private int playId;
    private MediaPlayer mediaPlayer;
    private String filePath;
    private Object attach;

    /**
     * 构造方法
     */
    public VoicePlay() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
    }

    public void setPlayId(int id) {
        playId = id;
    }

    public Object getAttach(){
        return attach;
    }

    /**
     * 添加一个附件
     * @param data
     */
    public void setAttach(Object data){
        this.attach = data;
    }

    /**
     * 设置播放文件的路径
     * 
     * @param path
     */
    public void setFilePath(String path) {
        this.filePath = path;
    }

    /**
     * 设置播放事件监听器
     * 
     * @param l
     */
    public void setOnPlayFinishListener(OnPlayFinishListener l) {
        onFinishListener = l;
    }

    /**
     * 播放
     */
    public void play() {
        if (!mediaPlayer.isPlaying()) {
            Exception exception = null;
            try {
                System.out.println("filePath==" + filePath);
                File file = new File(filePath);
                
                FileInputStream fis = new FileInputStream(file);
                mediaPlayer.setDataSource(fis.getFD());
               // mediaPlayer.setDataSource(path);
                
               // mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                
            } catch (IllegalArgumentException e) {
            	exception = e;
            } catch (SecurityException e) {
            	exception = e;
            } catch (IllegalStateException e) {
            	exception = e;
            } catch (IOException e) {
            	exception = e;
            }
            
            if (exception != null){
            	mediaPlayer.stop();
            	mediaPlayer.release();
            	System.out.println(exception.toString());
            }
        }
    }

    public void stop() {
    	try{    		
    		if (mediaPlayer.isPlaying()){
	        		mediaPlayer.stop();
	                mediaPlayer.reset();
	                
	           if (onFinishListener != null)
	               onFinishListener.onPlayStop(filePath, playId);
    		}  
    	}catch(IllegalStateException e){
    		e.printStackTrace();
    	}        
    }
    
    /**
     * 释放资源
     */
    public void release() {
    	try{
    		if (mediaPlayer.isPlaying()){
            	mediaPlayer.stop();
                mediaPlayer.release();
            } 
    	}
    	catch(Exception e){
    		mediaPlayer.stop();
    		mediaPlayer.release();
    	}                  
    }

    /**
     * 播放完成事件
     */
    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mediaPlayer.reset();
            if (onFinishListener != null)
                onFinishListener.onPlayFinish(filePath, playId);

        }
    };

    /**
     * 错误处理
     */
    private OnErrorListener onErrorListener = new OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (onFinishListener != null)
                onFinishListener.onPlayError(filePath, playId);

            mediaPlayer.reset();
            return false;
        }
    };

    /**
     * 播放完成事件监听器
     * 
     * @author orson
     * 
     */
    public static abstract class OnPlayFinishListener {
        public abstract void onPlayFinish(String filePathName, int id);

        public abstract void onPlayError(String filePathName, int id);

        public abstract void onPlayStop(String filePathName, int id);
    }
}