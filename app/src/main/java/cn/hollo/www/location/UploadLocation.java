package cn.hollo.www.location;

import com.amap.api.location.AMapLocation;
import cn.hollo.www.xmpp.ISubscribe;
import cn.hollo.www.xmpp.XMPPManager;

/**
 * Created by orson on 14-11-13.
 * 地理位置信息上报
 *
 */
public class UploadLocation{
    private  static UploadLocation instance;
    private XMPPManager xmppManager;
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
     * 释放资源
     */
    public void release(){
        instance    = null;
    }


    /*************************************************
     * 发送位置订阅
     * @param xmppManager
     * @param aMapLocation
     */
    public void onLocationChanged(XMPPManager xmppManager, AMapLocation aMapLocation, String jid) {
        if (xmppManager == null || aMapLocation == null)
            return;

        Subscribe subscribe = new Subscribe(aMapLocation.getLatitude(), aMapLocation.getLongitude(), jid);
        xmppManager.sendSubscribe(subscribe);

        System.out.println("==============已经发送了位置订阅信息====================");
    }

    /**************************************************
     * 订阅消息的实体类
     */
    public class Subscribe implements ISubscribe {
        private double lat;
        private double lng;
        private String jid;

        Subscribe(double lat, double lng, String jid){
            this.lat = lat;
            this.lng = lng;
            this.jid = jid;
        }

        @Override
        public String getId() {return jid;}
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
