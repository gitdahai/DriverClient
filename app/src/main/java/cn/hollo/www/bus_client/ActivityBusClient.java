package cn.hollo.www.bus_client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;

import cn.hollo.www.R;
import cn.hollo.www.location.ServiceLocation;

/**
 * 班车入口
 */
public class ActivityBusClient extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_client);

        Intent intent = new Intent(this, ServiceLocation.class);
        startService(intent);

        this.bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, ServiceLocation.class);
        this.unbindService(sc);
        this.stopService(intent);
    }

    //在客户端覆写onServiceConnected方法,当服务绑定成功会调用此回调函数
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceLocation.LocationBinder binder = (ServiceLocation.LocationBinder)service;
            binder.addLoactionListener("ss", new ServiceLocation.OnLocationListener(){
                public void onLocationChanged(AMapLocation aMapLocation) {
                    double lat = aMapLocation.getLatitude();
                    double lng = aMapLocation.getLongitude();

                    System.out.println("=========lat====== " + lat);
                    System.out.println("=========lng====== " + lng);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

}
