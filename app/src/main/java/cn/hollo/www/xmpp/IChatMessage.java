package cn.hollo.www.xmpp;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by orson on 14-12-3.
 * 消息接口
 */
public interface IChatMessage {
    /**
     * 接收者的jid
     * @return
     */
    public String  getTo();

    /**
     * 发送的消息体
     * @return
     */
    public Message getMessage();
}
