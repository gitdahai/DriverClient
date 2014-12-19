package cn.hollo.www.features.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import cn.hollo.www.R;
import cn.hollo.www.features.ActivityBase;
import cn.hollo.www.features.fragments.FragmentMission;
import cn.hollo.www.features.fragments.FragmentMissionWindow;

/**
 * Created by orson on 14-12-18.
 * 任务执行
 */
public class ActivityMissionExecution extends ActivityBase {
    private FragmentMission fragmentMissionDetail;          // 任务详情
    private FragmentMissionWindow fragmentMissionWindow;    // 任务窗口
    /*************************************************************
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_execution);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("任务详情");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //提取传递进来的参数
        Intent intent = this.getIntent();
        Bundle mBundle = intent.getExtras();

        fragmentMissionDetail = new FragmentMission();
        fragmentMissionWindow = new FragmentMissionWindow();
        fragmentMissionDetail.setArguments(mBundle);

        //关联动作
        fragmentMissionDetail.setIDriverActions(fragmentMissionWindow);

        //添加任务的站点列表
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.replace(R.id.stations, fragmentMissionDetail);

        //添加任务操作窗口
        ft.replace(R.id.missionWindow, fragmentMissionWindow).commit();
    }

    /*************************************************************
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
