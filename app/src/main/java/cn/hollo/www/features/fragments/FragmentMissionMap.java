package cn.hollo.www.features.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.List;

import cn.hollo.www.R;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.features.FragmentBase;
import cn.hollo.www.features.informations.StationInfo;
import cn.hollo.www.location.ServiceLocation;

/**
 * Created by orson on 14-12-18.
 * 群组聊天页面
 */
public class FragmentMissionMap extends FragmentBase {
    private MissionMap missionMap;
    private ServiceLocation.LocationBinder binder;

    /**************************************************
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission_map, null);
        missionMap = new MissionMap(view);
        missionMap.mapView.onCreate(savedInstanceState);
        return view;
    }

    public void onDestroy(){
        if (missionMap != null)
            missionMap.mapView.onDestroy();

        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle bundle){
        if (missionMap != null)
            missionMap.mapView.onSaveInstanceState(bundle);
    }

    public void onResume(){
        if (missionMap != null)
            missionMap.mapView.onResume();

        ServiceManager serviceManager = ServiceManager.getInstance(getActivity());
        serviceManager.getLocationBinder(onLocaBinder);

        super.onResume();
    }

    public void onPause(){
        if (missionMap != null)
            missionMap.mapView.onPause();

        super.onPause();
    }

    @Override
    public void onStop() {
        //移除绑定的事件
        if (binder != null)
            binder.removeLocationListener("MissionMap");

        super.onStop();
    }

    @Override
    public void onStartMission(List<StationInfo.Station>  stations) {

    }

    @Override
    public void onArrivingStation(StationInfo.Station station) {

    }

    @Override
    public void onFinishMission() {

    }

    /*************************************************
     * 得到位置服务binder对象
     */
    private ServiceManager.OnLocaBinder onLocaBinder = new ServiceManager.OnLocaBinder(){
        public void onBinder(ServiceLocation.LocationBinder binder) {
            if (missionMap != null){
                binder.addLoactionListener("MissionMap", missionMap.locationListener);
                FragmentMissionMap.this.binder = binder;
            }
        }
    };

    /*************************************************
     * 地图显示类
     */
    private class MissionMap {
        private MapView  mapView;
        private TextView onBusPopulationText;   //上车人数的文本
        private TextView offBusPopulationText;  //下车人数的文本
        private TextView showStationName;       //显示当前站点名称的文本

        private AMap aMap;
        private UiSettings uiSettings;
        private LocationSource.OnLocationChangedListener onLocationChangedListener;

        /*********************************************
         *
         * @param view
         */
        private MissionMap(View view){
            mapView = (MapView)view.findViewById(R.id.workDetailMapView);
            onBusPopulationText = (TextView)view.findViewById(R.id.onBusPopulationText);
            offBusPopulationText = (TextView)view.findViewById(R.id.offBusPopulationText);
            showStationName = (TextView)view.findViewById(R.id.showStationText);
            showStationName.setVisibility(View.GONE);

            aMap = mapView.getMap();
            aMap.setOnMapLoadedListener(onMapLoadListener);
            aMap.setOnInfoWindowClickListener(infoWindowClickListener);

            uiSettings = aMap.getUiSettings();
            //隐藏放大缩小按钮
            uiSettings.setZoomControlsEnabled(false);
            //启用地图指南针
            uiSettings.setCompassEnabled(true);
            //显示比例尺
            uiSettings.setScaleControlsEnabled(true);
            //禁止手势旋转地图
            uiSettings.setRotateGesturesEnabled(true);

            setUpMap();
        }

        /*****************************************************
         * 构造地图定位图形
         */
        private void setUpMap() {
            // 自定义系统定位蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            // 自定义定位蓝点图标
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus));
            // 自定义精度范围的圆形边框颜色
            //myLocationStyle.strokeColor(Color.BLACK);
            //自定义精度范围的圆形边框宽度
            //myLocationStyle.strokeWidth(5);
            //设置圆形区域（以定位位置为圆心，定位半径的圆形区域）的填充颜色。
            myLocationStyle.radiusFillColor(getActivity().getResources().getColor(R.color.color_trans));
            //设置圆形区域（以定位位置为圆心，定位半径的圆形区域）的边框颜色。
            myLocationStyle.strokeColor(getActivity().getResources().getColor(R.color.color_trans));
            // 将自定义的 myLocationStyle 对象添加到地图上
            aMap.setMyLocationStyle(myLocationStyle);
            // 构造 LocationManagerProxy 对象
            //mAMapLocationManager = LocationManagerProxy.getInstance(LocationSourceActivity.this);
            //设置定位资源。如果不设置此定位资源则定位按钮不可点击。
            aMap.setLocationSource(locationSource);
            //设置默认定位按钮是否显示
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationEnabled(true);
            //设置定位图片旋转的角度，从正北向开始，逆时针计算。
            aMap.setMyLocationRotateAngle(90.0f);
        }

        /*********************************************
         * 地图加载完成事件
         */
        private AMap.OnMapLoadedListener onMapLoadListener = new AMap.OnMapLoadedListener(){
            public void onMapLoaded() {
                if (aMap != null)
                    aMap.moveCamera(CameraUpdateFactory.zoomBy(aMap.getMaxZoomLevel() / 3));
            }
        };

        /*********************************************
         * 在marker上点击事件监听器
         */
        private AMap.OnInfoWindowClickListener infoWindowClickListener = new AMap.OnInfoWindowClickListener(){
            public void onInfoWindowClick(Marker marker) {

            }
        };

        /*********************************************
         * 位置改变事件通知
         */
        private ServiceLocation.OnLocationListener locationListener = new ServiceLocation.OnLocationListener(){
            public void onLocationChanged(AMapLocation aMapLocation) {
                //更新自己的位置
                if (onLocationChangedListener != null)
                    onLocationChangedListener.onLocationChanged(aMapLocation);
            }
        };

        /*********************************************
         * 定位资源
         */
        private LocationSource locationSource = new LocationSource(){
            public void activate(OnLocationChangedListener listener) {onLocationChangedListener = listener;}
            public void deactivate() {}
        };



        /******************************************************
         * 绘制动态的车辆和选中站点之间的线段
         */
        private void drawTrendsVehicleToStationLine(LatLng latlng){

        }

        /****************************************************
         * 获取由经纬度构造的LatLngBounds对象
         * @param latLngs
         * @return
         */
        private LatLngBounds getLatLngBounds(List<LatLng> latLngs){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (LatLng latLng : latLngs)
                builder.include(latLng);

            LatLngBounds bounds = builder.build();

            return bounds;
        }
    }
}
