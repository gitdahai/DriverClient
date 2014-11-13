package cn.hollo.www.xmpp;

/**
 * Created by orson on 14-11-13.
 * 订阅接口
 */
public interface ISubscribe {
    /**获取消息发布者的id*/
    public String getId();
    /**节点的名称*/
    public String getElementName();
    /**节点的命名空间*/
    public String getNamespace();
    /**需要传递的xml字符串*/
    public String getXmlPayload();
}
