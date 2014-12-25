package cn.hollo.www.content_provider;

import android.content.ContentValues;
import android.database.Cursor;

import cn.hollo.www.xmpp.IChatMessage;
import cn.hollo.www.xmpp.message.MessageContent;

/**
 * Created by orson on 14-12-23.
 * 聊天信息模型类
 */
public class ModelChatMessage {
    public int  _id;
    public int  gender = 1;             //性别
    public long timestamp;              //时间戳（产生或者接收时的时间）
    public String user_id;              //
    public String room_id;              //房间的id
    public String speeker;              //发送者的id
    public String nickname;             //昵称
    public String avatar;               //头像
    public String content;              //消息的实体
    public String message_type;         //消息的类型

    //本地附加的数据
    public boolean  is_read;            //消息的状态:消息的状态（true=已读，false=未读）
    public boolean  is_issue;           //消息:消息是自己发送的，还是接收的(true=发送，false=接收的)
    public int  message_status;         //消息的发送或者接收状态:消息的发送／接收状态（0=接收/上传中，
                                        // 1=接收/发送成功，
                                        // 2=接收/发送失败,
                                        // 3=未接收/上传
                                        // 4=播放中(语音播放中)）

    public ModelChatMessage(){}
    /**************************************************
     *
     * @param cursor
     */
    public ModelChatMessage(Cursor cursor){
        readDataFromCursor(cursor);
    }

    /**************************************************
     *
     * @param message
     */
    public ModelChatMessage(MessageContent message){
        setDataFromMessageContent(message);
    }

    /**************************************************
     *
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(OpenHelperChatMessage.USER_ID, user_id);
        values.put(OpenHelperChatMessage.ROOM_ID, room_id);
        values.put(OpenHelperChatMessage.SPEEKER, speeker);
        values.put(OpenHelperChatMessage.GENDER, gender);
        values.put(OpenHelperChatMessage.NICKNAME, nickname);
        values.put(OpenHelperChatMessage.AVATAR, avatar);
        values.put(OpenHelperChatMessage.CONTENT, content);
        values.put(OpenHelperChatMessage.MESSAGE_TYPE, message_type);
        values.put(OpenHelperChatMessage.TIME_STAMP, timestamp);
        values.put(OpenHelperChatMessage.MESSAGE_STATUS, message_status);
        values.put(OpenHelperChatMessage.IS_READ, is_read);
        values.put(OpenHelperChatMessage.IS_ISSUE, is_issue);
        return values;
    }

    /***************************************************
     * 从集合中读取数据
     * @param cursor
     */
    public void readDataFromCursor(Cursor cursor){
        _id = cursor.getInt(OpenHelperChatMessage.INDEX_ID);
        user_id = cursor.getString(OpenHelperChatMessage.INDEX_USER_ID);
        room_id = cursor.getString(OpenHelperChatMessage.INDEX_ROOM_ID);
        speeker = cursor.getString(OpenHelperChatMessage.INDEX_SPEEKER);
        gender  = cursor.getInt(OpenHelperChatMessage.INDEX_GENDER);
        nickname = cursor.getString(OpenHelperChatMessage.INDEX_NICKNAME);
        avatar  = cursor.getString(OpenHelperChatMessage.INDEX_AVATAR);
        content = cursor.getString(OpenHelperChatMessage.INDEX_CONTENT);
        message_type = cursor.getString(OpenHelperChatMessage.INDEX_MESSAGE_TYPE);
        timestamp = cursor.getLong(OpenHelperChatMessage.INDEX_TIME_STAMP);
        message_status = cursor.getInt(OpenHelperChatMessage.INDEX_MESSAGE_STATUS);
        is_read = (cursor.getInt(OpenHelperChatMessage.INDEX_IS_READ) == 0 ? false : true);
        is_issue = (cursor.getInt(OpenHelperChatMessage.INDEX_IS_ISSUE) == 0 ? false : true);
    }

    /***************************************************
     *
     * @param message
     */
    public void setDataFromMessageContent(MessageContent message){
        this.user_id = message.userId;
        this.room_id = message.roomId;
        this.speeker = message.speaker;
        this.nickname = message.nickname;
        this.avatar = message.avatar;
        this.content = message.body;
        this.message_type = message.messageType;
        this.timestamp = message.timestamp;
        this.gender = message.gender;
    }

    /**************************************************
     *
     * @return
     */
    public MessageContent createMessageContent(IChatMessage.ObserverSendMessageError observer){
        MessageContent message = new MessageContent();
        message.roomId = room_id;
        message.userId = user_id;
        message.speaker = speeker;
        message.nickname = nickname;
        message.avatar = avatar;
        message.body = content;
        message.messageType = message_type;
        message.timestamp = timestamp;
        message.gender = gender;
        message.observer = observer;
        return message;
    };

    /**************************************************
     * 输出信息
     */
    public void print(){
        System.out.println("==============================");
        System.out.println("gender=" + gender);
        System.out.println("timestamp=" + timestamp);
        System.out.println("user_id=" + user_id);
        System.out.println("room_id=" + room_id);
        System.out.println("speeker=" + speeker);
        System.out.println("nickname=" + nickname);
        System.out.println("avatar=" + avatar);
        System.out.println("content=" + content);
        System.out.println("message_type=" + message_type);

        System.out.println("is_read=" + is_read);
        System.out.println("is_issue=" + is_issue);
        System.out.println("message_status=" + message_status);
        System.out.println("==============================");
    }

}
