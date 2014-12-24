package cn.hollo.www.features.operation;

import org.jivesoftware.smack.packet.Message;
import cn.hollo.www.xmpp.IChatMessage;

/**
 * Created by orson on 14-12-3.
 * 站点消息通知类
 */
public class StationMessage implements IChatMessage {
    private static final String messageType = "Notification";
    public String describe;       //站点的描述
    public String contract_id;    //合约编号
    public String vehicle_code;   //车辆编号
    public String to;             //应该是用户的id
    public ObserverSendMessageError observer;

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getRoomId() {
        return to;
    }

    @Override
    public Message getMessage() {
        Message message = new Message();
        message.addBody("describe", describe);
        // message.addBody("contract_id", contract_id);
        message.addBody("vehicle_code", vehicle_code);
        message.addBody("messageType", messageType);

        return message;
    }

    @Override
    public ObserverSendMessageError getOnSendMessageListener() {
        return observer;
    }
}
