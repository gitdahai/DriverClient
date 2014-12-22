package cn.hollo.www.xmpp.message;

import cn.hollo.www.xmpp.XMPPManager;

/**
 * Created by orson on 14-12-22.
 * 处理没有链接的异常情况
 */
public class XmppNoConnectionHandle {
    /**
     * 构造方法，如果链接断开异常，则进行重链操作
     * @param connectManager
     */
    public XmppNoConnectionHandle(XMPPManager.XmppConnectManager connectManager){
        if (connectManager != null)
            connectManager.open();
    }
}
