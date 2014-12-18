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
import cn.hollo.www.features.informations.StationInfo;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-11-27.
 * 工单详情列表适配器
 */
public class AdapterStationList extends BaseAdapter {
    private Context context;
    private List<StationInfo.Station> stations;
    private Typeface typeface;
    private int  executionIndex = -1;
    private OnActionArriveListener listener;

    /***********************************************
     *
     * @param context
     * @param stations
     */
    public AdapterStationList(Context context, List<StationInfo.Station> stations){
        this.context = context;
        this.stations = stations;
        typeface = Util.loadTypeface(context, "fonts/ImpactMTStd.otf");
    }

    /**********************************************
     * 设置开始的站点
     * @param index
     */
    public void setStartStation(int index){
        executionIndex = index;
        this.notifyDataSetChanged();
    }

    /***********************************************
     * 设置动作监听器
     * @param l
     */
    public void setOnActionArriveListener(OnActionArriveListener l){
        this.listener = l;
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

        //设置数据
        holer.setData(position, stations.get(position));

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
        public StationInfo.Station station;

        /**====================================================
         *
         * @param view
         */
        private ItemHolder(View view){
            this.view = view;
            arriveTime = (TextView)view.findViewById(R.id.arriveTime);
            arriveButton = (Button)view.findViewById(R.id.arriveButton);
            populationText = (TextView)view.findViewById(R.id.populationText);
            arriveStationText = (TextView)view.findViewById(R.id.arriveStationText);
            arriveTime.setTypeface(typeface);
            arriveButton.setOnClickListener(this);
        }

        /**===================================================
         *
         * @param position
         * @param station
         */
        private void setData(int position, StationInfo.Station station){
            this.station = station;
            arriveStationText.setText(station.name);
            int onSize = station.on_users.size();
            int offSize = station.off_users.size();
            populationText.setText("上车: " + onSize + "人  下车: " + offSize + "人");
            String timeString = Util.getTimeString(station.arrived_at);
            arriveTime.setText(timeString);

            //当前的列表项等于需要执行的项，则进行状态修改
            if (position == executionIndex){
                view.setEnabled(false);
                arriveButton.setEnabled(true);
            }
            else {
                view.setEnabled(true);
                arriveButton.setEnabled(false);
            }
        }

        /**=================================================
         * 到达按钮产生额事件
         * @param v
         */
        public void onClick(View v) {
            if (listener != null)
                listener.onActionArrive(executionIndex, station);

            executionIndex++;
            AdapterStationList.this.notifyDataSetChanged();
        }
    }

    /********************************************************
     * 到达按钮产生的事件
     */
    public interface OnActionArriveListener{
        public void onActionArrive(int pos, StationInfo.Station station);
    }
}
