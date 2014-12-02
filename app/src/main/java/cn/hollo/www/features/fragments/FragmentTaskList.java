package cn.hollo.www.features.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.hollo.www.R;
import cn.hollo.www.UserInfo;
import cn.hollo.www.content_provider.WorkTaskProvider;
import cn.hollo.www.features.ActivityFeatures;
import cn.hollo.www.features.FragmentBase;
import cn.hollo.www.features.adapters.AdapterWorkTaskList;
import cn.hollo.www.features.informations.ParserUtil;
import cn.hollo.www.features.informations.WorkTaskExpand;
import cn.hollo.www.features.params.RequestWorkTasksParam;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;

/********************************************************
 * Created by orson on 14-11-24.
 * 工作任务列表
 */
public class FragmentTaskList extends FragmentBase {
    public static FragmentTaskList newInstance(){
        return new FragmentTaskList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, null);

        new TaskList(view);
        return view;
    }

    /****************************************************
     * 任务列表
     */
    private class TaskList implements OnRequestListener, AdapterView.OnItemClickListener {
        private ListView taskListView;
        private AdapterWorkTaskList adapter;

        private TaskList(View view){
            taskListView = (ListView)view.findViewById(R.id.taskListView);
            taskListView.setOnItemClickListener(this);

            Cursor cursor = getCursor();

            if (cursor != null){
                adapter = new AdapterWorkTaskList(getActivity(), cursor);
                taskListView.setAdapter(adapter);
            }

            loadWorkTasks();
        }

        private void loadWorkTasks(){
            UserInfo userInfo = UserInfo.getInstance(getActivity());
            //准备参数
            RequestWorkTasksParam param = new RequestWorkTasksParam();
            param.lisntener = this;
            param.user_id = userInfo.getUserId();
            //发送请求
            HttpStringRequest request = new HttpStringRequest(param);
            HttpManager httpManager = HttpManager.getInstance();
            httpManager.addRequest(request);
        }

        @Override
        public void onResponse(int code, String response) {
            if (code == 200){
               ParserUtil.parserWorkTasks(getActivity(), response);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AdapterWorkTaskList.ItemHolder holder = (AdapterWorkTaskList.ItemHolder)view.getTag();
            WorkTaskExpand data = holder.data;

            Intent intent = new Intent(getActivity(), ActivityFeatures.class);
            intent.putExtra("Features", ActivityFeatures.Features.WorkDetail);
            Bundle mBoudle = new Bundle();
            mBoudle.putSerializable("Attach", data);
            intent.putExtras(mBoudle);
            startActivity(intent);
            getActivity().finish();
        }

        /********************************************************
         * 获取Cursor对象
         * @return
         */
        private Cursor getCursor(){
            Cursor cursor = null;
            cursor = getActivity().getContentResolver().query(WorkTaskProvider.CONTENT_URI, null, null, null,null);
            return cursor;
        }
    }
}
