package cn.hollo.www.xmpp.message;

import org.jivesoftware.smack.packet.Message;

import cn.hollo.www.xmpp.IChatMessage;

/**
 * Created by orson on 14-12-20.
 * 文本消息体
 */
public class MessageText extends MessageContent implements IChatMessage {
    public String to;

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public Message getMessage() {
        Message message = new Message();
        message.addBody("body", body);
        message.addSubject("nickname", nickname);
        message.addSubject("messageType", messageType);
        message.addSubject("speaker", speaker);
        message.addSubject("timestamp", timestamp);
        message.addSubject("sendFromSpecialUser", sendFromSpecialUser);
        message.addSubject("userId", userId);
        return message;
    }
}
