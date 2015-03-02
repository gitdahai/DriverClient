package cn.hollo.www.xmpp.message;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;

import cn.hollo.www.xmpp.IChatMessage;
import cn.hollo.www.xmpp.XMPPConstant;

/**
 * Created by orson on 14-12-26.
 * 消息的实体类，这里包含了所有类型消息的字段
 */
public class ChatContent implements IChatMessage {
    public int    gender;
    public int    duration;
    public double longitude;
    public double latitude;
    public String content;
    public String groupRoomType;
    public String nickname;
    public String speaker;
    public String avatar;
    public String messageType;
    public String description;          //位置信息的描述
    public String sendFromSpecialUser;
    public long     messageId;          //发送的消息才会有该id，用于查询条件(该数据由时间戳生成)

    public String roomId;               //该数据不传递
    public String userJid;              //该数据不传递

    @Override
    public String getJid() {
        return userJid;
    }

    /**************************************************
     * 返回房间的id
     * 该方法提供的数据，是给发送对象使用的，
     * 用于拿到该房间id对应的Muilchat
     * @return
     */
    public String getRoomId() {
        return roomId;
    }

    /**************************************************
     * 该方法是，组装发送数据包
     * @return
     */
    public Message getMessage() {
        Message message = new Message(roomId + "@conference." + XMPPConstant.OPENFIRE_DOMAIN, Message.Type.groupchat);
        DefaultPacketExtension messageParams = new DefaultPacketExtension(ELEMENT_MESSAGE_PARAMS, NS_MESSAGE_PARAMS);
        message.setBody(content);
        messageParams.setValue("sendFromSpecialUser", SEND_FROM_SPECIAL_USER);
        messageParams.setValue("gender", "" + gender);
        messageParams.setValue("duration", "" + duration);
        messageParams.setValue("longitude", "" + longitude);
        messageParams.setValue("latitude", "" + latitude);
        messageParams.setValue("messageId", "" + messageId);

        if (groupRoomType != null)
            messageParams.setValue("GroupRoomType", groupRoomType);

        if (nickname != null)
            messageParams.setValue("nickname", nickname);

        if (speaker != null)
            messageParams.setValue("speaker", speaker);

        if (avatar != null)
            messageParams.setValue("avatar", avatar);

        if (messageType != null)
            messageParams.setValue("messageType", messageType);

        if (description != null)
            messageParams.setValue("description", description);


        message.addExtension(messageParams);
        return message;
    }
}
