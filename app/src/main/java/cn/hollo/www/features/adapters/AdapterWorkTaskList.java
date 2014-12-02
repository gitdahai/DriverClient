package cn.hollo.www.features.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cn.hollo.www.R;
import cn.hollo.www.features.informations.WorkTaskExpand;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-12-2.
 * 任务列表数据适配器
 */
public class AdapterWorkTaskList extends CursorAdapter{
    private Typeface typeface;

    /**
     *
     * @param context
     * @param cursor
     */
    public AdapterWorkTaskList(Context context, Cursor cursor) {
        super(context, cursor, true);
        typeface = Util.loadTypeface(context, "fonts/ImpactMTStd.otf");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.fragment_task_list_item, null);
        ItemHolder holder = new ItemHolder(view);
        view.setTag(holder);

        if (cursor != null){
            WorkTaskExpand wte = WorkTaskExpand.createWorkTaskExpand(cursor);
            holder.showData(wte);
        }

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ItemHolder holder = (ItemHolder)view.getTag();
        //设置新数据
        if (cursor != null){
            WorkTaskExpand wte = WorkTaskExpand.createWorkTaskExpand(cursor);
            holder.showData(wte);
        }
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return newCursor;
    }

    /***************************************************
     * 列表项
     */
    public class ItemHolder{
        private TextView voitureNumberText;
        private TextView voitureTypeNameText;
        private TextView taskDateTimeText;
        private TextView departureStationText;
        private TextView destinationStationText;
        private TextView taskStateText;
        public WorkTaskExpand data;

        private ItemHolder(View view){
            voitureNumberText = (TextView)view.findViewById(R.id.voitureNumberText);
            voitureTypeNameText = (TextView)view.findViewById(R.id.voitureTypeNameText);
            taskDateTimeText = (TextView)view.findViewById(R.id.taskDateTimeText);
            departureStationText = (TextView)view.findViewById(R.id.departureStationText);
            destinationStationText = (TextView)view.findViewById(R.id.destinationStationText);
            taskStateText = (TextView)view.findViewById(R.id.taskStateText);
            taskDateTimeText.setTypeface(typeface);
        }

        private void showData(WorkTaskExpand data){
            this.data = data;
            voitureNumberText.setText(data.voiture_number);
            voitureTypeNameText.setText(data.voiture_type);
            departureStationText.setText(data.departure_station);
            destinationStationText.setText(data.destination_station);
            taskDateTimeText.setText(Util.createDateTime(data.time * 1000));

            if (data.task_state == 0)
                taskStateText.setText("未开启");
            else if (data.task_state == 1)
                taskStateText.setText("进行中...");
            else if (data.task_state == 2)
                taskStateText.setText("已完成");
        }
    }

}
