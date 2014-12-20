package cn.hollo.www.features.activities;

import android.content.Context;

import cn.hollo.www.UserInfo;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.xmpp.XMPPManager;
import cn.hollo.www.xmpp.XMPPService;
import cn.hollo.www.xmpp.message.MessageText;

/**
 * Created by orson on 14-12-20.
 * 消息输出辅助类
 */
public class MessageExportHelper implements ServiceManager.OnXmppBinder {
    private Context context;
    private String roomId;
    private XMPPManager xmppManager;
    /**********************************************
     * 构造方法中，需要拿到
     * @param context
     */
    public MessageExportHelper(Context context, String roomId){
        this.context = context.getApplicationContext();
        this.roomId = roomId;

        ServiceManager serviceManager = ServiceManager.getInstance(context);
        serviceManager.getXmppBinder(this);
    }

    /*********************************************
     * 输出文本消息
     * @param text    : 需要发送的文本内容
     */
    public void exportText(String text){
        UserInfo userInfo = UserInfo.getInstance(context);

        MessageText message = new MessageText();
        message.to = roomId;
        message.body = text;
        message.nickname = userInfo.getUserName();
        message.speaker = userInfo.getUserId();
        message.messageType = "message";
        message.timestamp = "" + System.currentTimeMillis();
        message.userId = userInfo.getUserId();

        if (xmppManager != null)
            xmppManager.sendMultiUserChat(message);
    }

    /*********************************************
     * 输出语音消息
     * @param voicePathName : 语音保存的路径和名称
     */
    public void exportVoice(String voicePathName){
        UserInfo userInfo = UserInfo.getInstance(context);

        MessageText message = new MessageText();
        message.to = roomId;
        message.body = voicePathName;
        message.nickname = userInfo.getUserName();
        message.speaker = userInfo.getUserId();
        message.messageType = "message";
        message.timestamp = "" + System.currentTimeMillis();
        message.userId = userInfo.getUserId();

        if (xmppManager != null)
            xmppManager.sendMultiUserChat(message);
    }

    /********************************************
     * 释放资源
     */
    public void release(){
        xmppManager = null;
        context = null;
    }

    @Override
    public void onBinder(XMPPService.XmppBinder binder) {
        xmppManager = binder.getXMPPManager();
    }
}
