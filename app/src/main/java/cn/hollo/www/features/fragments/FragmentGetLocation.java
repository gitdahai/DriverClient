package cn.hollo.www.features.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

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
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

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
    private SearchAddress searchAddress;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.confirm, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.confirm){
            if (choiceLocation != null)
                choiceLocation.onConfirm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (searchAddress != null)
            searchAddress.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);


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
        //在actionbar上，增加自定义试图
        ActionBar actionBar = getActivity().getActionBar();
        View actionbarView = View.inflate(getActivity(), R.layout.actionbar_input, null);
        actionBar.setCustomView(actionbarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);
        searchAddress = new SearchAddress(actionbarView);

        //主体试图
        View view = inflater.inflate(R.layout.fragment_location_map, null);
        Bundle mBundle = getArguments();
        LocationInfo locationInfo = (LocationInfo)mBundle.getSerializable("LocationInfo");
        choiceLocation = new ChoiceLocation(view, locationInfo);
        choiceLocation.mapView.onCreate(savedInstanceState);
        return view;
    }

    /************************************************************
     * 搜索
     */
    private class SearchAddress{
        protected static final int RESULT_SPEECH = 1;
        private ImageView   voiceInput;
        private AutoCompleteTextView searchText;
        private List<String> listString;
        private ArrayAdapter aAdapter;
        private Inputtips inputTips;

        /**===================================================
         * 构造
         * @param view
         */
        private SearchAddress(View view){
            //初始化Actionbar试图
            voiceInput   = (ImageView)view.findViewById(R.id.voiceInput);
            voiceInput.setEnabled(false);
            voiceInput.setOnClickListener(clickListener);

            //文本搜索
            searchText = (AutoCompleteTextView)view.findViewById(R.id.searchText);
            searchText.addTextChangedListener(textWatcher);
            searchText.setVisibility(View.VISIBLE);
            searchText.setOnItemClickListener(itemClickListener);
            searchText.setEnabled(false);

            // 输入信息的回调方法
            inputTips = new Inputtips(getActivity(), inputtipsListener);
        }

        /**===================================================
         *
         */
        private void enable(){
            voiceInput.setEnabled(true);
            searchText.setEnabled(true);
        }

        /**===================================================
         *
         */
        private TextWatcher textWatcher = new TextWatcher(){
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String newText = s.toString();

                    // 发送输入提示请求
                    // 第一个参数表示提示关键字，
                    // 第二个参数默认代表全国，也可以为城市区号
                    inputTips.requestInputtips(newText, choiceLocation.cityCode);
                    // 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
                    //inputTips.requestInputtips(newText, editCity.getText().toString());
                } catch (com.amap.api.services.core.AMapException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        /**======================================================
         *
         */
        private Inputtips.InputtipsListener inputtipsListener = new Inputtips.InputtipsListener(){
            public void onGetInputtips(List<Tip> tips, int rCode) {
                listString = new ArrayList<String>();

                for (Tip tip : tips)
                    listString.add(tip.getName());

                aAdapter = new ArrayAdapter(getActivity(), R.layout.route_inputs, listString);
                searchText.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();
            }
        };

        /**=====================================================
         * 关键字选择事件
         */
        private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listString == null || listString.size() <= position)
                    return;

                GeocodeSearch geocoderSearch = new GeocodeSearch(getActivity());
                geocoderSearch.setOnGeocodeSearchListener(choiceLocation.geocodeSearchListener);
                // 第一个参数表示地址，
                // 第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
                GeocodeQuery query = new GeocodeQuery(listString.get(position), choiceLocation.cityCode);
                geocoderSearch.getFromLocationNameAsyn(query);
            }
        };

        /**=====================================================
         * 语音输入按钮
         */
        private View.OnClickListener clickListener = new View.OnClickListener(){
            public void onClick(View v) {

            }
        };

        /**=================================================
         *
         * @param requestCode
         * @param resultCode
         * @param data
         */
        private void onActivityResult(int requestCode, int resultCode, Intent data){
            if (resultCode == Activity.RESULT_OK && null != data) {

                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                searchText.setText(text.get(0));
            }
        }
    }

    /************************************************************
     * 选择位置坐标
     */
    private class ChoiceLocation {
        private MapView mapView;
        private ImageView locationSelectedBtn;
        private AMap aMap;
        private UiSettings uiSettings;
        private LocationSource.OnLocationChangedListener locationChangedListener;
        private LocationInfo locationInfo;

        private String cityCode = "";  //搜索关键字
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
            locationSelectedBtn = (ImageView)view.findViewById(R.id.locationSelectedBtn);
            locationSelectedBtn.setVisibility(View.VISIBLE);
            locationSelectedBtn.setEnabled(false);
            locationSelectedBtn.setOnClickListener(locationSelectedClick);

            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();

            //设置当地图加载完成后的事件监听器
            aMap.setOnMapLoadedListener(onMapLoadListener);
            //添加镜头移动监听器
            aMap.setOnCameraChangeListener(cameraChangeListener);
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

        /**=================================================
         * 定位按钮事件
         */
        private View.OnClickListener locationSelectedClick = new View.OnClickListener(){
            public void onClick(View v) {
            LatLng latLng = new LatLng(selfLat, delfLng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, aMap.getMaxZoomLevel() * 0.8f));
            }
        };

        /**==================================================
         * 确认选择当前的位置
         */
        public void onConfirm(){
            Intent intent = new Intent();
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("LocationInfo", locationInfo);
            intent.putExtras(mBundle);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }

        /**==================================================
         * 初始化当前的marker
         */
        private void initSelfMarker(){
            MarkerOptions options = createMarkerOptions(R.drawable.marker_icon, "正在获取位置", "", aMap.getCameraPosition().target);
            selfMarker = aMap.addMarker(options);
            selfMarker.setVisible(true);
        }

        /*********************************************
         * 地图加载完成事件
         */
        private AMap.OnMapLoadedListener onMapLoadListener = new AMap.OnMapLoadedListener(){
            public void onMapLoaded() {
            if (aMap != null){
                aMap.moveCamera(CameraUpdateFactory.zoomBy(aMap.getMaxZoomLevel() / 3));
            }
            }
        };

        /*********************************************
         * 位置改变事件通知
         */
        private ServiceLocation.OnLocationListener locationListener = new ServiceLocation.OnLocationListener(){
            public void onLocationChanged(AMapLocation aMapLocation) {
                cityCode = aMapLocation.getCityCode();

                //保存当前的位置
                selfLat = aMapLocation.getLatitude();
                delfLng = aMapLocation.getLongitude();

                if (!isLocationed){
                    isLocationed = true;
                    LatLng latLng = new LatLng(selfLat, delfLng);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, aMap.getMaxZoomLevel() * 0.8f));
                    geocoderSearch(latLng);
                    //启用定位按钮
                    locationSelectedBtn.setEnabled(true);
                    //启用anctionbar功能
                    searchAddress.enable();
                }
            }
        };

        /*****************************************************
         * 当地图移动时的监听器对象
         * 只有手动移动地图时才会触发该事件
         */
        private AMap.OnCameraChangeListener cameraChangeListener = new AMap.OnCameraChangeListener(){
            public void onCameraChange(CameraPosition cameraPosition) {
                selfMarker.hideInfoWindow();
                selfMarker.setPosition(cameraPosition.target);
            }

            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                locationInfo.lat = cameraPosition.target.latitude;
                locationInfo.lng = cameraPosition.target.longitude;
                selfMarker.setPosition(cameraPosition.target);
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
            /**-----------------------------------------------
             *
             * @param result
             * @param rCode
             */
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if(rCode == 0){
                    if(result != null && result.getRegeocodeAddress() != null){
                        RegeocodeAddress ra = result.getRegeocodeAddress();
                        /*System.out.println("CityCode===" + ra.getCityCode());
                        System.out.println("Province===" + ra.getProvince());
                        System.out.println("City===" + ra.getCity());
                        System.out.println("AdCode===" + ra.getAdCode());
                        System.out.println("Building===" + ra.getBuilding());
                        System.out.println("District===" + ra.getDistrict());
                        System.out.println("FormatAddress===" + ra.getFormatAddress());
                        System.out.println("Neighborhood===" + ra.getNeighborhood());
                        System.out.println("Township===" + ra.getTownship());*/

                        if (ra.getFormatAddress() != null){
                            String addressName = result.getRegeocodeAddress().getFormatAddress();

                            locationInfo.description = addressName;
                            selfMarker.setTitle(addressName);
                            selfMarker.showInfoWindow();
                        }
                    }
                }
            }

            /**=---------------------------------------------
             *
             * @param geocodeResult
             * @param rCode
             */
            public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
                if (rCode != 0 || geocodeResult == null || geocodeResult.getGeocodeAddressList() == null)
                    return;

                String locationName = null;
                GeocodeQuery geocodeQuery = geocodeResult.getGeocodeQuery();
                //获取查询时的（关键字）
                if (geocodeQuery != null)
                    locationName = geocodeQuery.getLocationName();

                //获取查询到的地址列表
                List<GeocodeAddress> addresses = geocodeResult.getGeocodeAddressList();

                if (addresses.size() > 0){
                    GeocodeAddress address = addresses.get(0);
                    LatLonPoint latLonPoint = address.getLatLonPoint();
                    locationInfo.lat = latLonPoint.getLatitude();
                    locationInfo.lng = latLonPoint.getLongitude();

                    //移动camera到选择的位置上
                    LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, aMap.getMaxZoomLevel() * 0.8f));

                    //取出地址信息
                    if (locationName == null)
                        locationName = address.getFormatAddress();
                }

                //保存名称，以及显示新的title
                locationInfo.description = locationName;
                selfMarker.setTitle(locationName);
                selfMarker.showInfoWindow();
            }
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
            //options.snippet(snippet);
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
