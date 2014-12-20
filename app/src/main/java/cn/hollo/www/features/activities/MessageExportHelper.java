package cn.hollo.www.features.activities;

import android.content.Context;

import cn.hollo.www.UserInfo;
import cn.hollo.www.xmpp.message.MessageText;

/**
 * Created by orson on 14-12-20.
 * 消息输出辅助类
 */
public class MessageExportHelper {
    
    /*********************************************
     * 输出文本消息
     * @param context
     * @param text    : 需要发送的文本内容
     */
    public static void exportText(Context context, String roomId, String text){
        UserInfo userInfo = UserInfo.getInstance(context);

        MessageText message = new MessageText();
        message.to = roomId;
        message.body = text;
        message.nickname = userInfo.getUserName();
        message.speaker = userInfo.getUserId();
        message.messageType = "message";
        message.timestamp = "" + System.currentTimeMillis();
        message.userId = userInfo.getUserId();
    }

    /*********************************************
     * 输出语音消息
     * @param context
     * @param voicePathName : 语音保存的路径和名称
     */
    public static void exportVoice(Context context, String roomId, String voicePathName){
        UserInfo userInfo = UserInfo.getInstance(context);

        MessageText message = new MessageText();
        message.to = roomId;
        message.body = voicePathName;
        message.nickname = userInfo.getUserName();
        message.speaker = userInfo.getUserId();
        message.messageType = "message";
        message.timestamp = "" + System.currentTimeMillis();
        message.userId = userInfo.getUserId();
    }
}
