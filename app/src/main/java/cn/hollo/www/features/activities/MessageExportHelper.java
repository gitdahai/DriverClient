package cn.hollo.www.features.activities;

import android.content.Context;

import cn.hollo.www.UserInfo;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.content_provider.ModelChatMessage;
import cn.hollo.www.content_provider.ProviderChatMessage;
import cn.hollo.www.thread_pool.ThreadPool;
import cn.hollo.www.upyun.UploadData;
import cn.hollo.www.upyun.voice.UploadVoice;
import cn.hollo.www.xmpp.IChatMessage;
import cn.hollo.www.xmpp.XMPPManager;
import cn.hollo.www.xmpp.XMPPService;
import cn.hollo.www.xmpp.message.ObserverSendMessageError;

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
        modelChatMessage.roomId = roomId;
        modelChatMessage.messageType = IChatMessage.PLAIN_MESSAGE;
        modelChatMessage.nickname = userInfo.getUserName();
        modelChatMessage.speaker = userInfo.getUserId();
        modelChatMessage.messageId = System.currentTimeMillis();
        modelChatMessage.isIssue = true;
        modelChatMessage.isRead  = true;
        modelChatMessage.content = text;
        modelChatMessage.userJid = userInfo.getUserId();

        //投递到xmpp进行发送
        if (xmppManager != null){
            //设置消息的状态为“正在发送中”
            modelChatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFERING;
            modelChatMessage.inserToDatabase(context);
            //用新的线程开始发送
            ThreadPool pool = ThreadPool.getInstance();
            SendTask task = new SendTask(modelChatMessage, sendMessageListener);
            pool.addTask(task);
        }
        else{
            //设置该消息的类型为“失败”
            modelChatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFER_FAIL;
            modelChatMessage.inserToDatabase(context);
        }
    }

    /*********************************************
     * 输出语音消息
     * @param voicePathName : 语音保存的路径和名称
     */
    public void exportVoice(String voicePathName, int duration){
        ModelChatMessage modelChatMessage = new ModelChatMessage();
        modelChatMessage.roomId = roomId;
        modelChatMessage.messageType = IChatMessage.AUDIO_MESSAGE;
        modelChatMessage.nickname = userInfo.getUserName();
        modelChatMessage.speaker = userInfo.getUserId();
        modelChatMessage.messageId = System.currentTimeMillis();
        modelChatMessage.userJid = userInfo.getUserId();
        modelChatMessage.isIssue = true;
        modelChatMessage.isRead  = true;
        modelChatMessage.content = voicePathName;       //只有上传成功后，才会被修改
        modelChatMessage.duration = duration / 1000;    //把毫秒转换成秒
        modelChatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFERING;

        //首先存入数据库
        context.getContentResolver().insert(ProviderChatMessage.CONTENT_URI, modelChatMessage.getContentValues());

        //上传语音文件到upYun上
        UploadVoice uploadVoice = new UploadVoice(userInfo.getUserId(), voicePathName);
        uploadVoice.setAttachment(modelChatMessage);
        uploadVoice.setOnUploadFinishListener(uploadListener);
        uploadVoice.excuteRequest();
    }

    /*********************************************
     *
     * @param description
     * @param lat
     * @param lng
     */
    public void exportLocation(String description, double lat, double lng){
        ModelChatMessage modelChatMessage = new ModelChatMessage();
        modelChatMessage.description = description;
        modelChatMessage.latitude = lat;
        modelChatMessage.longitude = lng;
        modelChatMessage.roomId = roomId;
        modelChatMessage.messageType = IChatMessage.LOCATION_MESSAGE;
        modelChatMessage.nickname = userInfo.getUserName();
        modelChatMessage.speaker = userInfo.getUserId();
        modelChatMessage.messageId = System.currentTimeMillis();
        modelChatMessage.userJid = userInfo.getUserId();
        modelChatMessage.isIssue = true;
        modelChatMessage.isRead  = true;
        modelChatMessage.content = description;       //只有上传成功后，才会被修改
        modelChatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFERING;

        //首先存入数据库
        context.getContentResolver().insert(ProviderChatMessage.CONTENT_URI, modelChatMessage.getContentValues());

        //投递到xmpp进行发送（开启新的线程）
        ThreadPool pool = ThreadPool.getInstance();
        SendTask task = new SendTask(modelChatMessage, sendMessageListener);
        pool.addTask(task);
    }

    /********************************************
     * 语音上传事件监听器
     */
    private UploadData.OnUploadFinishListener uploadListener = new UploadData.OnUploadFinishListener(){
        public void onUploadFinish(int code, String resultString,  String uriName, Object attach) {
            ModelChatMessage modelChatMessage = (ModelChatMessage)attach;
            //(原来的是存在本地资源路径，修改为服务器上的资源路径)
            modelChatMessage.content = uriName;

            //上传upYun成功
            //再发给用户端
            if (code == 200 && xmppManager != null){
                //投递到xmpp进行发送（单独的线程）
                ThreadPool pool = ThreadPool.getInstance();
                SendTask task = new SendTask(modelChatMessage, sendMessageListener);
                pool.addTask(task);
            }
            //上传失败（或者oepnfire有问题，则表示发送失败）
            else{
                //更新发送的状态为失败
                modelChatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFER_FAIL;
                modelChatMessage.updateToDatabase(context);
            }
        }
    };

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
     * 当该方法被执行时，说明在发送消息的过成功，发生了异常，
     * 从而导致了发送失败，这里需要处理的是，设置消息的状态
     * 为“发送失败”，并且更新到数据库中
     */
    private ObserverSendMessageError sendMessageListener = new ObserverSendMessageError(){
        public void onSendError(Exception exception, IChatMessage message) {
            ModelChatMessage chatMessage = (ModelChatMessage)message;
            chatMessage.messageStatus = ModelChatMessage.STATUS_TRANSFER_FAIL;
            chatMessage.updateToDatabase(context);
        }
    };

    /*************************************************************
     * 发送线程
     */
    private class SendTask implements Runnable{
        private ModelChatMessage chatMessage;
        private ObserverSendMessageError sendMessageListener;

        /**======================================================
         *
         * @param chatMessage
         * @param sendMessageListener
         */
        private SendTask(ModelChatMessage chatMessage, ObserverSendMessageError sendMessageListener){
            this.chatMessage = chatMessage;
            this.sendMessageListener = sendMessageListener;
        }

        public void run() {
            xmppManager.sendMultiUserChat(chatMessage, sendMessageListener);
        }
    }
}
