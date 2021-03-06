package cn.hollo.www.xmpp;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5Proxy;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;

import java.io.IOException;
import java.util.List;

import cn.hollo.www.xmpp.message.ObserverSendMessageError;

/**
 * Created by orson on 14-11-12.
 * xmpp服务的管理类
 */
public class XMPPManager {
    private static XMPPManager instance;
    private Context context;
    private XmppConnectManager connectManager;
    private ChatManager        chatManager;
    private MultiUserChatManager multiUserChatManager;
    private ChatMessageListener chatMessageListener;

    /************************************************
     *
     */
    private XMPPManager(Context context){ this.context = context.getApplicationContext();}

    /************************************************
     *
     * @param context
     * @return
     */
    static XMPPManager getInstance(Context context){
        if (instance == null)
            instance = new XMPPManager(context);

        return instance;
    }

    /************************************************
     *
     * @param openfirLogianName
     * @param openfirLoginPassword
     */
    void create(String openfirLogianName, String openfirLoginPassword){
        if (connectManager != null)
            connectManager.close();

        connectManager = new XmppConnectManager(openfirLogianName, openfirLoginPassword);
        connectManager.init();
        connectManager.open();

        multiUserChatManager = new MultiUserChatManager();
        chatMessageListener   = new ChatMessageListener();
    }

    /*************************************************
     * 销毁当前的资源
     */
    void destroy(){
        if (connectManager != null)
            connectManager.close();

        connectManager = null;
        multiUserChatManager.clear();
        multiUserChatManager = null;
        chatMessageListener = null;
        context = null;
        instance = null;
    }

    /*************************************************************
     * 来自某个聊天室的邀请事件
     * @param conn
     * @param room
     * @param inviter
     * @param reason
     * @param password
     * @param message
     */
    private void xmppInvitationReceived(XMPPConnection conn, String room, String inviter, String reason, String password, Message message){
        MultiUserChat muc = new MultiUserChat(conn, room);

        try {
            muc.join(connectManager.openfirLogianName);
            System.out.println("===========xmpp 邀请加入聊天室=========");
        } catch (SmackException.NoResponseException e) {
            Log.w("HL-DEBUG", e);
        } catch (XMPPException.XMPPErrorException e) {
            Log.w("HL-DEBUG",e);
        } catch (SmackException.NotConnectedException e) {
            Log.w("HL-DEBUG",e);
        }
    }

    /*************************************************************
     * 链接创建成功
     * @param xmppConnection
     */
    private void xmppConnected(XMPPConnection xmppConnection){
        System.out.println("===========xmpp已经链接=========");
    }

    /************************************************************
     * 链接已经断开
     */
    private void xmppConnectionClosed(){
        //清空群组聊天管理对象
        if (multiUserChatManager != null)
            multiUserChatManager.clear();

        chatManager = null;
        multiUserChatManager = null;
        chatMessageListener = null;
    }

    /**********************************************************
     * 用户登录成功
     * @param xmppConnection
     */
    private void xmppAuthenticated(XMPPConnection xmppConnection){
        chatManager = ChatManager.getInstanceFor(xmppConnection);
        multiUserChatManager.setXmppConnection(xmppConnection);

        System.out.println("===========user logined openfire=============");
    }

    /*********************************************************
     * 重链成功
     */
    private void xmppReconnectionSuccessful(){
        System.out.println("===========xmpp　重链成功=========");
    }

    /*********************************************************
     * 重链中
     * @param i
     */
    private void xmppReconnectingIn(int i){
        System.out.println("===========xmpp　重链中=========");
    }

    /*********************************************************
     * 重链失败
     * @param e
     */
    private void xmppReconnectionFailed(Exception e){
        System.out.println("===========xmpp　重链失败=========");
    }

    /*********************************************************
     * 链接关闭失败
     * @param e
     */
    private void xmppConnectionClosedOnError(Exception e){
        System.out.println("===========xmpp　链接关闭异常========= ");
        e.printStackTrace();
    }

    /*********************************************************
     * 加入群组聊天室
     * @param roomIds
     */
    public void jionInRooms(List<String> roomIds, String userId){
         multiUserChatManager.createMultiUserChats(roomIds, userId);
    }

