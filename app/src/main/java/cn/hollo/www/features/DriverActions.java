package cn.hollo.www.features;

import cn.hollo.www.features.params.ActionArriveStationParam;
import cn.hollo.www.features.params.ActionBusReadyParam;
import cn.hollo.www.features.params.ActionWorkTaskDeleteParam;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpRequestParams;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;

/**
 * Created by orson on 14-12-15.
 * 司机产生的动作
 */
public class DriverActions implements OnRequestListener {
    /***********************************************
     * 准备发车动作
     * @param userId
     * @param taskId
     */
    public void startAction(String userId, String taskId){
        ActionBusReadyParam busReady = new ActionBusReadyParam();
        busReady.user_id = userId;
        busReady.task_id = taskId;
        busReady.lisntener = this;
        action(busReady);
    }

    /***********************************************
     * 到达站点动作
     * @param userId
     * @param taskId
     * @param stationId
     */
    public void arriveAction(String userId, String taskId, String stationId){
        ActionArriveStationParam arriveStation = new ActionArriveStationParam();
        arriveStation.user_id = userId;
        arriveStation.task_id = taskId;
        arriveStation.station_id = stationId;
        arriveStation.lisntener = this;
        action(arriveStation);
    }

    /***********************************************
     * 完成任务的动作
     * @param userId
     * @param taskId
     */
    public void finishedAction(String userId, String taskId){
        ActionWorkTaskDeleteParam deleteParam = new ActionWorkTaskDeleteParam();
        deleteParam.user_id = userId;
        deleteParam.task_id = taskId;
        deleteParam.listener = this;
        action(deleteParam);
    }

    @Override
    public void onResponse(int code, String response) {
        System.out.println("==========action request===== " + code);
    }


    /************************************************
     * 执行动作
     * @param params
     */
    private void action(HttpRequestParams params){
        HttpStringRequest request = new HttpStringRequest(params);
        HttpManager manager = HttpManager.getInstance();
        manager.addRequest(request);
    }

}
