package cn.hollo.www.features.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hollo.www.FragmentBase;
import cn.hollo.www.R;
import cn.hollo.www.UserInfo;
import cn.hollo.www.features.ActivityFeatures;
import cn.hollo.www.features.informations.ParserUtil;
import cn.hollo.www.features.informations.WorkTask;
import cn.hollo.www.features.params.RequestWorkTasksParam;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;
import cn.hollo.www.utils.Util;

/********************************************************
 * Created by orson on 14-11-24.
 * 工作任务列表
 */
public class FragmentTaskList extends FragmentBase {
    private Typeface typeface;

    public static FragmentTaskList newInstance(){
        return new FragmentTaskList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, null);
        typeface = Util.loadTypeface(getActivity(), "fonts/ImpactMTStd.otf");
        new TaskList(view);
        return view;
    }

    /****************************************************
     * 任务列表
     */
    private class TaskList implements OnRequestListener, AdapterView.OnItemClickListener {
        private ListView taskListView;
        private List<WorkTask> workTasks;
        private TaskListAdapter adapter;

        private TaskList(View view){
            taskListView = (ListView)view.findViewById(R.id.taskListView);
            taskListView.setOnItemClickListener(this);
            workTasks = new ArrayList<WorkTask>();
            adapter = new TaskListAdapter(workTasks);
            taskListView.setAdapter(adapter);
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
               List<WorkTask> tasks = ParserUtil.parserWorkTasks(response);

                if (tasks != null){
                    workTasks.clear();
                    workTasks.addAll(tasks);
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WorkTask task = workTasks.get(position);
            Intent intent = new Intent(getActivity(), ActivityFeatures.class);
            intent.putExtra("Features", ActivityFeatures.Features.WorkDetail);
            Bundle mBoudle = new Bundle();
            mBoudle.putSerializable("Attach", task);
            intent.putExtras(mBoudle);
            startActivity(intent);
            //getActivity().finish();
        }
    }

    /***************************************************
     * 列表适配器
     */
    private class TaskListAdapter extends BaseAdapter{
        private List<WorkTask> workTasks;
        private TaskListAdapter(List<WorkTask> workTasks){this.workTasks = workTasks;}
        public int getCount() {
            return workTasks.size();
        }
        public Object getItem(int position) {
            return workTasks.get(position);
        }
        public long getItemId(int position) {return position;}
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;

            if (convertView == null){
                convertView = View.inflate(getActivity(), R.layout.fragment_task_list_item, null);
                holder = new ItemHolder(convertView);
                convertView.setTag(holder);
            }
            else
                holder = (ItemHolder)convertView.getTag();

            holder.showData(workTasks.get(position));
            return convertView;
        }
    }

    /***************************************************
     * 列表项
     */
    private class ItemHolder{
        private TextView voitureNumberText;
        private TextView voitureTypeNameText;
        private TextView taskDateTimeText;
        private TextView departureStationText;
        private TextView destinationStationText;

        private ItemHolder(View view){
            voitureNumberText = (TextView)view.findViewById(R.id.voitureNumberText);
            voitureTypeNameText = (TextView)view.findViewById(R.id.voitureTypeNameText);
            taskDateTimeText = (TextView)view.findViewById(R.id.taskDateTimeText);
            departureStationText = (TextView)view.findViewById(R.id.departureStationText);
            destinationStationText = (TextView)view.findViewById(R.id.destinationStationText);

            taskDateTimeText.setTypeface(typeface);
        }

        private void showData(WorkTask data){
            voitureNumberText.setText(data.voiture_number);
            voitureTypeNameText.setText(data.voiture_type);
            departureStationText.setText(data.departure_station);
            destinationStationText.setText(data.destination_station);
            taskDateTimeText.setText(Util.createDateTime(data.date * 1000));
        }
    }
}
