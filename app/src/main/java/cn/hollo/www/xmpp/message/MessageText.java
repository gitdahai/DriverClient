package cn.hollo.www.xmpp.message;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;

import cn.hollo.www.xmpp.IChatMessage;
import cn.hollo.www.xmpp.XMPPConstant;

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
        Message message = new Message(to + "@" + XMPPConstant.OPENFIRE_DOMAIN, Message.Type.groupchat);
        DefaultPacketExtension messageParams = new DefaultPacketExtension(ELEMENT_MESSAGE_PARAMS, NS_MESSAGE_PARAMS);
        messageParams.setValue("messageType", PLAIN_MESSAGE);
        messageParams.setValue("speaker", speaker);
        messageParams.setValue("timestamp", timestamp);
        messageParams.setValue("sendFromSpecialUser", sendFromSpecialUser);
        messageParams.setValue("userId", userId);
        message.addExtension(messageParams);
        message.setBody(body);
        return message;
    }
}
