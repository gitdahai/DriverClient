package cn.hollo.www.features.informations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import cn.hollo.www.content_provider.OpenHelperWorkTask;
import cn.hollo.www.content_provider.ProviderWorkTask;

/**
 * Created by orson on 14-12-2.
 * 数据扩展
 */
public class WorkTaskExpand extends WorkTask {
    public int task_state;   //任务的状态(0=无状态，1=进行中, 2=已完成)
    public int execute_index;//任务的执行索引(从0开始)

    /**
     * 获取该对象ContentValues数据集合
     * @return
     */
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(OpenHelperWorkTask.TASK_ID,              task_id);
        values.put(OpenHelperWorkTask.VOITURE_NUMBER,       shuttle_name);
        values.put(OpenHelperWorkTask.VOITURE_TYPE,         _type);
        values.put(OpenHelperWorkTask.DEPARTURE_STATION,    departure);
        values.put(OpenHelperWorkTask.DESTINATION_STATION,  destination);
        values.put(OpenHelperWorkTask.DATE_TIME,            depart_at);
        values.put(OpenHelperWorkTask.TASK_STATE,           task_state);
        values.put(OpenHelperWorkTask.EXECUTE_INDEX,        execute_index);
        return values;
    }

    /**
     * 从Cursor中生成WorkTaskExpand对象
     * @param cursor
     * @return
     */
    public static WorkTaskExpand createWorkTaskExpand(Cursor cursor){
        WorkTaskExpand wte = new WorkTaskExpand();
        wte.task_id = cursor.getString(OpenHelperWorkTask.COL_INDEX_TASK_ID);
        wte.shuttle_name = cursor.getString(OpenHelperWorkTask.COL_INDEX_VOITURE_NUMBER);
        wte._type = cursor.getString(OpenHelperWorkTask.COL_INDEX_VOITURE_TYPE);
        wte.departure = cursor.getString(OpenHelperWorkTask.COL_INDEX_DEPARTURE_STATION);
        wte.destination = cursor.getString(OpenHelperWorkTask.COL_INDEX_DESTINATION_STATION);
        wte.depart_at = cursor.getLong(OpenHelperWorkTask.COL_INDEX_DATE_TIME);
        wte.task_state = cursor.getInt(OpenHelperWorkTask.COL_INDEX_TASK_STATE);
        wte.execute_index = cursor.getInt(OpenHelperWorkTask.COL_INDEX_EXECUTE_INDEX);
        return wte;
    }

    /**
     * 更新数据库
     * @param context
     */
    public void update(Context context){
        ContentValues values = getContentValues();
        String selection = OpenHelperWorkTask.TASK_ID + "=?";
        String[] selectionArgs = {task_id};
        context.getContentResolver().update(ProviderWorkTask.CONTENT_URI, values, selection, selectionArgs);
    }

    /**
     * 删除数据
     * @param context
     */
    public void delete(Context context){
        String selection = OpenHelperWorkTask.TASK_ID + "=?";
        String[] selectionArgs = {task_id};
        context.getContentResolver().delete(ProviderWorkTask.CONTENT_URI, selection, selectionArgs);
    }
}
