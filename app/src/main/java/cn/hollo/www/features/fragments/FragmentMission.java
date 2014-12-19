package cn.hollo.www.features.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import cn.hollo.www.R;
import cn.hollo.www.UserInfo;
import cn.hollo.www.features.adapters.AdapterStationList;
import cn.hollo.www.features.informations.MissionInfo;
import cn.hollo.www.features.informations.ParserUtil;
import cn.hollo.www.features.informations.StationInfo;
import cn.hollo.www.features.params.RequestWorkTaskDetailParam;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-12-18.
 * 任务站点详情
 */
public class FragmentMission extends Fragment {
    private IDriverActions actions;

    /****************************************************
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stations, null);
        Bundle mBundle = this.getArguments();
        MissionInfo missionInfo = (MissionInfo)mBundle.getSerializable("MissionInfo");
        new Stations(view, missionInfo);
        return view;
    }

    /***************************************************
     * 设置动作事件的执行器对象
     * @param actions
     */
    public void setIDriverActions(IDriverActions actions){
        this.actions = actions;
    }

    /***************************************************
     *  任务单详情列表
     */
    private class Stations implements OnRequestListener, View.OnClickListener {
        private MissionInfo missionInfo;
        private StationInfo stationInfo;
        private ListView detailListView;
        private AdapterStationList adapter;
        private Button startMission;        //任务开始按钮

        /***********************************************
         * 任务列表初始化和数据的加载
         * @param view
         * @param missionInfo
         */
        private Stations(View view, MissionInfo missionInfo){
            this.missionInfo = missionInfo;

            detailListView = (ListView)view.findViewById(R.id.workDetailListView);
            startMission = (Button)view.findViewById(R.id.startDoTaskButton);
            startMission.setOnClickListener(this);
            startMission.setEnabled(false);

            //加载新数据
            loadWorkTaskDetail();
        }

        /**********************************************
         * 刷新列表
         * @param workTaskDetails
         */
        private void flushListView(StationInfo workTaskDetails){
            adapter = new AdapterStationList(getActivity(), workTaskDetails.stations);
            detailListView.setAdapter(adapter);
            adapter.setOnActionArriveListener(onAction);
        }

        /**********************************************
         * 加载数据
         */
        private void loadWorkTaskDetail(){
            //准备参数
            UserInfo userInfo = UserInfo.getInstance(getActivity());
            RequestWorkTaskDetailParam param = new RequestWorkTaskDetailParam();
            param.user_id = userInfo.getUserId();
            param.task_id = missionInfo.task_id;
            param.listener = this;
            //发送请求
            HttpStringRequest request = new HttpStringRequest(param);
            HttpManager httpManager = HttpManager.getInstance();
            httpManager.addRequest(request);
        }

        /**********************************************
         * 加载数据结果
         * @param code
         * @param response
         */
        public void onResponse(int code, String response) {
            if (code == 200){
                stationInfo = ParserUtil.parserWorkTaskDetail(response);

                //刷新页面数据
                if (stationInfo != null && stationInfo.stations.size() > 0){
                    //刷新任务列表
                    this.flushListView(stationInfo);
                    //初始站点
                    actions.onInitMission(stationInfo.stations);

                    //如果状态是一个“新任务”则开启“开始任务”按钮
                    if (missionInfo.task_state == 0)
                        startMission.setEnabled(true);

                    //如果是已经执行过的任务，则进行状态的设定
                    else if (missionInfo.task_state == 1){
                        adapter.setStartStation(missionInfo.execute_index);
                        //设置从那个站点开始
                        StationInfo.Station station = stationInfo.stations.get(missionInfo.execute_index);
                        actions.onStartMission(station);
                    }
                }
            }
        }

        /***********************************************
         *　开始事件　
         * @param v
         */
        public void onClick(View v) {
            Util.creteDefaultDialog(getActivity(), "确认", "确认开始任务?", "确定", "取消", null, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //确认开始
                    if (DialogInterface.BUTTON_POSITIVE == which) {
                        //设置任务初始执行时的状态
                        startMission.setEnabled(false);
                        //开始任务
                        onMissionStart();
                    }
                }
            });
        }

        /***********************************************
         * 站点到达事件监听器
         */
        private AdapterStationList.OnActionArriveListener onAction = new AdapterStationList.OnActionArriveListener(){
            public void onActionArrive(int pos, StationInfo.Station station) {
                onStationArrived(pos, station);
            }
        };

        /************************************************
         * 开始执行任务
         */
        private void onMissionStart(){
            //开始任务
            adapter.setStartStation(0);

            //更新任务的状态
            missionInfo.task_state = 1;
            missionInfo.execute_index = 0;
            missionInfo.update(getActivity());

            //发送“开始任务”的动作
            if (actions != null)
                actions.onStartMission(stationInfo.stations.get(0));
        }

        /************************************************
         * 站点到达
         * @param position
         */
        private void onStationArrived(int position, StationInfo.Station station){
            //发送“到达动作”事件
            if (actions != null){
                actions.onArrivingStation(station);

                //触发下一个站点的动作
                if (position + 1 < stationInfo.stations.size())
                    actions.onNextArrivingStation(stationInfo.stations.get(position + 1));
            }

            //任务完成
            if (position == stationInfo.stations.size() - 1){
                //发送“完成”动作
                if (actions != null)
                    actions.onFinishMission();

                missionInfo.delete(getActivity());
                getActivity().finish();
            }
            else{
                //更新任务的状态
                missionInfo.execute_index = position + 1;
                missionInfo.update(getActivity());
            }
        }
    }
}
