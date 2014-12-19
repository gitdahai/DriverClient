package cn.hollo.www.features.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
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
    public void onInitMission(List<StationInfo.Station> stations) {
        if (missionMap == null || stations == null || stations.size() == 0)
            return;

        //绘制站点的markers
        missionMap.drawStationMarkers(stations);
    }

    @Override
    public void onStartMission(StationInfo.Station  station) {
        if (missionMap == null || station == null)
            return;

        //保存站点的位置坐标数据
        missionMap.stationLat = station.location.lat;
        missionMap.stationLng = station.location.lng;
        //绘制车辆--站点连线
        missionMap.drawDottedLine();

        //显示上下车人数
        missionMap.onBusPopulationText.setVisibility(View.VISIBLE);
        missionMap.offBusPopulationText.setVisibility(View.VISIBLE);
        
        //显示上下车人数
        missionMap.showPassengers(station.on_users.size(), station.off_users.size());
    }

    @Override
    public void onArrivingStation(StationInfo.Station station) {
        if (missionMap == null || station == null)
            return;
    }

    @Override
    public void onNextArrivingStation(StationInfo.Station station) {
        if (missionMap == null || station == null)
            return;

        missionMap.stationLat = station.location.lat;
        missionMap.stationLng = station.location.lng;

        //绘制车辆--站点连线
        missionMap.drawDottedLine();

        //显示上下车人数
        missionMap.showPassengers(station.on_users.size(), station.off_users.size());
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
        private Polyline   polyline;
        private LocationSource.OnLocationChangedListener onLocationChangedListener;


        private double stationLat;  //站点当前的维度
        private double stationLng;  //站点当前的经度
        private double vehicleLat;  //车辆的当前维度
        private double vehicleLng;  //车辆的当前经度

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

                //保存当前的经纬度
                vehicleLat = aMapLocation.getLatitude();
                vehicleLng = aMapLocation.getLongitude();

                //绘制车辆和站点位置之间的虚线
                drawDottedLine();
            }
        };

        /*********************************************
         * 定位资源
         */
        private LocationSource locationSource = new LocationSource(){
            public void activate(OnLocationChangedListener listener) {onLocationChangedListener = listener;}
            public void deactivate() {}
        };

        /*********************************************
         * 显示上下车人数
         * @param on    ：上车人数
         * @param off   ：下车人数
         */
        private void showPassengers(int on, int off){
            onBusPopulationText.setText("上车\n" + on + "人");
            offBusPopulationText.setText("下车\n" + off + "人");
        }

        /****************************************************
         * 绘制动态的车辆和选中站点之间的线段
         */
        private void drawDottedLine(){
            if (vehicleLat < 1 || vehicleLng < 1 || stationLat < 1 || stationLng < 1)
                return;

            LatLng stationLatLng = new LatLng(stationLat, stationLng);
            LatLng vehicleLatLng = new LatLng(vehicleLat, vehicleLng);

            //如果连线对象不为null，则需要移除上一次的绘制结果
            if (polyline != null)
                polyline.remove();

            PolylineOptions pOption = new PolylineOptions();
            pOption.add(vehicleLatLng, stationLatLng);
            pOption.width(5.0f);
            //pOption.color(resources.getColor(R.color.color_red));
            pOption.setDottedLine(true);
            pOption.geodesic(true);
            pOption.visible(true);

            polyline = aMap.addPolyline(pOption);

            //缩放图层
            List<LatLng> latLngs = new ArrayList<LatLng>();
            latLngs.add(vehicleLatLng);
            latLngs.add(stationLatLng);
            zoomLatpngBounds(latLngs);
        }


        /****************************************************
         * 绘制站点marker标记
         * @param stations
         */
        private void drawStationMarkers(List<StationInfo.Station> stations){
            int size = stations.size();
            ArrayList<MarkerOptions> options = new ArrayList<MarkerOptions>();
            StationInfo.Station station = null;
            MarkerOptions option = null;
            LatLng latLng = null;

            for (int i=0; i<size; i++){
                station = stations.get(i);
                latLng = new LatLng(station.location.lat, station.location.lng);
                //绘制起点站点marker
                if (i == 0)
                    option = createMarkerOptions(R.drawable.ic_station_start, station.name, latLng);
                //绘制终点站点marker
                else if (i == size-1)
                    option = createMarkerOptions(R.drawable.ic_station_end, station.name, latLng);
                //绘制途径站点merker
                else
                    option = createMarkerOptions(R.drawable.ic_station_other, station.name, latLng);

                options.add(option);
            }

            aMap.addMarkers(options, true);
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

        /*****************************************************
         * 生成一个MarkerOptions对象
         * @param icon
         * @return
         */
        private MarkerOptions createMarkerOptions(int icon, String title, LatLng latlng){
            MarkerOptions options = new MarkerOptions();
            //定义marker 图标的锚点为中心点
            options.anchor(0.5f, 1.0f);
            //设置标记是禁止拖动
            options.draggable(false);
            //添加该marker的icon
            options.icon(BitmapDescriptorFactory.fromResource(icon));
            //当用户点击标记，在信息窗口上显示的字符串
            options.title(title);
            //首先禁止显示
            options.visible(true);
            //设置位置参数
            options.position(latlng);
            //设置标记的近大远小效果，在marker初始化时使用。
            // 当地图倾斜时，远方的标记变小，附近的标记变大
            options.perspective(true);
            return options;
        }

        /****************************************************
         * 缩放当前的图层
         * @param latLngs
         */
        private void zoomLatpngBounds(List<LatLng> latLngs){
            LatLngBounds bounds = getLatLngBounds(latLngs);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
            aMap.moveCamera(cameraUpdate);
        }
    }
}
