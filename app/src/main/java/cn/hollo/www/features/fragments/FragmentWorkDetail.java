package cn.hollo.www.features.fragments;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import cn.hollo.www.utils.Util;

/*******************************************************
 * Created by orson on 14-11-25.
 * 工作单的详情
 */
public class FragmentWorkDetail extends FragmentBase{
    /*********************************************************
     * 任务的状态
     */
    public interface TaskExcuteState{
        //状态为“未开始”
        public static final int TASK_STATE_NONE = -1;
        //状态为"进行中"
        public static final int TASK_STATE_EXCUTE = 1;
        //状态为"结束"
        public static final int TASK_STATE_FINISH = 2;
    }

    private WorkDetailList workDetailList;
    private WorkDetailMap  workDetailMap;
    private Resources resources;

    public static FragmentWorkDetail newInstance(){
        return new FragmentWorkDetail();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getActivity().getActionBar();
        actionBar.setTitle("任务详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setHasOptionsMenu(true);

        resources = getResources();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_work_detail, null);
        workDetailMap  = new WorkDetailMap(view);
        workDetailMap.onCreate(savedInstanceState);
        workDetailList = new WorkDetailList(view, workDetailMap);
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

    @Override
    public void onStop() {
        super.onStop();
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
    private class WorkDetailList implements OnRequestListener, View.OnClickListener, AdapterWorkDetail.OnStateListener {
        private WorkTaskDetail workTaskDetails;
        private WorkDetailMap  workDetailMap;
        private ListView       detailListView;
        private AdapterWorkDetail adapter;
        private Button         startDoTaskButton;   //任务开始按钮

        private String task_id;
        private int taskExcuteState;
        private int executionIndex;

        private WorkDetailList(View view, WorkDetailMap  workDetailMap){
            this.workDetailMap = workDetailMap;

            detailListView = (ListView)view.findViewById(R.id.workDetailListView);
            startDoTaskButton = (Button)view.findViewById(R.id.startDoTaskButton);
            startDoTaskButton.setOnClickListener(this);
            startDoTaskButton.setEnabled(false);

            //读取任务执行的状态
            TaskExecutionInfo taskExecutionInfo = TaskExecutionInfo.getInstance(getActivity());
            task_id = taskExecutionInfo.getTaskId();
            executionIndex = taskExecutionInfo.getExecutionIndex();
            taskExcuteState = taskExecutionInfo.getTaskExcuteState();
        }

        /**
         * 刷新列表
         * @param workTaskDetails
         */
        private void flushListView(WorkTaskDetail workTaskDetails){
            adapter = new AdapterWorkDetail(getActivity(), workTaskDetails.stations);
            adapter.setOnStateListener(this);
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
                if (workTaskDetails != null && workTaskDetails.stations.size() > 0){
                    //刷新任务列表
                    this.flushListView(workTaskDetails);
                    //绘制站点的marker
                    workDetailMap.drawStationMarkers(workTaskDetails.stations);

                    //启用任务按钮
                    if (task_id == null)
                        startDoTaskButton.setEnabled(true);
                    //如果当前任务详情的id等于记录的任务id，
                    //则说明是已经执行过的任务，那么需要回复上一次执行的位置
                    else if (task_id.equals(workTaskDetails.task_id))
                        adapter.actionInit(executionIndex);
                }
            }
        }

        @Override
        public void onClick(View v) {
            Util.creteDefaultDialog(getActivity(), "确认", "确认开始任务?", "确定", "取消", null, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    //确认开始
                    if (DialogInterface.BUTTON_POSITIVE == which){
                        //开始新任务的时候，首先保存新任务的id
                        TaskExecutionInfo taskExecutionInfo = TaskExecutionInfo.getInstance(getActivity());
                        taskExecutionInfo.putTaskId(workTaskDetails.task_id);
                        //设置任务初始执行时的状态
                        startDoTaskButton.setEnabled(false);
                        adapter.actionInit(0);
                    }
                }
            });
        }

        @Override
        public void onActionInit(WorkTaskDetail.Station station) {
            workDetailMap.setCurrentStation(station);
            System.out.println("==========已经初始化===============");
        }

        @Override
        public void onActionNext(WorkTaskDetail.Station station) {
            workDetailMap.setCurrentStation(station);
            System.out.println("==========下一个===============");
        }

        @Override
        public void onActionItemClick(WorkTaskDetail.Station station) {
            workDetailMap.setCurrentStation(station);
        }

