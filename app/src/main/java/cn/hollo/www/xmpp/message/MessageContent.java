package cn.hollo.www.xmpp.message;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;

import cn.hollo.www.xmpp.IChatMessage;
import cn.hollo.www.xmpp.XMPPConstant;

/**
 * Created by orson on 14-12-20.
 * 消息内容体
 */
public class MessageContent implements IChatMessage {
    public String roomId;
    public int gender;
    public String messageid;
    public String avatar;
    public String nickname;
    public String messageType;
    public String speaker;
    public String userId;
    public String body;
    public long timestamp;            //生成的时间
    public String GroupRoomType;

    /************************************************
     *
     * @return
     */
    public String getRoomId() {
        return roomId;
    }

    /************************************************
     *
     * @return
     */
    public Message getMessage() {
        Message message = new Message(roomId + "@" + XMPPConstant.OPENFIRE_DOMAIN, Message.Type.groupchat);
        DefaultPacketExtension messageParams = new DefaultPacketExtension(ELEMENT_MESSAGE_PARAMS, NS_MESSAGE_PARAMS);
        messageParams.setValue("sendFromSpecialUser", SEND_FROM_SPECIAL_USER);
        messageParams.setValue("messageType", messageType);
        messageParams.setValue("speaker", speaker);
        messageParams.setValue("timestamp", "" + timestamp);
        messageParams.setValue("userId", userId);
        messageParams.setValue("gender", "" + gender);
        message.addExtension(messageParams);
        message.setBody(body);
        return message;
    }
}
