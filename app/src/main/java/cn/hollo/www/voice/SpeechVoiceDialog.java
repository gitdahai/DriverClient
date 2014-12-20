package cn.hollo.www.voice;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by orson on 14-12-20.
 * 录音时的对话框
 */
public class SpeechVoiceDialog {
    private Context context;
    private SpeechVoiceRecorder recorder;
    private SpeechVoiceRecorder.OnRecorderListener recorderListener;

    private ImageView    dialogView;
    private ClipDrawable clipDrawable;      //录音动画执行器

    /**************************************************
     *
     * @param context
     * @param dialogView
     */
    public SpeechVoiceDialog(Context context, ImageView dialogView){
        this.context = context.getApplicationContext();
        this.dialogView = dialogView;

        clipDrawable = (ClipDrawable)dialogView.getDrawable();
        recorder = new SpeechVoiceRecorder(context);
        VoiceHandle handle = new VoiceHandle();
        recorder.setOnRecorderListener(handle);
        recorder.setOnMaxAmplitudeListener(handle);

        //清空以前的容器
        dialogView.setVisibility(View.GONE);
    }

    /***************************************************
     * 设置录音监听器
     * @param l
     */
    public void setOnRecorderListener(SpeechVoiceRecorder.OnRecorderListener l){
        recorderListener = l;
    }

    /***************************************************
     * 开始录音
     */
    public void startVoiceRecoder(){
        dialogView.setVisibility(View.VISIBLE);
        recorder.start();
    }

    /**************************************************
     * 停止录音
     */
    public void stopVoiceRecoder(){
        recorder.stop();
    }


    /****************************************************
     * 处理录音动画效果和结果
     */
    private class VoiceHandle implements SpeechVoiceRecorder.OnRecorderListener, SpeechVoiceRecorder.OnMaxAmplitudeListener {
        private int iLevel;

        /**==============================================
         *
         * @param isSuccess     : 录音是否成功
         * @param voicePathName : 录音保存的文件名称
         * @param duration      : 录音的时间长度
         */
        public void onRecorderFinish(boolean isSuccess, String voicePathName, int duration) {
            dialogView.setVisibility(View.GONE);

            if (recorderListener != null)
                recorderListener.onRecorderFinish(isSuccess, voicePathName, duration);
        }

        /**==============================================
         *
         * @param amplitude
         */
        public void onMaxAmplitude(double amplitude) {
            iLevel = (int)(10000 * (amplitude / 100));
            clipDrawable.setLevel(iLevel);
        }
    }
}
