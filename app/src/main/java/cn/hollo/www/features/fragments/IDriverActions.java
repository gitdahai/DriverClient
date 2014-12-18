package cn.hollo.www.features.fragments;

import java.util.List;

import cn.hollo.www.features.informations.StationInfo;

/**
 * Created by orson on 14-12-18.
 * 由司机产生的动作
 */
public interface IDriverActions {
    /**
     * 开始任务
     */
    public void onStartMission(List<StationInfo.Station> stations);

    /**
     * 到达站点
     * @param station
     */
    public void onArrivingStation(StationInfo.Station station);

    /**
     * 完成任务
     */
    public void onFinishMission();
}
