package cn.hollo.www.xmpp;

import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by orson on 14-11-13.
 * 消息过滤器
 */
public class MessageFilterManager {
    private static MessageFilterManager instance;
    private Map<String, OnMessageListener> listeners;

    private MessageFilterManager(){
        listeners = new HashMap<String, OnMessageListener>();
    }

    static MessageFilterManager getInstance(){
        if (instance == null)
            instance = new MessageFilterManager();

        return instance;
    }

    /**
     * 销毁所有资源
     */
    void destroy(){
        if (listeners != null)
            listeners.clear();

        listeners = null;
        instance  = null;
    }

    /**
     * 过滤信息
     * @param message
     */
    void filterMessage(Message message){
        System.out.println("====message====" + message.toXML());
    }

    /**
     * 添加消息过滤监听器
     * @param messageType
     * @param l
     */
    public void addMessageFilterListener(String messageType, OnMessageListener l){
        if (messageType != null && l != null)
            listeners.put(messageType, l);
    }

    /**
     * 移除消息过滤器
     * @param messageType
     */
    public void removeMessageFilterListener(String messageType){
        listeners.remove(messageType);
    }

    /********************************************************
     * 消息监听器
     */
    public interface OnMessageListener{
        public void onMessage(Message message);
    }
}
