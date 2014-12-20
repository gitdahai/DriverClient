package cn.hollo.www.voice;

import android.media.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by orson on 14-12-20.
 * 语音播放类
 */
public class SpeechVoicePlay {
    private MediaPlayer mediaPlayer;
    private OnVoicePlayListener voicePlayListener;

    private String voicePathName;   //播放文件的完整路径及名称
    private int duration;           //资源文件的最大播放时间长度
    private int position;           //当前播放的位置

    /***************************************************
     *
     * @param voicePathName : 播放文件的路径
     */
    public SpeechVoicePlay(String voicePathName){
        this.voicePathName = voicePathName;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(completionListener);
        mediaPlayer.setOnInfoListener(infoListener);
        mediaPlayer.setOnErrorListener(errorListener);
    }

    /**************************************************
     * 设置播放监听器
     * @param l
     */
    public void setOnVoicePlayListener(OnVoicePlayListener l){
        voicePlayListener = l;
    }

    /**************************************************
     * 播放
     */
    public void play() {
        if (!mediaPlayer.isPlaying()) {
            Exception exception = null;

            try {
                File file = new File(voicePathName);
                FileInputStream fis = new FileInputStream(file);
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
                mediaPlayer.start();
                duration = mediaPlayer.getDuration();

            } catch (IllegalArgumentException e) {
                exception = e;
            } catch (SecurityException e) {
                exception = e;
            } catch (IllegalStateException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }finally {
                if (exception != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    //通知用户播放的结果
                    if (voicePlayListener != null)
                        voicePlayListener.onVoicePlayFinish(false, voicePathName, position, duration);
                }
            }
        }
    }

    /**************************************************
     * 停止播放
     */
    public void stop() {
        try{
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                position = mediaPlayer.getCurrentPosition();
                //返回到前台
                if (voicePlayListener != null)
                    voicePlayListener.onVoicePlayFinish(true, voicePathName, position, duration);
            }
        }catch(IllegalStateException e){
            e.printStackTrace();
        }
    }

    /*************************************************
     * 播放完成监听器
     */
    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener(){
        public void onCompletion(MediaPlayer mp) {
            position = mediaPlayer.getCurrentPosition();

            //如果发生错误，则释放资源
            mediaPlayer.reset();
            mediaPlayer.release();

            //返回到前台
            if (voicePlayListener != null)
                voicePlayListener.onVoicePlayFinish(true, voicePathName, position, duration);
        }
    };

    /*************************************************
     * 播放信息监听器
     */
    private MediaPlayer.OnInfoListener infoListener = new MediaPlayer.OnInfoListener(){
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (MediaPlayer.MEDIA_INFO_UNKNOWN == what){}
            else if (MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING == what){}
            else if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what){}
            else if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what){}
            else if (MediaPlayer.MEDIA_INFO_BUFFERING_END == what){}
            else if (MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING == what){}
            else if (MediaPlayer.MEDIA_INFO_NOT_SEEKABLE == what){}
            else if (MediaPlayer.MEDIA_INFO_METADATA_UPDATE == what){}
            else if (MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE == what){}
            else if (MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT == what){}

            return false;
        }
    };

    /************************************************
     * 播放错误监听器
     */
    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener(){
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (MediaPlayer.MEDIA_ERROR_UNKNOWN == what){}
            else if (MediaPlayer.MEDIA_ERROR_SERVER_DIED == what){ }

            //如果发生错误，则释放资源
            mediaPlayer.reset();
            mediaPlayer.release();
            position = mediaPlayer.getCurrentPosition();

            //返回到前台
            if (voicePlayListener != null)
                voicePlayListener.onVoicePlayFinish(false, voicePathName, position, duration);

            return true;
        }
    };
    /*************************************************
     * 播放监听器
     */
    public interface OnVoicePlayListener{
        /**
         *
         * @param isSuccess     : 是否播放成功
         * @param voicePathName : 播放文件的路径及名称
         * @param position      : 当前已经播放的位置
         * @param duration      : 该资源文件时间长度
         */
        public void onVoicePlayFinish(boolean isSuccess, String voicePathName, int position, int duration);
    }
}
