package cn.hollo.www.features.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;

import java.util.ArrayList;
import java.util.List;

import cn.hollo.www.R;
import cn.hollo.www.UserInfo;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.features.FragmentBase;
import cn.hollo.www.features.OnRouteSearchListenerImp;
import cn.hollo.www.features.adapters.AdapterWorkDetail;
import cn.hollo.www.features.informations.ParserUtil;
import cn.hollo.www.features.informations.WorkTask;
import cn.hollo.www.features.informations.WorkTaskDetail;
import cn.hollo.www.features.params.RequestWorkTaskDetailParam;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;
import cn.hollo.www.location.ServiceLocation;

/*******************************************************
 * Created by orson on 14-11-25.
 * 工作单的详情
 */
public class FragmentWorkDetail extends FragmentBase{
    private WorkDetailList workDetailList;
    private WorkDetailMap  workDetailMap;

    public static FragmentWorkDetail newInstance(){
        return new FragmentWorkDetail();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getActivity().getActionBar();
        actionBar.setTitle("任务详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_detail, null);
        workDetailMap  = new WorkDetailMap(view);
        workDetailMap.onCreate(savedInstanceState);
        workDetailList = new WorkDetailList(view);
        workDetailList.loadWorkTaskDetail(this.getArguments());
        return view;
    }

    public void onResume() {
        super.onResume();
        workDetailMap.onResume();
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        workDetailMap.onSaveInstanceState(outState);
    }

    public void onPause() {
        super.onPause();
        workDetailMap.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        workDetailMap.onDestroy();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.getActivity().finish();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);

    }

    /***************************************************
     *  任务单详情列表
     */
    private class WorkDetailList implements OnRequestListener {
        private WorkTaskDetail workTaskDetails;
        private ListView       detailListView;

        private WorkDetailList(View view){
            detailListView = (ListView)view.findViewById(R.id.workDetailListView);
        }

        /**
         * 刷新列表
         * @param workTaskDetails
         */
        private void flushListView(WorkTaskDetail workTaskDetails){
             AdapterWorkDetail adapter = new AdapterWorkDetail(getActivity(), workTaskDetails.stations);
            detailListView.setAdapter(adapter);
        }

        /**
         * 加载数据
         */
        private void loadWorkTaskDetail(Bundle bundle){
            if (bundle == null)
                return;

            WorkTask task = (WorkTask)bundle.getSerializable("Attach");
            //如果没有WorkTask，则说明传递过程中丢失，或者没有传递
            if (task == null){
                try {
                    throw new Exception("丢失的任务数据");
                } catch (Exception e) {e.printStackTrace();}
            }
            else{
                //准备参数
                UserInfo userInfo = UserInfo.getInstance(getActivity());
                RequestWorkTaskDetailParam param = new RequestWorkTaskDetailParam();
                param.user_id = userInfo.getUserId();
                param.task_id = task.task_id;
                param.listener = this;
                //发送请求
                HttpStringRequest request = new HttpStringRequest(param);
                HttpManager httpManager = HttpManager.getInstance();
                httpManager.addRequest(request);
            }
        }

        @Override
        public void onResponse(int code, String response) {
            if (code == 200){
                workTaskDetails = ParserUtil.parserWorkTaskDetail(response);

                //刷新页面数据
                if (workTaskDetails != null){
                    this.flushListView(workTaskDetails);

                    List<WorkTaskDetail.Station> stations = workTaskDetails.stations;
                    WorkTaskDetail.Station station = null;
                    LatLng startLatLng = null;
                    LatLng endLatLng   = null;
                    List<LatLng>  otherLatLngs = stations.size() > 2 ? new ArrayList<LatLng>() : null;
                    int size = stations.size();

                    for (int i=0; i<size; i++){
                        station = stations.get(i);

                        if (i == 0)
                            startLatLng = new LatLng(station.location.lat, station.location.lng);
                        else if (i == (size - 1))
                            endLatLng = new LatLng(station.location.lat, station.location.lng);
                        else
                            otherLatLngs.add(new LatLng(station.location.lat, station.location.lng));
                    }
                    //规划路径
                    workDetailMap.planningPathLine(startLatLng, endLatLng, otherLatLngs);
                }
            }
        }
    }

    /***************************************************
     *  任务单详情地图模式
     */
    private class WorkDetailMap implements ServiceManager.OnLocaBinder {
        private MapView workMapView;
        private AMap aMap;
        private UiSettings uiSettings;
        private OnMapListener mapListener;
        private ServiceLocation.LocationBinder binder;
        private Marker busMarker;       //车辆标记
        private WorkTaskDetail.Station station;

        private WorkDetailMap(View view){
            workMapView = (MapView)view.findViewById(R.id.workDetailMapView);
            mapListener = new OnMapListener(this);
            aMap = workMapView.getMap();
            aMap.setOnMapLoadedListener(mapListener);
            setUpMap(aMap, mapListener);
            uiSettings = aMap.getUiSettings();
            //隐藏放大缩小按钮
            uiSettings.setZoomControlsEnabled(false);
            //启用地图指南针
            uiSettings.setCompassEnabled(true);
            //显示比例尺
            uiSettings.setScaleControlsEnabled(true);
            //禁止手势移动地图
            //uiSettings.setScrollGesturesEnabled(false);
            //禁止手势缩放
            //uiSettings.setZoomGesturesEnabled(false);
            //禁止手势旋转地图
            uiSettings.setRotateGesturesEnabled(true);
        }

