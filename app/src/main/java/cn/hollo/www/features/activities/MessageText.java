package cn.hollo.www.features.activities;

import org.jivesoftware.smack.packet.Message;

import cn.hollo.www.xmpp.IChatMessage;

/**
 * Created by orson on 14-12-20.
 * 文本消息体
 */
public class MessageText implements IChatMessage {
    @Override
    public String getTo() {
        return null;
    }

    @Override
    public Message getMessage() {
        return null;
    }
}
