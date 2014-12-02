package cn.hollo.www.features.informations;

import android.content.ContentValues;
import android.database.Cursor;

import cn.hollo.www.content_provider.WorkTaskOpenHelper;

/**
 * Created by orson on 14-12-2.
 * 数据扩展
 */
public class WorkTaskExpand extends WorkTask {
    public int task_state;   //任务的状态(0=无状态，1=进行中, 2=已完成)

    /**
     * 获取该对象ContentValues数据集合
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(WorkTaskOpenHelper.TASK_ID,              task_id);
        values.put(WorkTaskOpenHelper.VOITURE_NUMBER,       voiture_number);
        values.put(WorkTaskOpenHelper.VOITURE_TYPE,         voiture_type);
        values.put(WorkTaskOpenHelper.DEPARTURE_STATION,    departure_station);
        values.put(WorkTaskOpenHelper.DESTINATION_STATION,  destination_station);
        values.put(WorkTaskOpenHelper.DATE_TIME,            time);
        values.put(WorkTaskOpenHelper.TASK_STATE,           task_state);
        return values;
    }

    /**
     * 从Cursor中生成WorkTaskExpand对象
     * @param cursor
     * @return
     */
    public static WorkTaskExpand createWorkTaskExpand(Cursor cursor){
        WorkTaskExpand wte = new WorkTaskExpand();
        wte.task_id = cursor.getString(WorkTaskOpenHelper.COL_INDEX_TASK_ID);
        wte.voiture_number = cursor.getString(WorkTaskOpenHelper.COL_INDEX_VOITURE_NUMBER);
        wte.voiture_type = cursor.getString(WorkTaskOpenHelper.COL_INDEX_VOITURE_TYPE);
        wte.departure_station = cursor.getString(WorkTaskOpenHelper.COL_INDEX_DEPARTURE_STATION);
        wte.destination_station = cursor.getString(WorkTaskOpenHelper.COL_INDEX_DESTINATION_STATION);
        wte.time = cursor.getLong(WorkTaskOpenHelper.COL_INDEX_DATE_TIME);
        wte.task_state = cursor.getInt(WorkTaskOpenHelper.COL_INDEX_TASK_STATE);
        return wte;
    }
}
