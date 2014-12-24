package cn.hollo.www.features.activities;

import android.content.Context;

import cn.hollo.www.UserInfo;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.content_provider.ModelChatMessage;
import cn.hollo.www.content_provider.OpenHelperChatMessage;
import cn.hollo.www.content_provider.ProviderChatMessage;
import cn.hollo.www.upyun.UploadData;
import cn.hollo.www.upyun.voice.UploadVoice;
import cn.hollo.www.xmpp.IChatMessage;
import cn.hollo.www.xmpp.XMPPManager;
import cn.hollo.www.xmpp.XMPPService;
import cn.hollo.www.xmpp.message.MessageContent;

/**
 * Created by orson on 14-12-20.
 * 消息输出辅助类
 */
public class MessageExportHelper implements ServiceManager.OnXmppBinder {
    private Context context;
    private String roomId;
    private XMPPManager xmppManager;
    private UserInfo userInfo;
    /**********************************************
     * 构造方法中，需要拿到
     * @param context
     */
    public MessageExportHelper(Context context, String roomId){
        this.roomId = roomId;
        this.context = context.getApplicationContext();

        userInfo = UserInfo.getInstance(context);
        ServiceManager serviceManager = ServiceManager.getInstance(context);
        serviceManager.getXmppBinder(this);
    }

    /*********************************************
     * 输出文本消息
     * @param text    : 需要发送的文本内容
     */
    public void exportText(String text){
        //构造一个消息模型
        ModelChatMessage modelChatMessage = new ModelChatMessage();
        modelChatMessage.room_id = roomId;
        modelChatMessage.message_type = IChatMessage.PLAIN_MESSAGE;
        modelChatMessage.nickname = userInfo.getUserName();
        modelChatMessage.speeker = userInfo.getUserId();
        modelChatMessage.timestamp = System.currentTimeMillis();
        modelChatMessage.user_id = userInfo.getUserId();
        modelChatMessage.is_issue = true;
        modelChatMessage.is_read  = true;
        modelChatMessage.message_status = 0;
        modelChatMessage.content = text;

        //首先存入数据库
        context.getContentResolver().insert(ProviderChatMessage.CONTENT_URI, modelChatMessage.getContentValues());

        //投递到xmpp进行发送
        if (xmppManager != null){
            //通过消息的模型，生成一个可发送消息的
            xmppManager.sendMultiUserChat(modelChatMessage.createMessageContent(new ObserverSendListener()));
        }
    }

    /*********************************************
     * 输出语音消息
     * @param voicePathName : 语音保存的路径和名称
     */
    public void exportVoice(String voicePathName){
        //首先存入数据库
        ModelChatMessage modelChatMessage = new ModelChatMessage();
        modelChatMessage.room_id = roomId;
        modelChatMessage.message_type = IChatMessage.AUDIO_MESSAGE;
        modelChatMessage.nickname = userInfo.getUserName();
        modelChatMessage.speeker = userInfo.getUserId();
        modelChatMessage.timestamp = System.currentTimeMillis();
        modelChatMessage.user_id = userInfo.getUserId();
        modelChatMessage.is_issue = true;
        modelChatMessage.is_read  = true;
        modelChatMessage.message_status = 0;
        modelChatMessage.content = voicePathName;   //只有上传成功后，才会被修改

        //首先存入数据库
        context.getContentResolver().insert(ProviderChatMessage.CONTENT_URI, modelChatMessage.getContentValues());

        //上传语音文件到upYun上
        UploadVoice uploadVoice = new UploadVoice(userInfo.getUserId(), voicePathName);
        uploadVoice.setAttachment(modelChatMessage);
        uploadVoice.setOnUploadFinishListener(uploadListener);
        uploadVoice.excuteRequest();
    }

    /********************************************
     * 语音上传事件监听器
     */
    private UploadData.OnUploadFinishListener uploadListener = new UploadData.OnUploadFinishListener(){
        public void onUploadFinish(int code, String resultString,  String uriName, Object attach) {
            ModelChatMessage modelChatMessage = (ModelChatMessage)attach;

            //======================================================
            //上传upYun成功
            if (code == 200){
                //当资源上传成功时，则需要修改资源的内容
                //(原来的是存在本地资源路径，修改为服务器上的资源路径)
                modelChatMessage.content = uriName;
                //当上传成功后，更改状态为“成功”
                modelChatMessage.message_status = 1;
                //投递到xmpp进行发送
                if (xmppManager != null)
                    xmppManager.sendMultiUserChat(modelChatMessage.createMessageContent(new ObserverSendListener()));
            }
            //上传失败
            else{
                //更新发送的状态为失败
                modelChatMessage.message_status = 2;
            }

            //======================================================
            updateToDabase(modelChatMessage);
        }
    };

    /*********************************************
     * 跟新数据库中的数据
     * @param modelChatMessage
     */
    private void updateToDabase(ModelChatMessage modelChatMessage){
        if (context == null)
            return;

        //更新条件
        String where = OpenHelperChatMessage.ROOM_ID + "=? and " + OpenHelperChatMessage.TIME_STAMP + "=?";

        //更新条件是，用户的id,房间的id, 以及时间戳
        String[] selectionArgs = {
                modelChatMessage.room_id,
                "" + modelChatMessage.timestamp};

        //进行更新操作
        context.getContentResolver().update(ProviderChatMessage.CONTENT_URI,
                modelChatMessage.getContentValues(), where, selectionArgs);
    }

    /********************************************
     * 释放资源
     */
    public void release(){
        xmppManager = null;
    }

    @Override
    public void onBinder(XMPPService.XmppBinder binder) {
        xmppManager = binder.getXMPPManager();
    }

    /**************************************************************
     * 监听发送失败的状态
     */
    private class ObserverSendListener implements IChatMessage.ObserverSendMessageError{
        public void onSendError(Exception exception, IChatMessage message) {
            MessageContent content = (MessageContent)message;
            ModelChatMessage chatMessage = new  ModelChatMessage(content);
            chatMessage.is_issue = true;
            chatMessage.is_read  = true;
            chatMessage.message_status = 2;

            //更新数据库
            updateToDabase(chatMessage);
        }
    }
}
