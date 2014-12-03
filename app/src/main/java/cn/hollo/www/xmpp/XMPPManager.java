package cn.hollo.www.xmpp;

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
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;

import java.io.IOException;

/**
 * Created by orson on 14-11-12.
 * xmpp服务的管理类
 */
public class XMPPManager {
    private static XMPPManager instance;
    private XmppConnectManager connectManager;
    private ChatManager        chatManager;
    private ChatMessageListener chatMessageListener;

    private XMPPManager(){}

    static XMPPManager getInstance(){
        if (instance == null)
            instance = new XMPPManager();

        return instance;
    }

    /**
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
    }

    /**
     * 销毁当前的资源
     */
    void destroy(){
        if (connectManager != null)
            connectManager.close();

        connectManager = null;
    }

    /*************************************************************
     * 链接创建成功
     * @param xmppConnection
     */
    private void xmppConnected(XMPPConnection xmppConnection){

    }

    /************************************************************
     * 链接已经断开
     */
    private void xmppConnectionClosed(){
        chatManager = null;
        chatMessageListener = null;
    }

    /**********************************************************
     * 用户登录成功
     * @param xmppConnection
     */
    private void xmppAuthenticated(XMPPConnection xmppConnection){
        chatManager = ChatManager.getInstanceFor(xmppConnection);
        chatMessageListener = new ChatMessageListener();
    }

    /*********************************************************
     * 重链成功
     */
    private void xmppReconnectionSuccessful(){

    }

    /*********************************************************
     * 重链中
     * @param i
     */
    private void xmppReconnectingIn(int i){

    }

    /*********************************************************
     * 重链失败
     * @param e
     */
    private void xmppReconnectionFailed(Exception e){

    }

    /*********************************************************
     * 链接关闭失败
     * @param e
     */
    private void xmppConnectionClosedOnError(Exception e){
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
            }

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
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
            Chat chat = getChat(chartMessage.getTo());

            try {
                if (chat != null)
                    chat.sendMessage(chartMessage.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
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
            chat = chatManager.createChat(jid, jid, chatMessageListener);

        return chat;
    }

    /**********************************************************
     * 消息发送时的监听器
     */
    private class ChatMessageListener implements MessageListener {
        public void processMessage(Chat chat, Message message) {

        }
    }

    /**********************************************************
     * 接受数据包监听器
     */
    private class PackageListener implements PacketListener {
        public void processPacket(Packet packet) {
            try{
                Message message = (Message)packet;
                MessageFilterManager filterManager = MessageFilterManager.getInstance();
                filterManager.filterMessage(message);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /***********************************************************
     * xmpp链接管理类
     */
    private class XmppConnectManager extends Thread implements ConnectionListener {
        private String openfirLogianName;
        private String openfirLoginPassword;
        private XMPPConnection connection;
        private PackageListener pkListener;
        private boolean isConnect;

        private XmppConnectManager(String openfirLogianName, String openfirLoginPassword){
            this.openfirLogianName    = openfirLogianName;
            this.openfirLoginPassword = openfirLoginPassword;
            this.pkListener = new PackageListener();
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
            connection.addPacketListener(pkListener, new MessageTypeFilter(Message.Type.chat));
            connection.addPacketListener(pkListener, new MessageTypeFilter(Message.Type.normal));
            connection.addPacketListener(pkListener, new MessageTypeFilter(Message.Type.groupchat));
            connection.addPacketListener(pkListener, new MessageTypeFilter(Message.Type.error));
            connection.addPacketListener(pkListener, new MessageTypeFilter(Message.Type.headline));
        }

        /**
         * 进行链接
         */
        private void open(){
            this.start();
        }

        /**
         * 进行xmpp的链接
         */
        public void run(){
            while (!isConnect){
                try {
                    connection.connect();
                    connection.login(openfirLogianName, openfirLoginPassword);
                    isConnect = true;
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
                        connection.removePacketListener(pkListener);
                        pkListener = null;
                        connection = null;
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

            try{
                pubNode = mgr.getNode(id);
            }
            catch (XMPPException.XMPPErrorException ex){
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
