package cn.hollo.www.features.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import cn.hollo.www.R;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.features.informations.LocationInfo;
import cn.hollo.www.location.ServiceLocation;

/**
 * Created by orson on 14-12-27.
 * 获取位置信息
 */
public class FragmentGetLocation extends Fragment {
    private ChoiceLocation choiceLocation;
    private ServiceLocation.LocationBinder locationBinder;

    public void onDestroy(){
        if (choiceLocation != null)
            choiceLocation.mapView.onDestroy();

        locationBinder = null;
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle bundle){
        if (choiceLocation != null)
            choiceLocation.mapView.onSaveInstanceState(bundle);
    }

    public void onResume(){
        if (choiceLocation != null)
            choiceLocation.mapView.onResume();

        //如果locationBinder对象不存在，则需要获取
        if (locationBinder == null){
            ServiceManager serviceManager = ServiceManager.getInstance(getActivity());
            serviceManager.getLocationBinder(locaBinder);
        }
        super.onResume();
    }

    public void onPause(){
        if (choiceLocation != null)
            choiceLocation.mapView.onPause();

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (locationBinder != null)
            locationBinder.removeLocationListener("FragmentGetLocation");
    }

    /************************************************************
     * 绑定得到LocationBinder
     */
    private ServiceManager.OnLocaBinder locaBinder = new ServiceManager.OnLocaBinder(){
        public void onBinder(ServiceLocation.LocationBinder binder) {
            locationBinder = binder;

            if (choiceLocation != null)
                binder.addLoactionListener("FragmentGetLocation", choiceLocation.locationListener);
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
        LocationInfo locationInfo = (LocationInfo)mBundle.getSerializable("LocationInfo");
        choiceLocation = new ChoiceLocation(view, locationInfo);
        choiceLocation.mapView.onCreate(savedInstanceState);
        return view;
    }

    /************************************************************
     * 选择位置坐标
     */
    private class ChoiceLocation{
        private MapView mapView;
        private AMap aMap;
        private UiSettings uiSettings;
        private LocationSource.OnLocationChangedListener locationChangedListener;
        private LocationInfo locationInfo;

        private Marker selfMarker;
        private double selfLat;
        private double delfLng;
        private boolean isLocationed;   //是否已经定过位了
        /********************************************************
         *
         * @param view
         * @param locationInfo
         */
        private ChoiceLocation(View view, LocationInfo locationInfo){
            if (locationInfo != null)
                this.locationInfo = locationInfo;
            else
                this.locationInfo = new LocationInfo();

            mapView = (MapView)view.findViewById(R.id.locationMapView);
            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();

            //设置当地图加载完成后的事件监听器
            aMap.setOnMapLoadedListener(onMapLoadListener);
            //添加镜头移动监听器
            aMap.setOnCameraChangeListener(cameraChangeListener);
            //设置定位层是否显示。
            //aMap.setMyLocationEnabled(true);
            //启用定位按钮
            //uiSettings.setMyLocationButtonEnabled(true);
            //隐藏放大缩小按钮
            uiSettings.setZoomControlsEnabled(true);
            //启用地图指南针
            uiSettings.setCompassEnabled(true);
            //显示比例尺
            uiSettings.setScaleControlsEnabled(true);
            //禁止手势旋转地图
            uiSettings.setRotateGesturesEnabled(true);

            //初始哈自己的marker标记
            initSelfMarker();
        }

        /**==================================================
         * 初始化当前的marker
         */
        private void initSelfMarker(){
            MarkerOptions options = createMarkerOptions(R.drawable.marker_icon, "", "", aMap.getCameraPosition().target);
            selfMarker = aMap.addMarker(options);
            selfMarker.setVisible(true);
            selfMarker.showInfoWindow();
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
         * 位置改变事件通知
         */
        private ServiceLocation.OnLocationListener locationListener = new ServiceLocation.OnLocationListener(){
            public void onLocationChanged(AMapLocation aMapLocation) {
                //保存当前的位置
                selfLat = aMapLocation.getLatitude();
                delfLng = aMapLocation.getLongitude();

                if (!isLocationed){
                    isLocationed = true;
                    LatLng latLng = new LatLng(selfLat, delfLng);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, aMap.getMaxZoomLevel() * 0.8f));
                }
            }
        };

        /*****************************************************
         * 当地图移动时的监听器对象
         */
        private AMap.OnCameraChangeListener cameraChangeListener = new AMap.OnCameraChangeListener(){
            public void onCameraChange(CameraPosition cameraPosition) {
                selfMarker.hideInfoWindow();
                selfMarker.setPosition(cameraPosition.target);
            }

            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                locationInfo.lat = cameraPosition.target.latitude;
                locationInfo.lng = cameraPosition.target.longitude;
                geocoderSearch(cameraPosition.target);
            }
        };

        /**==================================================
         * 定位资源
         */
        private LocationSource locationSource = new LocationSource(){
            public void activate(OnLocationChangedListener listener) {locationChangedListener = listener;}
            public void deactivate() {}
        };

        /**==================================================
         * 根据坐标查询位置信息
         * @param target
         */
        private void geocoderSearch(LatLng target){
            GeocodeSearch geocoderSearch = new GeocodeSearch(getActivity());
            geocoderSearch.setOnGeocodeSearchListener(geocodeSearchListener);
            // 第一个参数表示一个Latlng，
            // 第二参数表示范围多少米，
            // 第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(target.latitude, target.longitude), 50, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        }

        /**===================================================
         * 查询到的位置信息，在这里返回
         */
        private GeocodeSearch.OnGeocodeSearchListener geocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener(){
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if(rCode == 0){
                    if(result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null){
                        String addressName = result.getRegeocodeAddress().getFormatAddress();

                        selfMarker.showInfoWindow();
                        selfMarker.setSnippet(addressName);
                        locationInfo.description = addressName;
                    }
                }
            }

            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {}
        };

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
