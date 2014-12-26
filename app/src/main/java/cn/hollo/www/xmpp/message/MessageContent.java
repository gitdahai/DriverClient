package cn.hollo.www.xmpp.message;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by orson on 14-12-20.
 * 消息内容体
 */
public class MessageContent extends ChatContent {

    /*************************************************
     *
     * @param message
     * @return
     */
    public void setMessage(Message message){
        this.content = message.getBody();
        //读取其他信息
        DefaultPacketExtension messageParams = message.getExtension(ELEMENT_MESSAGE_PARAMS, NS_MESSAGE_PARAMS);

        if (messageParams != null){
            this.gender              = parserInt(messageParams.getValue("gender"));
            this.duration            = parserInt(messageParams.getValue("duration"));
            this.longitude           = parserDouble(messageParams.getValue("longitude"));
            this.latitude            = parserDouble(messageParams.getValue("latitude"));
            this.groupRoomType       = messageParams.getValue("groupRoomType");
            this.nickname            = messageParams.getValue("nickname");
            this.speaker             = messageParams.getValue("speaker");
            this.avatar              = messageParams.getValue("avatar");
            this.messageType         = messageParams.getValue("messageType");
            this.description         = messageParams.getValue("description");
            this.sendFromSpecialUser = messageParams.getValue("sendFromSpecialUser");
        }

        //抽取roomId
        this.roomId = extractRoomId(message.getFrom());
    }

    /**************************************************
     * 抽取roomId
     * @param from
     * @return
     */
    private static String extractRoomId(String from){
        int index = from.indexOf('@');
        String result = from.substring(0, index);
        return result;
    }

    /**************************************************
     * 由字符串转换成int型
     * @param ints
     * @return
     */
    public static int parserInt(String ints){
        if (ints == null || "".equals(ints))
            return 0;

        int result = 0;

        try{result = Integer.parseInt(ints);}
        catch(NumberFormatException e){}

        return result;
    }

    /***************************************************
     * 由字符串转换成double型
     * @param dbls
     * @return
     */
    public static double parserDouble(String dbls){
        if (dbls == null || "".equals(dbls))
            return 0;

        double result = 0;

        try{result = Double.parseDouble(dbls);}
        catch(NumberFormatException e){}

        return result;
    }

    /***************************************************
     * 由字符串转换成long型
     * @param lngs
     * @return
     */
    public static long parserLong(String lngs){
        if (lngs == null || "".equals(lngs))
            return 0;

        long result = 0;

        try{result = Long.parseLong(lngs);}
        catch(NumberFormatException e){}

        return result;
    }
}
