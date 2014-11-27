package cn.hollo.www.features.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amap.api.maps.MapView;

import cn.hollo.www.R;
import cn.hollo.www.UserInfo;
import cn.hollo.www.features.FragmentBase;
import cn.hollo.www.features.adapters.AdapterWorkDetail;
import cn.hollo.www.features.informations.ParserUtil;
import cn.hollo.www.features.informations.WorkTask;
import cn.hollo.www.features.informations.WorkTaskDetail;
import cn.hollo.www.features.params.RequestWorkTaskDetailParam;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;

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
        //workDetailMap.onCreate(savedInstanceState);
        workDetailList = new WorkDetailList(view);
        workDetailList.loadWorkTaskDetail(this.getArguments());
        return view;
    }

    public void onResume() {
        super.onResume();
        //workDetailMap.onResume();
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //workDetailMap.onSaveInstanceState(outState);
    }

    public void onPause() {
        super.onPause();
        //workDetailMap.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        //workDetailMap.onDestroy();
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
                    flushListView(workTaskDetails);
                }
            }
        }
    }

    /***************************************************
     *  任务单详情地图模式
     */
    private class WorkDetailMap{
        private MapView workMapView;
        private WorkTaskDetail.Station station;

        private WorkDetailMap(View view){
            workMapView = (MapView)view.findViewById(R.id.workDetailMapView);
        }

        /**
         * 刷新地图页面
         * @param station
         */
        private void flushMapView(WorkTaskDetail.Station station){
            this.station = station;
        }

        private void onCreate(Bundle bundle){workMapView.onCreate(bundle);}
        private void onDestroy(){workMapView.onDestroy();}
        private void onResume(){workMapView.onResume();}
        private void onPause(){workMapView.onPause();}
        private void onSaveInstanceState(Bundle bundle){workMapView.onSaveInstanceState(bundle);}
    }

    /***************************************************
     *  任务详情图标模式
     */
    private class WorkDetailIco{
        private WorkDetailIco(View view){

        }
    }
}
