package cn.hollo.www.xmpp.message;

import cn.hollo.www.xmpp.IChatMessage;

/**
 * Created by orson on 14-12-26.
 */
public interface ObserverSendMessageError {
    public void onSendError(Exception exception, IChatMessage message);
}
