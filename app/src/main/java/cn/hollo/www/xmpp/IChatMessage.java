package cn.hollo.www.xmpp;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by orson on 14-12-3.
 * 消息接口
 */
public interface IChatMessage {
     static final String ELEMENT_MESSAGE_PARAMS = "params";
     static final String NS_MESSAGE_PARAMS = "http://hollo.cn/xmpp/message/params";
     static final String LOCATION_MESSAGE = "LocationMessage";
     static final String IMAGE_MESSAGE = "ImageMessage";
     static final String EMOTION_MESSAGE = "EmotionMessage";
     static final String AUDIO_MESSAGE = "AudioMessage";
     static final String PLAIN_MESSAGE = "Message";
     static final String SEND_FROM_SPECIAL_USER = "Driver";

    /**
     *
     * @return
     */
    public String getUserId();

    /**
     * 接收者的jid
     * @return
     */
    public String  getRoomId();

    /**
     * 发送的消息体
     * @return
     */
    public Message getMessage();
}
