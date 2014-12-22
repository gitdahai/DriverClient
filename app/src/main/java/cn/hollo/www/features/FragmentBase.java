package cn.hollo.www.features;

import android.app.Fragment;

import java.util.List;

import cn.hollo.www.features.fragments.IDriverActions;
import cn.hollo.www.features.informations.StationInfo;

/**
 * Created by orson on 14-11-24.
 */
public class FragmentBase extends Fragment implements IDriverActions {
    @Override
    public void onPutData(Object data) {

    }

    @Override
    public void onInitMission(List<StationInfo.Station> stations) {

    }

    @Override
    public void onStartMission(StationInfo.Station station) {

    }

    @Override
    public void onArrivingStation(StationInfo.Station station) {

    }

    @Override
    public void onNextArrivingStation(StationInfo.Station station) {

    }

    @Override
    public void onFinishMission() {

    }
}