        @Override
        public void onActionFinish(WorkTaskDetail.Station station) {
            System.out.println("==========已经完成===============");
        }
    }

    /***************************************************
     *  任务单详情地图模式
     */
    private class WorkDetailMap implements ServiceManager.OnLocaBinder {
        private MapView workMapView;
        private TextView onBusPopulationText;
        private TextView offBusPopulationText;

        private AMap aMap;
        private UiSettings uiSettings;
        private OnMapListener mapListener;
        private ServiceLocation.LocationBinder binder;
        private WorkTaskDetail.Station station;
        private Polyline pLine;

        private WorkDetailMap(View view){
            workMapView = (MapView)view.findViewById(R.id.workDetailMapView);
            onBusPopulationText = (TextView)view.findViewById(R.id.onBusPopulationText);
            offBusPopulationText = (TextView)view.findViewById(R.id.offBusPopulationText);
            setSGPopulationText(false);

            mapListener = new OnMapListener(this);
            aMap = workMapView.getMap();
            aMap.setOnMapLoadedListener(mapListener);
            aMap.setOnInfoWindowClickListener(mapListener);
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

        /*************************************************
         * 设置显示上下车人数的文本显示或者隐藏
         * @param b
         */
        private void setSGPopulationText(boolean b){
            if (b){
                onBusPopulationText.setVisibility(View.VISIBLE);
                offBusPopulationText.setVisibility(View.VISIBLE);
            }
            else{
                onBusPopulationText.setVisibility(View.GONE);
                offBusPopulationText.setVisibility(View.GONE);
            }
        }

        /**
         * 设置当前的站点
         * @param station
         */
        private void setCurrentStation(WorkTaskDetail.Station station){
            if (station == null)
                return;

            this.station = station;
            LatLng startLatlng = new LatLng(station.location.lat,station.location.lng);
            drawBusToStationLine(startLatlng, mapListener.positionLatlng);

            int onPopulation = station.on_users.size();
            int offPopulation = station.off_users.size();

            setSGPopulationText(true);
            onBusPopulationText.setText("上车\n" + onPopulation + "人");
            offBusPopulationText.setText("下车\n" + offPopulation + "人");
        }

        /******************************************************
         * 绘制动态的bus和选中站点之间的线段
         */
        private void drawTrendsBusToStationLine(LatLng busPosLatlng){
            if (busPosLatlng != null && station != null && station.location != null){
                LatLng startLatlng = new LatLng(station.location.lat,station.location.lng);
                drawBusToStationLine(startLatlng, busPosLatlng);
            }
        }

        /******************************************************
         * 绘制当前bus和站点之间的直线
         * @param startLatlng
         * @param endLatlng
         */
        private void drawBusToStationLine(LatLng startLatlng, LatLng endLatlng){
            if (mapListener.positionLatlng != null){
                if (pLine != null)
                    pLine.remove();

                PolylineOptions pOption = new PolylineOptions();
                pOption.add(startLatlng, endLatlng);
                pOption.width(5.0f);
                pOption.color(resources.getColor(R.color.color_red));
                pOption.setDottedLine(true);
                pOption.geodesic(true);
                pOption.visible(true);

                pLine = aMap.addPolyline(pOption);

                //缩放图层
                List<LatLng> latLngs = new ArrayList<LatLng>();
                latLngs.add(startLatlng);
                latLngs.add(endLatlng);
                zoomLatpngBounds(latLngs);
            }
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
        /*******************************************************
         * 显示站点的标志
         * @param stations
         */
        private void drawStationMarkers(List<WorkTaskDetail.Station> stations){
            int size = stations.size();
            ArrayList<MarkerOptions> options = new ArrayList<MarkerOptions>();
            WorkTaskDetail.Station station = null;
            MarkerOptions option = null;
            LatLng latLng = null;

            for (int i=0; i<size; i++){
                station = stations.get(i);
                latLng = new LatLng(station.location.lat, station.location.lng);

                if (i == 0)
                    option = createMarkerOptions(R.drawable.ic_path_start, station.name, latLng);
                else if (1 == size-1)
                    option = createMarkerOptions(R.drawable.ic_path_end, station.name, latLng);
                else
                    option = createMarkerOptions(R.drawable.ic_path_end, station.name, latLng);

                options.add(option);
            }

            List<Marker> markers = workDetailMap.aMap.addMarkers(options, true);

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
            AMap.OnInfoWindowClickListener,
            LocationSource {

        private WorkDetailMap workDetailMap;
        private OnLocationChangedListener onLocationChangedListener;
        private LatLng positionLatlng;

        private OnMapListener(WorkDetailMap workDetailMap){
            this.workDetailMap = workDetailMap;
        }

        /**
         * 地图加载完成时执行
         */
        public void onMapLoaded() {

        }

        /**
         * 获取当前的位置信息，以便于更新地图
         * @param aMapLocation
         */
        public void onLocationChanged(AMapLocation aMapLocation) {
            //通知定位位置变化
            if (onLocationChangedListener != null)
                onLocationChangedListener.onLocationChanged(aMapLocation);

            positionLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            //绘制动态线段
            workDetailMap.drawTrendsBusToStationLine(positionLatlng);
        }

        /*************************************************
         * 激活自动定位
         * @param listener
         */
        public void activate(LocationSource.OnLocationChangedListener listener) {
            if (onLocationChangedListener == null)
                this.onLocationChangedListener = listener;
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

        @Override
        public void onInfoWindowClick(Marker marker) {

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
