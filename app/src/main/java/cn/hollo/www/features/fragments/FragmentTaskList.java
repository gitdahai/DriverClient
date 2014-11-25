package cn.hollo.www.features.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import cn.hollo.www.FragmentBase;
import cn.hollo.www.R;

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
        return view;
    }

    /****************************************************
     * 任务列表
     */
    private class TaskList{
        private ListView taskListView;

        private TaskList(View view){
            taskListView = (ListView)view.findViewById(R.id.taskListView);
        }
    }

    /***************************************************
     * 列表适配器
     */
    private class TaskListAdapter extends BaseAdapter{
        public int getCount() {
            return 0;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {return position;}
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
