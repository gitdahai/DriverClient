package cn.hollo.www.voice;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;

import java.io.IOException;

/**
 * Created by orson on 14-12-19.
 * 语音录制类
 */
public class SpeechVoiceRecorder {
    private Context context;
    private MediaRecorder recorder;
    private OnRecorderListener recorderlistener;        //录音结果监听器
    private OnMaxAmplitudeListener amplitudeListener;   //振幅监听器
    private AmplitudeRunnable runnable;                 //获取振幅的执行器对象

    private int maxDuration = 60000;                    //最大录音时间为60秒
    private int amplitudeInterval = 100;                //返回录音振幅的间隔时间１００毫秒

    private Handler mHandler;
    private String voicePathName;   //录音文件的保存路径
    private int duration;           //录音的时间长度

    /****************************************************
     *
     * @param context
     */
    public SpeechVoiceRecorder(Context context){
        this.context = context.getApplicationContext();
        //生成录音文件的新名称
        voicePathName = createCatchName();
        //构造新的录音对象
        recorder = new MediaRecorder();
        //设置音频来源为Mic
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置MediaRecorder的音频源为麦克风
        //音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        // 设置MediaRecorder录制的音频格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //添加输出文件名称
        recorder.setOutputFile(voicePathName);
        //设置最大录音时间长度
        recorder.setMaxDuration(maxDuration);
        //设置采样率
        //设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
        recorder.setAudioSamplingRate(8000);
        //设置通道数
        //设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
        recorder.setAudioChannels(1);
        //设置录制的音频编码比特率
        //recorder.setAudioEncodingBitRate(16);
        //设置采样率
        //recorder.setAudioSamplingRate(44100);

        //添加监听器
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        runnable = new AmplitudeRunnable();
        mHandler = new Handler();
    }

    /****************************************************
     * 开始录音
     */
    public void start(){
        Exception exception = null;

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            exception = e;
        } catch (Exception e){
            e.printStackTrace();
            exception = e;
        } finally {
            //如果开启录音时，发生异常，则直接返回
            if (exception != null){
                if (recorderlistener != null)
                    recorderlistener.onRecorderFinish(false, null, 0);

                //停止，并且释放资源
                recorder.release();
            }
            else
                startingAmplitude();
        }
    }

    /****************************************************
     * 停止录音
     */
    public void stop(){
        stoppingAmplitude();
        recorder.stop();
        recorder.reset();
        recorder.release();

        //通知录音的结果返回
        if (recorderlistener != null)
            recorderlistener.onRecorderFinish(true, voicePathName, duration);
    }

    /***************************************************
     * 设置录音监听器
     * @param l
     */
    public void setOnRecorderListener(OnRecorderListener l){
        recorderlistener = l;
    }

    /***************************************************
     * 添加录音最大振幅监听器
     * @param l
     */
    public void setOnMaxAmplitudeListener(OnMaxAmplitudeListener l){
        amplitudeListener = l;
    }

    /**************************************************
     * 启用获取振幅功能
     */
    private void startingAmplitude(){
        //如果用户设置了振幅监听器对象，则启动线程
        if (amplitudeListener != null)
            mHandler.post(runnable);
    }

    /**************************************************
     * 关闭振幅获取功能
     */
    private void stoppingAmplitude(){
        mHandler.removeCallbacks(runnable);
    }

    /***************************************************
     * 回调接口，当录音出现错误的时候调用
     */
    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener(){
        public void onError(MediaRecorder mr, int what, int extra) {
            System.out.println("===MediaRecorder　error====== " + what);
            //如果录音过程中发生错误，则不清空所有的资源
            if (recorderlistener != null)
                recorderlistener.onRecorderFinish(false, null, 0);

            stoppingAmplitude();
            //释放资源
            recorder.stop();
            recorder.release();
        }
    };

    /***************************************************
     * 回调接
     */
    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener(){
        public void onInfo(MediaRecorder mr, int what, int extra) {

            //当录音时间到达最大时间时，则自动发送该消息，并且会停止录音
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                duration = maxDuration;
            }
            else if (what == MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN){}

            //当录音达到设定的最大文件限制的时候，则会回调该方法
            else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED){}

            //返回录音的结果
            if (recorderlistener != null)
                recorderlistener.onRecorderFinish(true, voicePathName, duration);

            stoppingAmplitude();
            //释放资源
            recorder.stop();
            recorder.release();
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

    /***************************************************
     * 返回设置定时间间隔的录音振幅
     */
    public interface OnMaxAmplitudeListener{
        public void onMaxAmplitude(double amplitude);
    }

    /***************************************************
     * 线程执行器
     */
    private class AmplitudeRunnable implements Runnable{
        private double dB;
        private int   amp;

        public void run() {
            amp = recorder.getMaxAmplitude();
            dB = (int)(10 * Math.log10(amp));

            if (dB >= 0 && amplitudeListener != null)
                    amplitudeListener.onMaxAmplitude(dB);

            //进行下一次延迟执行
            mHandler.postDelayed(this, amplitudeInterval);
            //累加录音时间
            duration += amplitudeInterval;
        }
    }
}
