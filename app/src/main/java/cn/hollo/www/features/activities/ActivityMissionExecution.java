package cn.hollo.www.features.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import cn.hollo.www.R;
import cn.hollo.www.content_provider.OpenHelperChatMessage;
import cn.hollo.www.content_provider.ProviderChatMessage;
import cn.hollo.www.features.ActivityBase;
import cn.hollo.www.features.fragments.FragmentMission;
import cn.hollo.www.features.fragments.FragmentMissionWindow;
import cn.hollo.www.features.informations.MissionInfo;

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

        //更新没有完成的状态
        updateToChatMessageDB(mBundle);

        fragmentMissionDetail = new FragmentMission();
        fragmentMissionWindow = new FragmentMissionWindow();
        fragmentMissionDetail.setArguments(mBundle);
        fragmentMissionWindow.setArguments(mBundle);

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

    /*************************************************************
     * 当程序进入到任务执行功能中时，需要修改上次退出是还没有完成的状态
     * 当message_status = 0 时，修改成3
     * 当message_status = 4 时，修改成1
     */
    private void updateToChatMessageDB(Bundle mBundle){
        if (mBundle == null)
            return;

        MissionInfo missionInfo = (MissionInfo)mBundle.getSerializable("MissionInfo");

        if (missionInfo == null)
            return;

        String where = OpenHelperChatMessage.ROOM_ID + "=? and " +
                OpenHelperChatMessage.MESSAGE_STATUS + "=?";

        String[] selectionArgs = new String[2];
        selectionArgs[0] = missionInfo.room_id;

        ContentValues values = new ContentValues();

        //首先修改message_status = 0 时的状态
        selectionArgs[1] = "0";
        values.put(OpenHelperChatMessage.MESSAGE_STATUS, 3);
        this.getContentResolver().update(ProviderChatMessage.CONTENT_URI, values, where, selectionArgs);

        //在修改成message_status = ４的状态
        selectionArgs[1] = "4";
        values.clear();
        values.put(OpenHelperChatMessage.MESSAGE_STATUS, 1);
        this.getContentResolver().update(ProviderChatMessage.CONTENT_URI, values, where, selectionArgs);
    }
}
