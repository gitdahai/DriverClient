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
        return convertView;
    }

    /************************************************************
     *
     */
    private class ItemHolder{
        private Button arriveButton;        //到达按钮
        private TextView arriveTime;        //到达时间
        private TextView arriveStationText; //站点名称
        private TextView populationText;    //上下车的人数
        private WorkTaskDetail.Station station;

        private ItemHolder(View view){
            arriveTime = (TextView)view.findViewById(R.id.arriveTime);
            arriveButton = (Button)view.findViewById(R.id.arriveButton);
            populationText = (TextView)view.findViewById(R.id.populationText);
            arriveStationText = (TextView)view.findViewById(R.id.arriveStationText);

            arriveTime.setTypeface(typeface);
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
    }
}
