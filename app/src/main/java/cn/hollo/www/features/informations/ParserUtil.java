package cn.hollo.www.features.informations;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.hollo.www.content_provider.OpenHelperWorkTask;
import cn.hollo.www.content_provider.ProviderWorkTask;

/**
 * Created by orson on 14-11-25.
 * 解析工具类
 */
public class ParserUtil {
    /**************************************************************
     * 解析工作任务列表
     * @param context
     * @param json
     * @return
     */
    public static void parserWorkTasks(Context context, String json){
        System.out.println("==========task json=========== " + json);

        Type listType = new TypeToken<ArrayList <WorkTaskExpand>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<WorkTaskExpand> list = gson.fromJson(json, listType);

        //进行数据库插入
        ContentResolver resolver = context.getContentResolver();
        String selection = OpenHelperWorkTask.TASK_ID + "=?";
        String[] selectionArgs = new String[1];
        WorkTaskExpand workTask = null;
        Cursor cursor = null;
        int count = 0;

        //如果成功解析到数据，则进行数据库擦如操作
        if (list != null && list.size() > 0){
            int size = list.size();

            for (int i=0; i<size; i++){
                count = 0;
                workTask = list.get(i);
                selectionArgs[0] = workTask.task_id;
                cursor = resolver.query(ProviderWorkTask.CONTENT_URI, null, selection, selectionArgs, null);

                //检查数据表中是否存在同样的数据
                if (cursor != null){
                    count = cursor.getCount();
                    cursor.close();
                }

                //如果没有相同的数据，则进行插入操作
                if (count == 0){
                    resolver.insert(ProviderWorkTask.CONTENT_URI, workTask.getContentValues());
                    resolver.notifyChange(ProviderWorkTask.CONTENT_URI, null);
                }
            }
        }
    }

    /*************************************************************
     * 解析工作任务的详情数据
     * @param json
     * @return
     */
    public static WorkTaskDetail parserWorkTaskDetail(String json){
        System.out.println("==========detail json=========== " + json);

        WorkTaskDetail detail = null;
        //如果没有可解析的数据，则返回
        if (json == null || "".equals(json))
            return null;

        try {
            JSONObject jDetail = new JSONObject(json);
            detail = new WorkTaskDetail();
            //解析路线id
            if (jDetail.has("path_id") && !jDetail.isNull("path_id"))
                detail.path_id = jDetail.getString("path_id");

            //任务id
            if (jDetail.has("task_id") && !jDetail.isNull("task_id"))
                detail.task_id = jDetail.getString("task_id");

            //解析站点名称
            if (jDetail.has("stations") && !jDetail.isNull("stations")){
                JSONArray jStations = jDetail.getJSONArray("stations");
                int size = jStations.length();

                WorkTaskDetail.Station   station = null;
                JSONObject              jStation = null;

                //循环解析站点数据
                for (int i=0; i<size; i++){
                    jStation = jStations.getJSONObject(i);
                    station  = detail.newStationInstance();

                    //解析到达时间
                    if (jStation.has("arrived_at") && !jStation.isNull("arrived_at"))
                        station.arrived_at = jStation.getLong("arrived_at");

                    //解析站点名称
                    if (jStation.has("name") && !jStation.isNull("name"))
                        station.name = jStation.getString("name");

                    //解析站点的id
                    if (jStation.has("_id") && !jStation.isNull("_id"))
                        station._id = jStation.getString("_id");

                    //解析位置信息
                    if (jStation.has("location") && !jStation.isNull("location")){
                        JSONArray jLocation = jStation.getJSONArray("location");
                        parserLocation(jLocation, station.location);
                    }

                    //解析上车用户信息
                    if (jStation.has("on_users") && !jStation.isNull("on_users")){
                        JSONArray jUsers = jStation.getJSONArray("on_users");
                        parserUser(jUsers, station.on_users, detail.task_id);
                    }

                    //解析下车用户信息
                    if (jStation.has("off_users") && !jStation.isNull("off_users")){
                        JSONArray jUsers = jStation.getJSONArray("off_users");
                        parserUser(jUsers, station.off_users, detail.task_id);
                    }

                    detail.stations.add(station);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return detail;
    }

    /*****************************************************************
     * 解析位置信息
     * @param jLocation
     * @param location
     */
    private static void parserLocation(JSONArray jLocation, WorkTaskDetail.Location location) throws JSONException {
        location.lng = jLocation.getDouble(0);
        location.lat = jLocation.getDouble(1);
    }

    /*****************************************************************
     * 解析用户的基本信息
     * @param jUsers
     * @param users
     */
    private static void parserUser(JSONArray jUsers, List<Passenger> users, String task_id) throws JSONException {
        int size = jUsers.length();
        JSONObject jUser = null;
        Passenger  user = null;

        for (int i=0; i<size; i++){
            jUser = jUsers.getJSONObject(i);
            user  = new Passenger();
            user.task_id = task_id;

            if (jUser.has("user_id") && !jUser.isNull("user_id"))
                user.user_id = jUser.getString("user_id");

            if (jUser.has("avatar") && !jUser.isNull("avatar"))
                user.avatar = jUser.getString("avatar");

            if (jUser.has("nickname") && !jUser.isNull("nickname"))
                user.nickname = jUser.getString("nickname");

            if (jUser.has("contract_id") && !jUser.isNull("contract_id"))
                user.contract_id = jUser.getString("contract_id");

            users.add(user);
        }
    }
}
