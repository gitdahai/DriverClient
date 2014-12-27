package cn.hollo.www.features.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import cn.hollo.www.features.activities.ActivityLocationMap;
import cn.hollo.www.location.ServiceLocation;

/****************************************************************
 * Created by orson on 14-12-27.
 * 显示位置信息的fragment
 */
public class FragmentShowLocation extends Fragment {
    private ShowLocation showLocation;
    private ServiceLocation.LocationBinder locationBinder;

    public void onDestroy(){
        if (showLocation != null)
            showLocation.mapView.onDestroy();

        locationBinder = null;
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle bundle){
        if (showLocation != null)
            showLocation.mapView.onSaveInstanceState(bundle);
    }

    public void onResume(){
        if (showLocation != null)
            showLocation.mapView.onResume();

        //如果locationBinder对象不存在，则需要获取
        if (locationBinder == null){
            ServiceManager serviceManager = ServiceManager.getInstance(getActivity());
            serviceManager.getLocationBinder(locaBinder);
        }
        super.onResume();
    }

    public void onPause(){
        if (showLocation != null)
            showLocation.mapView.onPause();

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (locationBinder != null)
            locationBinder.removeLocationListener("FragmentShowLocation");
    }

    /************************************************************
     * 绑定得到LocationBinder
     */
    private ServiceManager.OnLocaBinder locaBinder = new ServiceManager.OnLocaBinder(){
        public void onBinder(ServiceLocation.LocationBinder binder) {
            locationBinder = binder;

            if (showLocation != null)
                binder.addLoactionListener("FragmentShowLocation", showLocation.locationListener);
        }
    };

    /************************************************************
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_map, null);
        Bundle mBundle = getArguments();
        ActivityLocationMap.LocationInfo locationInfo = (ActivityLocationMap.LocationInfo)mBundle.getSerializable("LocationInfo");
        showLocation = new ShowLocation(view, locationInfo);
        showLocation.mapView.onCreate(savedInstanceState);
        return view;
    }

    /************************************************************
     * 显示位置
     */
    private class ShowLocation{
        private MapView mapView;
        private AMap aMap;
        private UiSettings uiSettings;
        private LocationSource.OnLocationChangedListener locationChangedListener;
        private ActivityLocationMap.LocationInfo locationInfo;

        private Polyline polyline;
        private LatLng userLatLng;
        private double selfLat;
        private double delfLng;

        /**=====================================================
         *
         * @param view
         */
        private ShowLocation(View view, ActivityLocationMap.LocationInfo locationInfo){
            this.locationInfo = locationInfo;

            if (locationInfo.lat > 1 && locationInfo.lng > 1)
                userLatLng = new LatLng(locationInfo.lat, locationInfo.lng);

            mapView = (MapView)view.findViewById(R.id.locationMapView);
            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();

            //隐藏放大缩小按钮
            uiSettings.setZoomControlsEnabled(true);
            //启用地图指南针
            uiSettings.setCompassEnabled(true);
            //显示比例尺
            uiSettings.setScaleControlsEnabled(true);
            //禁止手势旋转地图
            uiSettings.setRotateGesturesEnabled(true);
            //设置自己当前的定位样式和显示位置
            setUpMap();
            //添加用户的标记到地图上
            addUserMarker();
        }

        /**===================================================
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

        /**===================================================
         * 在地图上添加用户的标记
         */
        private void addUserMarker(){
            if (locationInfo == null || locationInfo.description == null || userLatLng == null)
                return;

            String title = "我在这里";
            String snippet = locationInfo.description;
            MarkerOptions options = createMarkerOptions(R.drawable.marker_icon, title, snippet, userLatLng);
            Marker marker = aMap.addMarker(options);
            marker.showInfoWindow();
        }

        /**==================================================
         * 定位资源
         */
        private LocationSource locationSource = new LocationSource(){
            public void activate(OnLocationChangedListener listener) {locationChangedListener = listener;}
            public void deactivate() {}
        };

        /*********************************************
         * 位置改变事件通知
         */
        private ServiceLocation.OnLocationListener locationListener = new ServiceLocation.OnLocationListener(){
            public void onLocationChanged(AMapLocation aMapLocation) {
                //更新自己的位置
                if (locationChangedListener != null)
                    locationChangedListener.onLocationChanged(aMapLocation);

                //保存当前的位置
                selfLat = aMapLocation.getLatitude();
                delfLng = aMapLocation.getLongitude();
                //绘制自己与用户的之间的连线　
                drawUserWithSelfLine();
            }
        };

        /****************************************************
         * 绘制动态的车辆和选中站点之间的线段
         */
        private void drawUserWithSelfLine(){
            if (userLatLng == null || selfLat < 1 || delfLng < 1)
                return;

            LatLng selfLatLng = new LatLng(selfLat, delfLng);

            //如果连线对象不为null，则需要移除上一次的绘制结果
            if (polyline != null)
                polyline.remove();

            PolylineOptions pOption = new PolylineOptions();
            pOption.add(userLatLng, selfLatLng);
            pOption.width(5.0f);
            //pOption.color(resources.getColor(R.color.color_red));
            pOption.setDottedLine(true);
            pOption.geodesic(true);
            pOption.visible(true);

            polyline = aMap.addPolyline(pOption);

            //缩放图层
            List<LatLng> latLngs = new ArrayList<LatLng>();
            latLngs.add(userLatLng);
            latLngs.add(selfLatLng);
            zoomLatpngBounds(latLngs);
        }

        /****************************************************
         * 缩放当前的图层
         * @param latLngs
         */
        private void zoomLatpngBounds(List<LatLng> latLngs){
            LatLngBounds bounds = getLatLngBounds(latLngs);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200);
            aMap.moveCamera(cameraUpdate);
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
        private MarkerOptions createMarkerOptions(int icon, String title, String snippet,LatLng latlng){
            MarkerOptions options = new MarkerOptions();
            //定义marker 图标的锚点为中心点
            options.anchor(0.5f, 1.0f);
            //设置标记是禁止拖动
            options.draggable(false);
            //添加该marker的icon
            options.icon(BitmapDescriptorFactory.fromResource(icon));
            //当用户点击标记，在信息窗口上显示的字符串
            options.title(title);
            //描述片段
            options.snippet(snippet);
            //首先禁止显示
            options.visible(true);
            //设置位置参数
            options.position(latlng);
            //设置标记的近大远小效果，在marker初始化时使用。
            // 当地图倾斜时，远方的标记变小，附近的标记变大
            options.perspective(true);
            return options;
        }
    }
}
