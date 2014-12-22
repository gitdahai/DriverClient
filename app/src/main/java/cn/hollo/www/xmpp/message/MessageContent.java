package cn.hollo.www.xmpp.message;

/**
 * Created by orson on 14-12-20.
 * 消息内容体
 */
public abstract class MessageContent {
    public String gender;
    public String messageid;
    public String avatar;
    public String nickname;
    public String messageType;
    public String speaker;
    public String userId;
    public String body;
    public String timestamp;            //生成的时间
    public String GroupRoomType;
}
