package cn.hollo.www.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hollo.www.thread_pool.ThreadPool;

/**
 * Created by orson on 14-12-20.
 */
public class MultiUserChatManager {
    private XMPPConnection xmppConnection;
    private Map<String, MultiUserChat> multiUserChats = new HashMap<String, MultiUserChat>();
    private Map<String, String> cache = new HashMap<String, String>();

    /***********************************************
     * 获取群组聊天对象
     * @param roomId
     * @return
     */
    synchronized public MultiUserChat getMultiUserChat(String roomId){
        return multiUserChats.get(roomId);
    }

    /***********************************************
     * 创建一个新的群组聊天对象
     * @param roomId
     * @param userJid
     * @return
     */
    synchronized public void createMultiUserChat(String roomId, String userJid){
        if (roomId == null || userJid == null)
            return ;

        //如果链接还没有建立起来，则先缓存起来
        if (xmppConnection == null){
            cache.put(roomId, userJid);
            return;
        }

        //如果已经存在该聊天会话对象，则直接返回
        if (multiUserChats.get(roomId) != null)
            return;

        //创建一个新的聊天会话对象
        MultiUserChat muc = new MultiUserChat(xmppConnection, roomId + "@conference." + XMPPConstant.OPENFIRE_DOMAIN);

        try {
            //如果当前用户没有加入到聊天室，则进行加入操作
            if (!muc.isJoined()){
                DiscussionHistory history = new DiscussionHistory();
                history.setSince(new Date());
                muc.join(userJid, "", history, 1000);
            }

            //保存该聊天室会话对象
            multiUserChats.put(roomId, muc);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    /************************************************
     * 加入一组聊天室中
     * @param roomIds
     * @param userJid
     */
    synchronized public void createMultiUserChats(List<String>roomIds, String userJid){
        //否则直接创建
        for (String roomId : roomIds){
            //如果集合中不存在相同的聊天室room ,则创建一个新的
            createMultiUserChat(roomId, userJid);
        }
    }

    /*************************************************
     * 设置链接对象
     * @param xmppConnection
     */
    public void setXmppConnection(XMPPConnection xmppConnection){
        if (xmppConnection != null){
            this.xmppConnection = xmppConnection;

            //如果有
            if (cache.size() > 0){
                ThreadPool pool = ThreadPool.getInstance();
                pool.addTask(new JionInRoom());
            }
        }
    }

    /*************************************************
     * 清空容器
     */
    public void clear(){
        xmppConnection = null;
        cache.clear();
        multiUserChats.clear();
    }

    /**************************************************
     * 加入房间任务
     */
    private class JionInRoom implements Runnable{
        public void run() {
            Set<Map.Entry<String, String>> entries = cache.entrySet();
            String roomId = null;
            String jid = null;
            //开始循环创建工作
            for (Map.Entry entry : entries){
                roomId = (String)entry.getKey();
                jid = (String)entry.getValue();
                createMultiUserChat(roomId, jid);
            }
            //清空缓存容器
            cache.clear();
        }
    }
}
