package cn.hollo.www.xmpp;

import android.content.Context;

import org.jivesoftware.smack.packet.Message;

import cn.hollo.www.content_provider.ModelChatMessage;
import cn.hollo.www.content_provider.OpenHelperChatMessage;
import cn.hollo.www.content_provider.ProviderChatMessage;
import cn.hollo.www.xmpp.message.MessageContent;

/**
 * Created by orson on 14-11-13.
 * 消息过滤器
 */
public class MessageFilterManager {
    private static MessageFilterManager instance;

    /*************************************************
     *
     */
    private MessageFilterManager(){
    }

    /************************************************
     *
     * @return
     */
    static MessageFilterManager getInstance(){
        if (instance == null)
            instance = new MessageFilterManager();

        return instance;
    }


    /************************************************
     * 过滤信息
     * @param message
     */
    void filterMessage(Context context, Message message){
        Message.Type type = message.getType();

        if (type == Message.Type.groupchat){
            handleGroupMessage(context, message);
        }
        else if (type == Message.Type.chat){
            handleChatMessage(context, message);
        }
        else if (type == Message.Type.normal){
            handleNormalMessage(context, message);
        }
        else if (type == Message.Type.error){
            handleErrorMessage(context, message);
        }
        else if (type == Message.Type.headline){
            handleHeadlineMessage(context, message);
        }
    }

    /************************************************
     * 处理群组消息
     * @param context
     * @param message
     */
    private void handleGroupMessage(Context context, Message message){
        MessageContent content = MessageContent.newMessageContent(message);
        ModelChatMessage chatMessage = new ModelChatMessage(content);

        //如果是司机自己发送的消息,则需要更新
        if ("Driver".equals(content.sendFromSpecialUser)){
            chatMessage.message_status = 1;     //已经成功发送
            chatMessage.is_read  = true;        //设置已读
            chatMessage.is_issue = true;        //标记自己发送的

            String where = OpenHelperChatMessage.ROOM_ID + "=? and " + OpenHelperChatMessage.TIME_STAMP + "=?";
            String[] selectionArgs = {chatMessage.room_id, "" + chatMessage.timestamp};
            //更新数据库
            context.getContentResolver().update(ProviderChatMessage.CONTENT_URI, chatMessage.getContentValues(), where, selectionArgs);
        }
        //否则就是其他用户发送的
        else{
            //如果消息的类型是“文本信息”，则设置接收标志为成功接收
            if (MessageContent.PLAIN_MESSAGE.equals(content.messageType))
                chatMessage.message_status = 1;
            //否则，其他任何消息，都设置成，还没有接收
            else
                chatMessage.message_status = 3;

            //设置还没有读取状态
            chatMessage.is_read  = false;
            //标记自己发送的(false＝是接收别人发送的消息)
            chatMessage.is_issue = false;
            //添加接收时间戳
            chatMessage.timestamp = System.currentTimeMillis();

            //如果是其他用户发送的消息，则直接插入到数据库中
            context.getContentResolver().insert(ProviderChatMessage.CONTENT_URI, chatMessage.getContentValues());
        }

        chatMessage.print();
    }

    /***********************************************
     *
     * @param context
     * @param message
     */
    private void handleChatMessage(Context context, Message message){

    }

    /**********************************************
     *
     * @param context
     * @param message
     */
    private void handleNormalMessage(Context context, Message message){

    }

    /**********************************************
     *
     * @param context
     * @param message
     */
    private void handleErrorMessage(Context context, Message message){

    }

    /**********************************************
     *
     * @param context
     * @param message
     */
    private void handleHeadlineMessage(Context context, Message message){

    }
}
