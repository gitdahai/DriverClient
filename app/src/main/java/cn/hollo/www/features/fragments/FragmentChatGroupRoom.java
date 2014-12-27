package cn.hollo.www.features.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import cn.hollo.www.R;
import cn.hollo.www.content_provider.ModelChatMessage;
import cn.hollo.www.content_provider.OpenHelperChatMessage;
import cn.hollo.www.content_provider.ProviderChatMessage;
import cn.hollo.www.features.FragmentBase;
import cn.hollo.www.features.activities.ActivityLocationMap;
import cn.hollo.www.features.activities.MessageExportHelper;
import cn.hollo.www.features.adapters.AdapterChatCursor;
import cn.hollo.www.features.informations.MissionInfo;
import cn.hollo.www.utils.Util;
import cn.hollo.www.voice.SpeechVoiceDialog;
import cn.hollo.www.voice.SpeechVoiceRecorder;

/**
 * Created by orson on 14-12-18.
 * 群组聊天页面
 */
public class FragmentChatGroupRoom extends FragmentBase {
    private MessageExportHelper exportHelper;
    private ModeControler controler;
    private ChatContentDisplay  chatDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (controler != null)
            controler.ononActivityResult(requestCode, resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    /***************************************************************
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, null);

        Bundle mBundle = getArguments();
        //取得参数
        if (mBundle != null){
            MissionInfo missionInfo =  (MissionInfo)mBundle.getSerializable("MissionInfo");

            if (missionInfo != null){
                exportHelper = new MessageExportHelper(getActivity(), missionInfo.room_id);
                controler = new ModeControler(view);
                chatDisplay = new ChatContentDisplay(view, missionInfo.room_id);
            }
        }

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        chatDisplay.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exportHelper.release();
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
        private ImageView locationBtn;      //获取位置信息的按钮

        private ImageView         voiceDialogView;
        private SpeechVoiceDialog voiceDialog;

        private ModeControler(View view){
            recordingContainer = view.findViewById(R.id.recordingContainer);
            messageContainer = view.findViewById(R.id.messageContainer);
            voiceDialogView = (ImageView)view.findViewById(R.id.voiceSpeekView);
            messageModeBtn = (ImageView)view.findViewById(R.id.messageModeBtn);
            voiceModeBtn = (ImageView)view.findViewById(R.id.voiceModeBtn);
            sendMessageBtn = (ImageView)view.findViewById(R.id.sendMessageBtn);
            recordingBtn = (Button)view.findViewById(R.id.recordingBtn);
            messageInput = (EditText)view.findViewById(R.id.messageInput);
            locationBtn = (ImageView)view.findViewById(R.id.locationBtn);

            sendMessageBtn.setEnabled(false);
            //文本消息监听
            messageInput.addTextChangedListener(watcher);
            //按住录音事件
            recordingBtn.setOnTouchListener(this);
            //切换到语音模式事件
            voiceModeBtn.setOnClickListener(this);
            //切换到文本发送模式事件
            messageModeBtn.setOnClickListener(this);
            //发送文本消息事件
            sendMessageBtn.setOnClickListener(this);
            //设置“获取”位置信息事件
            locationBtn.setOnClickListener(this);
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
                case R.id.locationBtn:      onGetLoaction();    break;
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
            String text = messageInput.getText().toString();
            exportHelper.exportText(text);
        }

        /**============================================
         * 获取位置信息
         */
        private void onGetLoaction(){
            Intent intent = new Intent(getActivity(), ActivityLocationMap.class);
            intent.putExtra("Type", ActivityLocationMap.TYPE_GET_LOCATION);
            startActivityForResult(intent, 1);
        }

        /**============================================
         * 录音结果返回
         */
        private SpeechVoiceRecorder.OnRecorderListener voiceListener = new SpeechVoiceRecorder.OnRecorderListener(){
            public void onRecorderFinish(boolean isSuccess, String voicePathName, int duration) {
                if (isSuccess){
                    if (duration <= 1000){
                        Util.showMsg(getActivity(), "语音太短！");
                        return;
                    }

                    //输出语音
                    exportHelper.exportVoice(voicePathName, duration);
                }
            }
        };

        /**============================================
         * 开始录音
         */
        private void onStartRecord(){
            voiceDialog = new SpeechVoiceDialog(getActivity(), voiceDialogView);
            voiceDialog.setOnRecorderListener(voiceListener);
            voiceDialog.startVoiceRecoder();
        }

        /**============================================
         * 停止录音
         */
        private void onStopRecord(){
            voiceDialog.stopVoiceRecoder();
        }

        /**============================================
         * 获取位置信息返回
         * @param requestCode
         * @param resultCode
         * @param data
         */
        private void ononActivityResult(int requestCode, int resultCode, Intent data){
            //如果成功获取到位置信息，则发送该位置
            if (resultCode == Activity.RESULT_OK && exportHelper != null){
                String description = data.getStringExtra("Description");
                double lat = data.getDoubleExtra("Lat", 0);
                double lng = data.getDoubleExtra("Lng", 0);
                exportHelper.exportLocation(description, lat, lng);
            }
        }
    }

    /****************************************************************
    / * 显示聊天对话的消息
    / * 加载语音，图片等资源
    / ***************************************************************/
    private class ChatContentDisplay{
        private ListView chatListView;
        private String roomId;
        private AdapterChatCursor adapter;

        /**==============================================
         *
         * @param view
         * @param roomId
         */
        private ChatContentDisplay(View view, String roomId){
            this.roomId  = roomId;
            chatListView = (ListView)view.findViewById(R.id.chatListView);
            chatListView.setOnScrollListener(scrollListener);

            Cursor cursor = getCursor();

            if (cursor != null){
                adapter = new AdapterChatCursor(getActivity(), cursor);
                chatListView.setAdapter(adapter);
                adapter.setOserverUpdateCursor(observer);
            }
        }

        /**=============================================
         * 列表滚动事件监听器
         */
        private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener(){
            /**-----------------------------------------
             *
             * @param view
             * @param scrollState
             */
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            /**-----------------------------------------
             *
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //如果列表项已经在最后一条，则实现自动添加和滚动
                if(visibleItemCount + firstVisibleItem == totalItemCount){
                    chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
                //否则关闭自动滚动
                else
                    chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
            }
        };

        /**=============================================
         * 添加观察者对象
         */
        private AdapterChatCursor.OserverUpdateCursor observer = new AdapterChatCursor.OserverUpdateCursor(){
            public void onUpdateCursor(ModelChatMessage chatMessage) {
                String where =  OpenHelperChatMessage.ROOM_ID + "=? and " + OpenHelperChatMessage.MESSAGE_ID + "=?";
                String[] selectionArgs = {roomId, "" + chatMessage.messageId};
                getActivity().getContentResolver().update(ProviderChatMessage.CONTENT_URI, chatMessage.getContentValues(), where, selectionArgs);
            }
        };

        /**=============================================
         * 获取Cursor对象
         * @return
         */
        private Cursor getCursor(){
            if (roomId == null)
                return null;

            Cursor cursor = null;

            String selection =  OpenHelperChatMessage.ROOM_ID + "=?";
            String[] selectionArgs = {roomId};
            cursor = getActivity().getContentResolver().query(ProviderChatMessage.CONTENT_URI, null, selection, selectionArgs, null);
            return cursor;
        }

        /**=============================================
         * 停止
         */
        void onStop(){
            if (adapter != null)
                adapter.onStoped();
        }
    }
}
