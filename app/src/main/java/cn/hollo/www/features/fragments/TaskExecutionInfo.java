package cn.hollo.www.features.fragments;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by orson on 14-12-1.
 */
public class TaskExecutionInfo {
    private static TaskExecutionInfo instance;
    private SharedPreferences spf;

    private TaskExecutionInfo(Context context){
        spf = context.getSharedPreferences("TaskExecutionInfo", Context.MODE_PRIVATE);
    }

    public static TaskExecutionInfo getInstance(Context context){
        if (instance == null)
            instance = new TaskExecutionInfo(context);

        return instance;
    }

    /**
     * 获取执行索引
     * @return
     */
    public int getExecutionIndex(){
        return spf.getInt("ExecutionIndex", -1);
    }

    /**
     * 获取任务id
     * @return
     */
    public String getTaskId(){
        return spf.getString("TaskId", null);
    }


    /**
     * 保存当前的执行索引
     * @param index
     */
    public void putExecutionIndex(int index){
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt("ExecutionIndex", index);
        editor.commit();
    }

    /**
     * 保存任务的id
     * @param task_id
     */
    public void putTaskId(String task_id){
        SharedPreferences.Editor editor = spf.edit();
        editor.putString("TaskId", task_id);
        editor.commit();
    }
}
