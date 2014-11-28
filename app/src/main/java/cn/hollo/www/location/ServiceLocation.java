package cn.hollo.www.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 位置服务
 */
public class ServiceLocation extends Service {
    private LocationProxy locationProxy;
    private LocationBinder binder;

    /**
     * 启动位置服务
     * @param context
     */
    public static void startService(Context context){
        Intent intent = new Intent(context, ServiceLocation.class);
        context.startService(intent);
    }

    /**
     * 停止位置服务
     * @param context
     */
    public static void stopService(Context context){
        Intent intent = new Intent(context, ServiceLocation.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationProxy = new LocationProxy(this);
        binder = new LocationBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!locationProxy.isUpdate)
            locationProxy.init();

        return super.onStartCommand(intent, flags, START_REDELIVER_INTENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //当定位没有停止，则进行停止
        if (locationProxy.isUpdate)
            locationProxy.stopLocation();

        binder = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /********************************************
     * 数据绑定类
     */
    public class LocationBinder extends Binder {
        /**
         * 添加定位监听器
         * @param key
         * @param listener
         */
        public void addLoactionListener(String key, OnLocationListener listener){
            if (key == null || listener == null || locationProxy == null ||  locationProxy.listenerMap == null)
                return;

            locationProxy.listenerMap.put(key, listener);
        }

        /**
         * 删除已经添加过的定位监听器
         * @param key
         */
        public void removeLocationListener(String key){
            if (key == null || locationProxy == null ||  locationProxy.listenerMap == null)
                return;

            locationProxy.listenerMap.remove(key);
        }
    }

    /********************************************
     * 定位数据监听器
     */
    public interface OnLocationListener{
        public void onLocationChanged(AMapLocation aMapLocation);
    }

    /********************************************
     * 定位服务代理类
     */
    private class LocationProxy implements AMapLocationListener {
        private final String TAG = "Location";
        private Map<String, OnLocationListener> listenerMap;
        private LocationManagerProxy mLocationManagerProxy;
        private boolean isUpdate;

        private LocationProxy(Context context){
            listenerMap = new HashMap<String, OnLocationListener>();
            listenerMap = Collections.synchronizedMap(listenerMap);
        }

        /**
         * 初始化定位
         */
        private void init() {
            if (mLocationManagerProxy == null)
                mLocationManagerProxy = LocationManagerProxy.getInstance(getApplicationContext());

            /**此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
            //在定位结束后，在合适的生命周期调用destroy()方法
            其中如果间隔时间为-1，则定位只定一次
            */
            mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 5 * 1000, 15, this);
            //进行混合定位
            //mLocationManagerProxy.setGpsEnable(false);

            isUpdate = true;
        }

        /**
         * 停止定位时，调用 LocationManagerProxy 类的 removeUpdates(AMapLocationListener listener)
         * 方法移除定位请求（单次定位时，无需调用该方法）。并对定位服务对象进行销毁，
         * 调用 LocationManagerProxy.destory() 方法。
         */
        private void stopLocation() {
            if (mLocationManagerProxy != null) {
                mLocationManagerProxy.removeUpdates(this);
                mLocationManagerProxy.destroy();
            }
            mLocationManagerProxy = null;
            isUpdate = false;
            listenerMap.clear();
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            /**
             * 通过位置信息对象 AMapLocation 可获取定位点的坐标、定位精度半径、provider名称
             * （网络定位时，返回 “lbs”；GPS定位时，返回“gps”）信息。除了上述信息，不同的定位方式，还返回其特有的位置信息。
             使用GPS定位时，可获取定位速度（getSpeed()），定位方向(getBearing())。
             使用网络定位时，返回省名称（如果是直辖市，省名称为null）（getProvince()），
             城市名称（getCity()），城市编码（getCityCode()），区（县）名称（getDistrict()），
             区域编码（getAdCode()），街道和门牌信息（getStreet()），详细地址（getAddress()），
             描述信息（getExtras()）

             说明：通过 AMapLocation.getExtras() 方法获取位置的描述信息，包括省、市、区以及街道信息，并以空格分隔。
             String desc = "";
             Bundle locBundle = location.getExtras();
             if (locBundle != null) {desc = locBundle.getString("desc");}

             使用混合定位时，需要判断是否为 GPS 定位使用类 AMapLocation 的 getProvider() 方法。
             GPS定位时，不能获取网络定位特有的信息。
            */
            if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0 && listenerMap != null && listenerMap.size() > 0){
                Set<Entry<String, OnLocationListener>> entries = listenerMap.entrySet();

                for (Entry<String, OnLocationListener> e : entries){
                    OnLocationListener l = e.getValue();
                    l.onLocationChanged(aMapLocation);
                }
            }
        }

        @Override
        public void onLocationChanged(Location location) {}//此方法已经废弃
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override
        public void onProviderEnabled(String s) {}
        @Override
        public void onProviderDisabled(String s) {}
    }
}
