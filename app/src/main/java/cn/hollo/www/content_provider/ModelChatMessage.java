package cn.hollo.www.content_provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jivesoftware.smack.packet.Message;

import cn.hollo.www.xmpp.message.MessageContent;

/**
 * Created by orson on 14-12-23.
 * 聊天信息模型类
 */
public class ModelChatMessage extends MessageContent{
    /**正在上传（正在接收）中*/
    public static final int STATUS_TRANSFERING = 0;
    /**已经接收（发送）成功*/
    public static final int STATUS_TRANSFER_SUCCED = 1;
    /**接收（上传）失败*/
    public static final int STATUS_TRANSFER_FAIL = 2;
    /**没有接收（没有上传）*/
    public static final int STATUS_NONE_TRANSFER = 3;
    /**声音正在播放中*/
    public static final int STATUS_SOUND_PLAYING = 4;


    public int      _id;            //数据表的主键索引id
    public long     messageId;      //发送的消息才会有该id，用于查询条件(该数据由时间戳生成)
    public boolean  isRead;         //消息的状态:消息的状态（true=已读，false=未读）
    public boolean  isIssue;        //消息:消息是自己发送的，还是接收的(true=发送，false=接收的)

    /**
     * 消息的发送或者接收状态:消息的发送／接收状态（
     // 0=接收/上传中，
     // 1=接收/发送成功，
     // 2=接收/发送失败,
     // 3=未接收/上传
     // 4=播放中(语音播放中)）
     */
    public int  messageStatus;

    public ModelChatMessage(){}
    /**************************************************
     *
     * @param cursor
     */
    public ModelChatMessage(Cursor cursor){
        setCursor(cursor);
    }

    /**************************************************
     * 由Message生成一个实例对象
     * @param message
     */
    public ModelChatMessage(Message message){setMessage(message);}

    /**************************************************
     * 返回消息的封装数据
     * @return
     */
    public Message getMessage(){
        Message message = super.getMessage();

        if(message != null)
            message.addBody("messageId", "" + messageId);

        return message;
    }


    /**************************************************
     *
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(OpenHelperChatMessage.GENDER,                    this.gender);
        values.put(OpenHelperChatMessage.DURATION,                  this.duration);
        values.put(OpenHelperChatMessage.LONGITUDE,                 this.longitude);
        values.put(OpenHelperChatMessage.LATITUDE,                  this.latitude);
        values.put(OpenHelperChatMessage.CONTENT,                   this.content);
        values.put(OpenHelperChatMessage.GROUP_ROOM_TYPE,           this.groupRoomType);
        values.put(OpenHelperChatMessage.NICKNAME,                  this.nickname);
        values.put(OpenHelperChatMessage.SPEEKER,                   this.speaker);
        values.put(OpenHelperChatMessage.AVATAR,                    this.avatar);
        values.put(OpenHelperChatMessage.MESSAGE_TYPE,              this.messageType);
        values.put(OpenHelperChatMessage.DESCRIPTION,               this.description);
        values.put(OpenHelperChatMessage.SEND_FROM_SPECIAL_USER,    this.sendFromSpecialUser);
        values.put(OpenHelperChatMessage.ROOM_ID,                   this.roomId);
        values.put(OpenHelperChatMessage.MESSAGE_ID,                this.messageId);
        values.put(OpenHelperChatMessage.MESSAGE_STATUS,            this.messageStatus);
        values.put(OpenHelperChatMessage.IS_READ,                   this.isRead);
        values.put(OpenHelperChatMessage.IS_ISSUE,                  this.isIssue);
        return values;
    }

    /***************************************************
     * 从集合中读取数据
     * @param cursor
     */
    public void setCursor(Cursor cursor){
        this._id                = cursor.getInt(OpenHelperChatMessage.INDEX_ID);
        this.gender             = cursor.getInt(OpenHelperChatMessage.INDEX_GENDER);
        this.duration           = cursor.getInt(OpenHelperChatMessage.INDEX_DURATION);
        this.longitude          = cursor.getDouble(OpenHelperChatMessage.INDEX_LONGITUDE);
        this.latitude           = cursor.getDouble(OpenHelperChatMessage.INDEX_LATITUDE);
        this.content            = cursor.getString(OpenHelperChatMessage.INDEX_CONTENT);
        this.groupRoomType      = cursor.getString(OpenHelperChatMessage.INDEX_GROUP_ROOM_TYPE);
        this.nickname           = cursor.getString(OpenHelperChatMessage.INDEX_NICKNAME);
        this.speaker            = cursor.getString(OpenHelperChatMessage.INDEX_SPEEKER);
        this.avatar             = cursor.getString(OpenHelperChatMessage.INDEX_AVATAR);
        this.messageType        = cursor.getString(OpenHelperChatMessage.INDEX_MESSAGE_TYPE);
        this.description        = cursor.getString(OpenHelperChatMessage.INDEX_DESCRIPTION);
        this.sendFromSpecialUser = cursor.getString(OpenHelperChatMessage.INDEX_SEND_FROM_SPECIAL_USER);
        this.roomId             = cursor.getString(OpenHelperChatMessage.INDEX_ROOM_ID);
        this.messageId          = cursor.getLong(OpenHelperChatMessage.INDEX_MESSAGE_ID);
        this.messageStatus      = cursor.getInt(OpenHelperChatMessage.INDEX_MESSAGE_STATUS);
        this.isRead             = (cursor.getInt(OpenHelperChatMessage.INDEX_IS_READ) == 0 ? false : true);
        this.isIssue            = (cursor.getInt(OpenHelperChatMessage.INDEX_IS_ISSUE) == 0 ? false : true);
    }

    /***************************************************
     *
     * @param message
     */
    public void setMessage(Message message){
        super.setMessage(message);
        this.messageId = parserLong(message.getBody("messageId"));

        System.out.println("====messageId===== " + messageId);
    }

    /**************************************************
     * 插入到数据库中
     * @param context
     */
    public void inserToDatabase(Context context){
        if (context != null){
            context.getContentResolver().insert(ProviderChatMessage.CONTENT_URI, getContentValues());
        }
    }

    /***************************************************
     * 跟新数据到数据库
     */
    public void updateToDatabase(Context context){
        if (context == null)
            return;

        String where = OpenHelperChatMessage.ROOM_ID + "=? and " + OpenHelperChatMessage.MESSAGE_ID + "=?";
        String[] selectionArgs = {this.roomId, "" + this.messageId};
        context.getContentResolver().update(ProviderChatMessage.CONTENT_URI, getContentValues(), where, selectionArgs);
    }
}
