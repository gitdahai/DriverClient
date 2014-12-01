package cn.hollo.www.features.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.hollo.www.R;
import cn.hollo.www.features.informations.WorkTaskDetail;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-11-27.
 * 工单详情列表适配器
 */
public class AdapterWorkDetail extends BaseAdapter {
    private Context context;
    private List<WorkTaskDetail.Station> stations;
    private Typeface typeface;
    private OnStateListener listener;
    private int executionIndex = -1;

    /**
     *
     * @param context
     * @param stations
     */
    public AdapterWorkDetail(Context context, List<WorkTaskDetail.Station> stations){
        this.context = context;
        this.stations = stations;
        typeface = Util.loadTypeface(context, "fonts/ImpactMTStd.otf");
    }

    public int getCount() {return stations.size();}
    public Object getItem(int position) {return stations.get(position);}
    public long getItemId(int position) {return position;}
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holer = null;

        if (convertView == null){
            convertView = View.inflate(context, R.layout.fragment_work_detail_list_item, null);
            holer = new ItemHolder(convertView);
            convertView.setTag(holer);
        }
        else
            holer = (ItemHolder)convertView.getTag();

        holer.setWorkTaskDetail(position, stations.get(position));

        //如果任务的执行索引等于当前任务列表索引，则是设置该item为关注状态
        if (executionIndex == position)
            holer.setItemAttentionState();
        //设置该item为关注初始状态
        else if (executionIndex > stations.size())
            holer.setItemInitState();
        //否则设置结束状态
        else
            holer.setItemFinishState();

        return convertView;
    }

    /************************************************************
     *
     */
    public class ItemHolder implements View.OnClickListener {
        public View   view;
        public Button arriveButton;        //到达按钮
        public TextView arriveTime;        //到达时间
        public TextView arriveStationText; //站点名称
        public TextView populationText;    //上下车的人数
        public WorkTaskDetail.Station station;

        private ItemHolder(View view){
            this.view = view;
            arriveTime = (TextView)view.findViewById(R.id.arriveTime);
            arriveButton = (Button)view.findViewById(R.id.arriveButton);
            populationText = (TextView)view.findViewById(R.id.populationText);
            arriveStationText = (TextView)view.findViewById(R.id.arriveStationText);
            arriveTime.setTypeface(typeface);

            view.setOnClickListener(this);
            arriveButton.setOnClickListener(this);
        }

        private void setWorkTaskDetail(int position, WorkTaskDetail.Station station){
            this.station = station;

            arriveStationText.setText(station.name);
            int onSize = station.on_users.size();
            int offSize = station.off_users.size();
            populationText.setText("上车: " + onSize + "人  下车: " + offSize + "人");
            String timeString = Util.getTimeString(station.arrived_at);
            arriveTime.setText(timeString);
        }

        /**
         * 初始状态
         */
        private void setItemInitState(){
            view.setBackgroundColor(context.getResources().getColor(R.color.color_trans));
            arriveButton.setEnabled(false);
        }

        /**
         * 关注状态
         */
        private void setItemAttentionState(){
            view.setBackgroundColor(context.getResources().getColor(R.color.color_gray_4d4d4d));
            arriveButton.setEnabled(true);
        }

        /**
         * 结束状态
         */
        private void setItemFinishState(){
            view.setBackgroundColor(context.getResources().getColor(R.color.color_trans));
            arriveButton.setEnabled(false);
        }

        @Override
        public void onClick(View v) {
            //到达按钮事件
            if (v.getId() == R.id.arriveButton){
                //如果当前的站点索引为最后一个，则可以确定完成了
                if (executionIndex == stations.size() - 1){
                    actionNext();

                    if (listener != null)
                        listener.onActionFinish(station);
                }
                else {
                    actionNext();

                    if (listener != null)
                        listener.onActionArrived(station);
                }
            }
            //itme试图事件
            else{
                if (listener != null)
                    listener.onActionItemClick(station);
            }
        }
    }

    /*********************************************************
     * 设置事件监听器
     * @param l
     */
    public void setOnStateListener(OnStateListener l){
        this.listener = l;
    }

    /**********************************************************
     * 执行任务
     */
    public void actionInit(int executionIndex){
        this.executionIndex = executionIndex;
        this.notifyDataSetChanged();

        if (listener != null){
            if (executionIndex >= stations.size())
                listener.onActionInit(stations.get(stations.size() - 1));
            else if (executionIndex >= 0)
                listener.onActionInit(stations.get(executionIndex));
        }
    }

    /**********************************************************
     * 下一个
     */
    public void actionNext(){
        executionIndex++;
        this.notifyDataSetChanged();
    }

    /**********************************************************
     * 返回当前的执行索引
     * @return
     */
    public int getExecutionIndex(){
        return executionIndex;
    }

    /**********************************************************
     * 路线的事件
     */
    public interface OnStateListener{
        /**
         * 初始动作
         * @param station
         */
        public void onActionInit(WorkTaskDetail.Station station);
        /**
         * 到达事件
         * @param station
         */
        public void onActionArrived(WorkTaskDetail.Station station);

        /**
         * 列表项点击事件
         * @param station
         */
        public void onActionItemClick(WorkTaskDetail.Station station);

        /**
         * 任务完成事件
         * @param station
         */
        public void onActionFinish(WorkTaskDetail.Station station);
    }
}
