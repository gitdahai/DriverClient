package cn.hollo.www.features.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.hollo.www.R;
import cn.hollo.www.features.FragmentBase;
import cn.hollo.www.voice.SpeechVoiceRecorder;

/**
 * Created by orson on 14-12-18.
 * 群组聊天页面
 */
public class FragmentGroupChat extends FragmentBase {
    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, null);
        ModeControler controler = new ModeControler(view);
        return view;
    }


    /*****************************************************************
     * 聊天信息输入模式控制者
     */
    private class ModeControler implements View.OnClickListener, View.OnTouchListener {
        private View recordingContainer;    //含有录音容器
        private View messageContainer;      //文本输入容器

        private ImageView messageModeBtn;   //文本模式切换
        private ImageView voiceModeBtn;     //语音模式切换
        private ImageView sendMessageBtn;   //发送文本消息
        private Button recordingBtn;        //录音按钮
        private EditText  messageInput;     //文本输入框

        private SpeechVoiceRecorder recorder;

        private ModeControler(View view){
            recordingContainer = view.findViewById(R.id.recordingContainer);
            messageContainer = view.findViewById(R.id.messageContainer);
            messageModeBtn = (ImageView)view.findViewById(R.id.messageModeBtn);
            voiceModeBtn = (ImageView)view.findViewById(R.id.voiceModeBtn);
            sendMessageBtn = (ImageView)view.findViewById(R.id.sendMessageBtn);
            recordingBtn = (Button)view.findViewById(R.id.recordingBtn);
            messageInput = (EditText)view.findViewById(R.id.messageInput);

            sendMessageBtn.setEnabled(false);
            messageInput.addTextChangedListener(watcher);
            recordingBtn.setOnClickListener(this);
            messageModeBtn.setOnClickListener(this);
            sendMessageBtn.setOnClickListener(this);
            recordingBtn.setOnTouchListener(this);
        }

        /**============================================
         * 文本框监视对象
         */
        private TextWatcher watcher = new TextWatcher(){
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                if (messageInput.length() > 0)
                    sendMessageBtn.setEnabled(true);
                else
                    sendMessageBtn.setEnabled(false);
            }
        };
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.messageModeBtn:   onMessageMode();    break;
                case R.id.voiceModeBtn:     onVoiceMode();      break;
                case R.id.sendMessageBtn:   onSendMessage();    break;
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            //开始录音
            if (action == MotionEvent.ACTION_DOWN)
                onStartRecord();

            //停止录音
            else if (action == MotionEvent.ACTION_UP)
                onStopRecord();

            return false;
        }

        /**============================================
         * 切换到文本输入模式
         */
        private void onMessageMode(){
            recordingContainer.setVisibility(View.GONE);
            messageContainer.setVisibility(View.VISIBLE);
        }

        /**============================================
         * 切换到语音模式
         */
        private void onVoiceMode(){
            recordingContainer.setVisibility(View.VISIBLE);
            messageContainer.setVisibility(View.GONE);
        }

        /**============================================
         * 发送文本信息
         */
        private void onSendMessage(){

        }

        /**============================================
         * 开始录音
         */
        private void onStartRecord(){
            recorder = new SpeechVoiceRecorder(getActivity());
            recorder.start();
        }

        /**============================================
         * 停止录音
         */
        private void onStopRecord(){
            //recorder.stop();
        }

    }
}
