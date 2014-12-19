package cn.hollo.www.voice;

import android.content.Context;
import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by orson on 14-12-19.
 * 语音录制类
 */
public class SpeechVoiceRecorder {
    private Context context;
    private MediaRecorder recorder;
    private OnRecorderListener listener;
    private int maxDuration = 60000;//最大录音时间为60秒

    private boolean isSuccess;      //录音是否成功的标志
    private String voicePathName;   //录音文件的保存路径
    private int duration;           //录音的时间长度

    public SpeechVoiceRecorder(Context context){
        this.context = context.getApplicationContext();
        //生成录音文件的新名称
        voicePathName = createCatchName();
        //构造新的录音对象
        recorder = new MediaRecorder();
        //设置音频来源为Mic
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置MediaRecorder的音频源为麦克风
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        // 设置MediaRecorder录制的音频格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //添加输出文件名称
        recorder.setOutputFile(voicePathName);
        //设置最大录音时间长度
        recorder.setMaxDuration(maxDuration);
        recorder.setAudioSamplingRate(8000);
        //设置通道数
        recorder.setAudioChannels(1);
        //设置录制的音频编码比特率
        //recorder.setAudioEncodingBitRate(16);
        //设置采样率
        //recorder.setAudioSamplingRate(44100);


        //添加监听器
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);
    }

    /****************************************************
     * 开始录音
     */
    public void start(){
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("======录音开启失败====== ");
        }
    }

    /****************************************************
     * 停止录音
     */
    public void stop(){
        recorder.stop();
        recorder.reset();
        recorder.release();
    }

    /***************************************************
     * 设置录音监听器
     * @param l
     */
    public void setOnRecorderListener(OnRecorderListener l){
        listener = l;
    }

    /***************************************************
     * 回调接口，当录音出现错误的时候调用
     */
    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener(){
        public void onError(MediaRecorder mr, int what, int extra) {
            System.out.println("===MediaRecorder　error====== " + what);
        }
    };

    /***************************************************
     * 回调接口，当录音出现错误的时候调用
     */
    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener(){
        public void onInfo(MediaRecorder mr, int what, int extra) {
            System.out.println("===MediaRecorder　error====== " + what);
            //当录音时间到达最大时间时，则自动发送该消息，并且会停止录音
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                System.out.println("========录音时间到达==============");
                stop();
            }
        }
    };

    /***************************************************
     * 获取录音文件的保存路径和文件名称
     * @return
     */
    private String createCatchName(){
        String shortName = System.currentTimeMillis() + ".amr";
        String catchDir = context.getCacheDir().getAbsolutePath();
        String fullName = catchDir + "/" + shortName;

        return fullName;
    }

    /****************************************************
     * 录音监听器
     */
    public interface OnRecorderListener{
        /**
         * 录音完成，通知事件监听器
         * @param isSuccess     : 录音是否成功
         * @param voicePathName : 录音保存的文件名称
         * @param duration      : 录音的时间长度
         */
        public void onRecorderFinish(boolean isSuccess, String voicePathName, int duration);
    }
}
