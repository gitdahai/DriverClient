package cn.hollo.www.features.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.hollo.www.R;
import cn.hollo.www.UserInfo;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.content_provider.OpenHelperWorkTask;
import cn.hollo.www.content_provider.ProviderWorkTask;
import cn.hollo.www.features.ActivityBase;
import cn.hollo.www.features.adapters.AdapterMissionList;
import cn.hollo.www.features.informations.MissionInfo;
import cn.hollo.www.features.informations.ParserUtil;
import cn.hollo.www.features.params.RequestWorkTasksParam;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-11-24.
 * 功能服务
 */
public class ActivityMissionList extends ActivityBase implements AdapterView.OnItemClickListener {
    private ListView taskListView;          //任务列表试图
    private AdapterMissionList adapter;    //任务适配器

    /*************************************************
     * 退出应用
     */
    public void onBackPressed() {
        Dialog dialog = Util.creteDefaultDialog(this, "提示", "您确定要退出?", "取消", "确定", null, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    ServiceManager SM = ServiceManager.getInstance(ActivityMissionList.this);
                    SM.stopService();
                    finish();
                }
            }
        });
    }

    /*************************************************
     * 准备，创建
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_mission_list);

        taskListView = (ListView)findViewById(R.id.taskListView);
        taskListView.setOnItemClickListener(this);

        Cursor cursor = getCursor();

        if (cursor != null){
            adapter = new AdapterMissionList(this, cursor);
            taskListView.setAdapter(adapter);
        }

        loadMissionList();
    }

    /*********************************************************
     *
     */
    protected void onStart() {
        super.onStart();

        //刷新状态
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /********************************************************
     * 加载任务列表数据
     */
    private void loadMissionList(){
        UserInfo userInfo = UserInfo.getInstance(this);
        //准备参数
        RequestWorkTasksParam param = new RequestWorkTasksParam();
        param.user_id = userInfo.getUserId();
        //设置回调
        param.lisntener = new OnRequestListener(){
            public void onResponse(int code, String response) {
                if (code == 200)
                    ParserUtil.parserWorkTasks(ActivityMissionList.this, response);
            }
        };

        //发送请求
        HttpStringRequest request = new HttpStringRequest(param);
        HttpManager httpManager = HttpManager.getInstance();
        httpManager.addRequest(request);
    }

    /********************************************************
     * 任务列表的项的事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdapterMissionList.ItemHolder holder = (AdapterMissionList.ItemHolder)view.getTag();
        MissionInfo data = holder.data;
        Intent intent = new Intent(this, ActivityMissionExecution.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("MissionInfo", data);
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    /********************************************************
     * 获取Cursor对象
     * @return
     */
    private Cursor getCursor(){
        Cursor cursor = null;
        String selection = OpenHelperWorkTask.TASK_STATE + "!=?";
        String[] selectionArgs = {"2"};
        cursor = getContentResolver().query(ProviderWorkTask.CONTENT_URI, null, selection, selectionArgs,null);
        return cursor;
    }
}
