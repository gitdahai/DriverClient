package cn.hollo.www.content_provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by orson on 14-12-3.
 * 乘客数据库帮助类
 */
public class OpenHelperPassenger extends SQLiteOpenHelper {
    //数据表的定义字段
    private static final String DATA_0 = "data_0";
    private static final String DATA_1 = "data_1";
    private static final String DATA_2 = "data_2";
    private static final String DATA_3 = "data_3";
    private static final String DATA_4 = "data_4";
    private static final String DATA_5 = "data_5";
    private static final String DATA_6 = "data_6";
    private static final String DATA_7 = "data_7";
    private static final String DATA_8 = "data_8";
    private static final String DATA_9 = "data_9";
    private static final String DATA_10 = "data_10";
    private static final String DATA_11 = "data_11";
    private static final String DATA_12 = "data_12";
    private static final String DATA_13 = "data_13";
    private static final String DATA_14 = "data_14";
    private static final String DATA_15 = "data_15";

    //数据表的列名
    public static final String ID = "_id";              //主键索引id
    public static final String TASK_ID      = DATA_0;   //任务的id
    public static final String USER_ID      = DATA_1;   //乘客的id
    public static final String CONTRACT_ID  = DATA_2;   //合约id
    public static final String NICKNAME     = DATA_3;   //昵称
    public static final String AVATAR       = DATA_4;   //头像
    public static final String CONDITION    = DATA_5;   //状态(0=默认,1=上车, 2=下车)


    //数据库索引
    public static final int COL_INDEX_ID            = 0;
    public static final int COL_INDEX_TASK_ID       = 1;
    public static final int COL_INDEX_USER_ID       = 2;
    public static final int COL_INDEX_CONTRACT_ID   = 3;
    public static final int COL_INDEX_NICKNAME      = 4;
    public static final int COL_INDEX_AVATAR        = 5;
    public static final int COL_INDEX_CONDITION     = 6;

    static final String TABLE_NAME = "PassengerTable";

    public OpenHelperPassenger(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + TABLE_NAME);
        sb.append("( ");
        sb.append("_id integer primary key autoincrement");
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
