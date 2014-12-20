package cn.hollo.www.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by orson on 14-12-20.
 */
public class MultiUserChatManager {
    private Map<String, MultiUserChat> multiUserChats = new HashMap<String, MultiUserChat>();

    /***********************************************
     * 获取群组聊天对象
     * @param roomId
     * @return
     */
    public MultiUserChat getMultiUserChat(String roomId){
        return multiUserChats.get(roomId);
    }

    /***********************************************
     * 创建一个新的群组聊天对象
     * @param connection
     * @param roomId
     * @param userJid
     * @return
     */
    public MultiUserChat createMultiUserChat(XMPPConnection connection, String roomId, String userJid){
        MultiUserChat muc = new MultiUserChat(connection, roomId);
        try {
            muc.join(userJid);
            multiUserChats.put(roomId, muc);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return muc;
    }

    /*************************************************
     * 清空容器
     */
    public void clear(){
        multiUserChats.clear();
    }
}
