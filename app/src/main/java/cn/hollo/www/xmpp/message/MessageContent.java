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
    public String sendFromSpecialUser;

    /***********************************************
     *
     * @return
     */
    public String getUserId(){
        return userId;
    }

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
        Message message = new Message(roomId + "@conference." + XMPPConstant.OPENFIRE_DOMAIN, Message.Type.groupchat);
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

    /*************************************************
     *
     * @param message
     * @return
     */
    public static MessageContent newMessageContent(Message message){
        MessageContent content = new MessageContent();
        DefaultPacketExtension messageParams = message.getExtension(ELEMENT_MESSAGE_PARAMS, NS_MESSAGE_PARAMS);
        content.body = message.getBody();
        content.speaker = messageParams.getValue("speaker");
        content.userId = messageParams.getValue("userId");
        content.sendFromSpecialUser = messageParams.getValue("sendFromSpecialUser");
        content.messageType = messageParams.getValue("messageType");
        content.nickname = messageParams.getValue("nickname");
        content.avatar = messageParams.getValue("avatar");

        //取得性别
        try{
            content.gender = Integer.parseInt(messageParams.getValue("gender"));
        }
        catch(NumberFormatException e){
            content.gender = 1;
        }

        //取得时间戳（只有司机发送的消息才有该值）
        try{
            content.timestamp = Long.parseLong(messageParams.getValue("timestamp"));
        }
        catch(NumberFormatException e){
            content.timestamp = 1;
        }

        //抽取roomId
        content.roomId = extractRoomId(message.getFrom());

        return content;
    }

    /**************************************************
     * 抽取roomId
     * @param from
     * @return
     */
    private static String extractRoomId(String from){
        int index = from.indexOf('@');
        String result = from.substring(0, index);
        return result;
    }
}
