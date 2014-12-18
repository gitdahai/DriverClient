package cn.hollo.www.features.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.hollo.www.R;
import cn.hollo.www.custom_view.PagerSlidingTabStrip;
import cn.hollo.www.features.FragmentBase;
import cn.hollo.www.features.adapters.AdapterViewPager;
import cn.hollo.www.features.informations.StationInfo;

/**
 * Created by orson on 14-12-18.
 * 任务操作窗口
 */
public class FragmentMissionWindow extends FragmentBase {
    private MissionWindow missionWindow;

    /****************************************************
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission_window, container, false);
        missionWindow = new MissionWindow(view);
        return view;
    }

    @Override
    public void onStartMission() {
        if (missionWindow != null){
            missionWindow.fragments[0].onStartMission();
            missionWindow.fragments[1].onStartMission();
        }
    }

    @Override
    public void onArrivingStation(StationInfo.Station station) {
        if (missionWindow != null){
            missionWindow.fragments[0].onArrivingStation(station);
            missionWindow.fragments[1].onArrivingStation(station);
        }
    }

    @Override
    public void onFinishMission() {
        if (missionWindow != null){
            missionWindow.fragments[0].onFinishMission();
            missionWindow.fragments[1].onFinishMission();
        }
    }

    /****************************************************
     * 任务窗口类
     */
    private class MissionWindow{
        private final String[] titles = {"群聊", "地图"};
        private final FragmentBase[] fragments = {new FragmentGroupChat(), new FragmentMissionMap()};
        private ViewPager fragmentViewpager;
        private PagerSlidingTabStrip pagerSliding;
        private FragmentStatePagerAdapter viewPagerAdapter;

        /************************************************
         *
         * @param view
         */
        private MissionWindow(View view){
            fragmentViewpager = (ViewPager)view.findViewById(R.id.fragmentViewpager);
            FragmentManager fm = FragmentMissionWindow.this.getChildFragmentManager();
            viewPagerAdapter = new AdapterViewPager(fm, fragments, titles);
            fragmentViewpager.setAdapter(viewPagerAdapter);

            pagerSliding = (PagerSlidingTabStrip)view.findViewById(R.id.pagerSliding);
            pagerSliding.setViewPager(fragmentViewpager);
        }
    }
}
