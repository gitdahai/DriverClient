package cn.hollo.www.location;

import com.amap.api.location.AMapLocation;

import cn.hollo.www.xmpp.ISubscribe;
import cn.hollo.www.xmpp.XMPPManager;

/**
 * Created by orson on 14-11-13.
 * 地理位置信息上报
 *
 */
public class UploadLocation implements ServiceLocation.OnLocationListener {
    private  static UploadLocation instance;
    private XMPPManager xmppManager;
    private String  id;

    private UploadLocation(){};

    /**
     * 返回该类的实例对象
     * @return
     */
    public static UploadLocation getInstance(){
        if (instance == null)
            instance = new UploadLocation();

        return instance;
    }

    /**
     * 设置订阅消息的id
     * @param id
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * 设置xmpp服务对象
     * @param manager
     */
    public void setXMPPManager(XMPPManager manager){
        this.xmppManager = manager;
    }

    /**
     * 释放资源
     */
    public void release(){
        xmppManager = null;
        instance    = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (xmppManager == null || aMapLocation == null || id == null)
            return;

        Subscribe subscribe = new Subscribe(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        xmppManager.sendSubscribe(subscribe);
    }

    /**************************************************
     * 订阅消息的实体类
     */
    public class Subscribe implements ISubscribe {
        private double lat;
        private double lng;

        Subscribe(double lat, double lng){
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public String getId() {return id;}
        @Override
        public String getElementName() {return "location";}
        @Override
        public String getNamespace() {return "pubsub:shuttle:location";}
        @Override
        public String getXmlPayload() {
            return "<location xmlns=\"pubsub:shuttle:location\">" +
                    "<longitude>" + lng + "</longitude>" +
                    "<latitude>"  + lat + "</latitude>" +
                    "</location>";
        }
    }
}
