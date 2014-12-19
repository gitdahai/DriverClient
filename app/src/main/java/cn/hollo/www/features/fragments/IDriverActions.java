package cn.hollo.www.features.fragments;

import java.util.List;

import cn.hollo.www.features.informations.StationInfo;

/**
 * Created by orson on 14-12-18.
 * 由司机产生的动作
 */
public interface IDriverActions {
    /**
     * 初始站点
     * @param stations
     */
    public void onInitMission(List<StationInfo.Station> stations);

    /**
     * 开始任务
     */
    public void onStartMission(StationInfo.Station station);

    /**
     * 到达站点
     * @param station
     */
    public void onArrivingStation(StationInfo.Station station);

    /**
     * 将要到达的下一个站点
     * @param station
     */
    public void onNextArrivingStation(StationInfo.Station station);

    /**
     * 完成任务
     */
    public void onFinishMission();
}
