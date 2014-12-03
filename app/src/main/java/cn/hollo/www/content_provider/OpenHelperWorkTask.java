package cn.hollo.www.content_provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by orson on 14-12-2.
 * 工作任务单数据表
 */
public class OpenHelperWorkTask extends SQLiteOpenHelper {
    //列代号
    private static String DATA_0 = "data_0";
    private static String DATA_1 = "data_1";
    private static String DATA_2 = "data_2";
    private static String DATA_3 = "data_3";
    private static String DATA_4 = "data_4";
    private static String DATA_5 = "data_5";
    private static String DATA_6 = "data_6";
    private static String DATA_7 = "data_7";
    private static String DATA_8 = "data_8";
    private static String DATA_9 = "data_9";
    private static String DATA_10 = "data_10";
    private static String DATA_11 = "data_11";
    private static String DATA_12 = "data_12";
    private static String DATA_13 = "data_13";
    private static String DATA_14 = "data_14";
    private static String DATA_15 = "data_15";

    //列索引
    public static final int COL_INDEX_ID                  = 0;
    public static final int COL_INDEX_TASK_ID             = 1;
    public static final int COL_INDEX_VOITURE_NUMBER      = 2;
    public static final int COL_INDEX_VOITURE_TYPE        = 3;
    public static final int COL_INDEX_DEPARTURE_STATION   = 4;
    public static final int COL_INDEX_DESTINATION_STATION = 5;
    public static final int COL_INDEX_DATE_TIME           = 6;
    public static final int COL_INDEX_TASK_STATE          = 7;
    public static final int COL_INDEX_EXECUTE_INDEX       = 8;
     static final int COL_INDEX_DATA_8 = 9;
     static final int COL_INDEX_DATA_9 = 10;
     static final int COL_INDEX_DATA_10 = 11;
     static final int COL_INDEX_DATA_11 = 12;
     static final int COL_INDEX_DATA_12 = 13;
     static final int COL_INDEX_DATA_13 = 14;
     static final int COL_INDEX_DATA_14 = 15;
     static final int COL_INDEX_DATA_15 = 16;

    //列名称
    public static final String TABLE_NAME = "WorkTaskTable";
    public static final String ID = "_id";                          //主键索引
    public static final String TASK_ID              = DATA_0;       //任务的id
    public static final String VOITURE_NUMBER       = DATA_1;       //车辆编号
    public static final String VOITURE_TYPE         = DATA_2;       //车辆类型
    public static final String DEPARTURE_STATION    = DATA_3;       //起点站名称
    public static final String DESTINATION_STATION  = DATA_4;       //到达站点名称
    public static final String DATE_TIME            = DATA_5;       //日期和时间
    public static final String TASK_STATE           = DATA_6;       //任务的状态
    public static final String EXECUTE_INDEX        = DATA_7;       //任务的执行索引

    /**********************************************************
     * 在该构造方法中需要创建数据库
     * @param context
     * @param name
     * @param version
     */
    public OpenHelperWorkTask(Context context, String name, int version) {
        super(context, name, null, version);
    }

    /**********************************************************
     *
     * @param db
     */
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + TABLE_NAME);
        sb.append("( ");
        sb.append("_id  integer primary key autoincrement");
        sb.append(", data_0 Text");
        sb.append(", data_1 Text");
        sb.append(", data_2 Text");
        sb.append(", data_3 Text");
        sb.append(", data_4 Text");
        sb.append(", data_5 Text");
        sb.append(", data_6 Text");
        sb.append(", data_7 Text");
        sb.append(", data_8 Text");
        sb.append(", data_9 Text");
        sb.append(", data_10 Text");
        sb.append(", data_11 Text");
        sb.append(", data_12 Text");
        sb.append(", data_13 Text");
        sb.append(", data_14 Text");
        sb.append(", data_15 Text");
        sb.append(")");

        String sql = sb.toString();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