    /********************************************************
     * 添加订阅信息，该消息随后就会通过xmpp发送出去
     * @param subscribe
     */
    public void sendSubscribe(ISubscribe subscribe){
        if (subscribe == null || connectManager == null)
            return;

        try {
            LeafNode leafNode = connectManager.createSubscribeNode(subscribe.getId());

            if (leafNode != null){
                SimplePayload spl = new SimplePayload(subscribe.getElementName(), subscribe.getNamespace(), subscribe.getXmlPayload());
                PayloadItem item = new PayloadItem(spl);
                leafNode.send(item);
                System.out.println("===============发送了订阅信息==================");
            }

        } catch (SmackException.NotConnectedException e) {
            System.out.println("----------xmpp　没有链接-----------");
            e.printStackTrace();
            connectManager.open();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************
     * 发送单聊消息
     */
    public void sendSingleChat(IChatMessage chartMessage){
        if (chartMessage != null && chatManager != null){
            Chat chat = getChat(chartMessage.getRoomId());

            try {
                if (chat != null){
                    Message message = chartMessage.getMessage();
                    chat.sendMessage(message);
                }

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    /**********************************************************
     * 发送群组聊天信息
     * @param chartMessage
     */
    public void sendMultiUserChat(IChatMessage chartMessage, ObserverSendMessageError listener){
        String to = chartMessage.getRoomId();
        String userId = chartMessage.getJid();
        MultiUserChat muc = getMultiUserChat(to, userId);
        Exception exception = null;

        try {
            if (muc != null){
                Message message = chartMessage.getMessage();
                muc.sendMessage(message);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
            exception = e;

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            exception = e;

        } finally {
            //如果发生错误，则通知调用者
            if (exception != null && listener != null)
                listener.onSendError(exception, chartMessage);
        }
    }

    /**********************************************************
     * 获取当前chat对象
     * @param jid
     * @return
     */
    private Chat getChat(String jid){
        Chat chat = chatManager.getThreadChat(jid);

        if (chat == null)
            chat = chatManager.createChat(jid + "@" + XMPPConstant.OPENFIRE_DOMAIN, jid, chatMessageListener);

        return chat;
    }

    /***********************************************************
     * 获取群组聊天对象
     * @param roomJid
     * @return
     */
    private MultiUserChat getMultiUserChat(String roomJid, String userId){
        MultiUserChat muc = multiUserChatManager.getMultiUserChat(roomJid);

        //如果不存在，就创建一个
        if (muc == null)
             multiUserChatManager.createMultiUserChat(roomJid, userId);


        return multiUserChatManager.getMultiUserChat(roomJid);
    }

    /**********************************************************
     * 消息发送时的监听器
     */
    private class ChatMessageListener implements MessageListener {
        public void processMessage(Chat chat, Message message) {
            //System.out.println("=============发送消息事件========　" + message.toString());
        }
    }

    /**********************************************************
     * 接受数据包监听器
     */
    private class PackageListener extends MessageTypeFilter implements PacketListener{
        private Message.Type type;

        /**===================================================
         *
         * @param type
         */
        public PackageListener(Message.Type type) {
            super(type);
            this.type = type;
        }

        /**===================================================
         * 过滤Packet是否需要处理的
         * @param packet
         * @return
         */
        public boolean accept(Packet packet) {
            boolean result = false;

            //过滤群组消息
            if (packet instanceof Message){
                Message message = (Message)packet;

                if (type == message.getType())
                    result = true;

                else if (type == message.getType())
                    result = true;
            }

            return result;
        }

        /**===================================================
         * 已知的子类和间接子类:(Presence, IQ, Registration, RosterPacket, Session, Bind)
         * @param packet
         */
        public void processPacket(Packet packet) {
            try{
                Message message = (Message)packet;
                MessageFilterManager filterManager = MessageFilterManager.getInstance();
                filterManager.filterMessage(context, message);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /***********************************************************
     * xmpp链接管理类
     */
    public class XmppConnectManager extends Thread implements ConnectionListener, InvitationListener{
        private String openfirLogianName;
        private String openfirLoginPassword;
        private XMPPConnection connection;
        private PackageListener pkListenerGroupChat;
        private PackageListener pkListenerChat;
        private PackageListener pkListenerNormal;
        private PackageListener pkListenerError;
        private PackageListener pkListenerHeadline;
        private boolean isOpened;

        private XmppConnectManager(String openfirLogianName, String openfirLoginPassword){
            this.openfirLogianName    = openfirLogianName;
            this.openfirLoginPassword = openfirLoginPassword;

            this.pkListenerGroupChat = new PackageListener(Message.Type.groupchat);
            this.pkListenerChat      = new PackageListener(Message.Type.chat);
            this.pkListenerNormal    = new PackageListener(Message.Type.normal);
            this.pkListenerError     = new PackageListener(Message.Type.error);
            this.pkListenerHeadline  = new PackageListener(Message.Type.headline);
        }

        /**
         * 初始化
         */
        private void init(){
            Socks5Proxy.setLocalSocks5ProxyEnabled(false);
            ConnectionConfiguration config = new ConnectionConfiguration(XMPPConstant.OPENFIRE_DOMAIN, XMPPConstant.OPENFIRE_PORT);
            config.setReconnectionAllowed(true);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setCompressionEnabled(false);
            config.setSendPresence(true);
            config.setDebuggerEnabled(true);

            connection = new XMPPTCPConnection(config);
            connection.addConnectionListener(this);
            connection.addPacketListener(pkListenerGroupChat, pkListenerGroupChat);
            connection.addPacketListener(pkListenerChat, pkListenerChat);
            connection.addPacketListener(pkListenerNormal, pkListenerNormal);
            connection.addPacketListener(pkListenerError, pkListenerError);
            connection.addPacketListener(pkListenerHeadline, pkListenerHeadline);
            MultiUserChat.addInvitationListener(connection, this);
        }

        /**
         * 进行链接
         */
        public void open(){
            if (!isOpened && !connection.isConnected())
                this.start();
        }

        /**
         * 进行xmpp的链接
         */
        public void run(){
            while (true){
                isOpened = true;
                try {
                    connection.connect();

                    connection.login(openfirLogianName, openfirLoginPassword);
                    break;
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();    break;
                } catch(Exception e){
                    e.printStackTrace();    break;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 关闭
         */
        private void close(){
            if (connection != null){

                //需要开启新的线程，执行断开链接操作
                new Thread(new Runnable(){
                    public void run() {
                        try {
                            connection.disconnect();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        //该方法必须在关闭链接之后调用
                        //否则有的事件无法调用
                        connection.removeConnectionListener(XmppConnectManager.this);
                        connection.removePacketListener(pkListenerGroupChat);
                        connection.removePacketListener(pkListenerChat);
                        connection.removePacketListener(pkListenerNormal);
                        connection.removePacketListener(pkListenerError);
                        connection.removePacketListener(pkListenerHeadline);
                        connection = null;
                        isOpened = false;
                    }
                }).start();
            }
        }

        @Override
        public void connected(XMPPConnection xmppConnection) {xmppConnected(xmppConnection);}
        @Override
        public void authenticated(XMPPConnection xmppConnection) {xmppAuthenticated(xmppConnection);}
        @Override
        public void connectionClosed() {xmppConnectionClosed();}
        @Override
        public void connectionClosedOnError(Exception e) {xmppConnectionClosedOnError(e);}
        @Override
        public void reconnectingIn(int i) {xmppReconnectingIn(i);}
        @Override
        public void reconnectionSuccessful() {xmppReconnectionSuccessful();}
        @Override
        public void reconnectionFailed(Exception e) {xmppReconnectionFailed(e);}
        @Override
        public void invitationReceived(XMPPConnection xmppConnection, String room, String inviter, String reason, String password, Message message) {
            xmppInvitationReceived(xmppConnection, room, inviter, reason, password, message);
        }

        /********************************************************
         * 获取（创建）LeafNode　对象
         * @param id
         * @return
         * @throws SmackException.NotConnectedException
         * @throws SmackException.NoResponseException
         * @throws XMPPException.XMPPErrorException
         */
        private LeafNode createSubscribeNode(String id) throws
                SmackException.NotConnectedException,
                SmackException.NoResponseException,
                XMPPException.XMPPErrorException {

            PubSubManager mgr = new PubSubManager(connection);
            LeafNode pubNode = null;

            try {
                pubNode = mgr.getNode(id);
            } catch (XMPPException.XMPPErrorException ex) {
                ConfigureForm form = new ConfigureForm(FormType.submit);
                form.setAccessModel(AccessModel.open);
                form.setDeliverPayloads(true);
                form.setNotifyRetract(false);
                form.setPersistentItems(false);
                form.setPublishModel(PublishModel.publishers);
                pubNode = (LeafNode) mgr.createNode(id, form);
            }

            return pubNode;
        }
    }
}