        /*******************************************************
         * 刷新地图页面
         * @param station
         */
        private void flushMapView(WorkTaskDetail.Station station){
            this.station = station;


        }

        /*********************************************************
         * 规划路径
         * @param startLatLng
         * @param endLatLng
         * @param otherLatLngs
         */
        private void planningPathLine(LatLng startLatLng, LatLng endLatLng, List<LatLng> otherLatLngs){
            if (startLatLng == null || endLatLng == null)
                return;

            RouteSearch routeSearch = new RouteSearch(getActivity());
            routeSearch.setRouteSearchListener(mapListener);

            //组织路径的起点和终点
            LatLonPoint startPoint = new LatLonPoint(startLatLng.latitude, startLatLng.longitude);
            LatLonPoint endPoint = new LatLonPoint(endLatLng.latitude, endLatLng.longitude);
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);

            //组织途径点
            List<LatLonPoint> otherPoints = null;
            if (otherLatLngs != null){
                otherPoints = new ArrayList<LatLonPoint>();
                for (LatLng latLng : otherLatLngs)
                    otherPoints.add(new LatLonPoint(latLng.latitude, latLng.longitude));
            }

            // 第一个参数表示路径规划的起点和终点，
            // 第二个参数表示驾车模式
            // 第三个参数表示途经点，
            // 第四个参数表示避让区域，
            // 第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, otherPoints, null, "");
            routeSearch.calculateDriveRouteAsyn(query);
        }

        private void onCreate(Bundle bundle){workMapView.onCreate(bundle);}
        private void onDestroy(){workMapView.onDestroy();}
        private void onSaveInstanceState(Bundle bundle){workMapView.onSaveInstanceState(bundle);}

        /*****************************************************
         * 当该地图重新激活的时候，需要添加定位监听
         */
        private void onResume(){
            workMapView.onResume();
            ServiceManager manager = ServiceManager.getInstance();
            manager.getLocationBinder(this);
        }

        /****************************************************
         * 当地图暂停的时候，则需要取消定位监听
         */
        private void onPause(){
            workMapView.onPause();
            if (binder != null)
                binder.removeLocationListener("MapView");

            binder = null;
        }

        /****************************************************
         *
         * @param binder
         */
        public void onBinder(ServiceLocation.LocationBinder binder) {
            this.binder = binder;
            binder.addLoactionListener("MapView", mapListener);
        }
    }

    /*************************************************
     * 地图事件集合
     */
    private class OnMapListener extends OnRouteSearchListenerImp implements AMap.OnMapLoadedListener,
            ServiceLocation.OnLocationListener,
            LocationSource {

        private WorkDetailMap workDetailMap;
        private OnLocationChangedListener onLocationChangedListener;

        private OnMapListener(WorkDetailMap workDetailMap){
            this.workDetailMap = workDetailMap;
        }

        /**
         * 地图加载完成时执行
         */
        public void onMapLoaded() {
            //设置地图放大倍数
            setMapViewZoom(workDetailMap.aMap);
        }

        /**
         * 获取当前的位置信息，以便于更新地图
         * @param aMapLocation
         */
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (onLocationChangedListener != null)
                onLocationChangedListener.onLocationChanged(aMapLocation);


            LatLng latlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        }

        /*************************************************
         * 激活自动定位
         * @param listener
         */
        public void activate(LocationSource.OnLocationChangedListener listener) {
            if (onLocationChangedListener == null)
                this.onLocationChangedListener = listener;

            setMapViewZoom(workDetailMap.aMap);
        }

        /*************************************************
         *
         */
        public void deactivate() {
            onLocationChangedListener = null;
        }

        /*************************************************
         * 驾车路径规划结果
         * @param result
         * @param code
         */
        public void onDriveRouteSearched(DriveRouteResult result, int code) {
            if (code != 0)
                return;

            if(result != null && result.getPaths() != null && result.getPaths().size() > 0){
                //清理之前的图标
                workDetailMap.aMap.clear();
                DrivePath drivePath = result.getPaths().get(0);

                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        getActivity(),
                        workDetailMap.aMap,
                        drivePath,
                        result.getStartPos(),
                        result.getTargetPos());

                drivingRouteOverlay.setNodeIconVisibility(false);
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            }
        }
    }

    /******************************************************
     * 设置地图的放大倍数
     * @param aMap
     */
    private void setMapViewZoom(AMap aMap){
        //设置地图的放大倍数
        float zoom = aMap.getMaxZoomLevel() * 0.7f;
        aMap.moveCamera(CameraUpdateFactory.zoomBy(zoom));
    }

    /*****************************************************
     * 生成一个MarkerOptions对象
     * @param icon
     * @return
     */
    private MarkerOptions createMarkerOptions(int icon){
        MarkerOptions options = new MarkerOptions();
        //定义marker 图标的锚点为中心点
        options.anchor(0.5f, 0.5f);
        //设置标记是禁止拖动
        options.draggable(false);
        //添加该marker的icon
        options.icon(BitmapDescriptorFactory.fromResource(icon));
        //首先禁止显示
        options.visible(false);
        return options;
    }

    /*****************************************************
     * 构造地图定位图形
     * @param aMap
     * @param ls
     */
    private void setUpMap(AMap aMap, LocationSource ls) {
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
        aMap.setLocationSource(ls);
        //设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        //设置定位图片旋转的角度，从正北向开始，逆时针计算。
        aMap.setMyLocationRotateAngle(90.0f);
    }
}
