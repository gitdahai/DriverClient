package cn.hollo.www.content_provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by orson on 14-12-22.
 */
public class OpenHelperChatMessage extends SQLiteOpenHelper {
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

    public static final String ID = "_id";              //主键索引id
    public static final String USER_ID = DATA_0;
    public static final String ROOM_ID = DATA_1;
    public static final String SPEEKER = DATA_2;
    public static final String GENDER  = DATA_3;
    public static final String NICKNAME = DATA_4;
    public static final String AVATAR   = DATA_5;
    public static final String BODY     = DATA_6;
    public static final String MESSAGE_TYPE = DATA_7;
    public static final String TIMES_STAMP  = DATA_8;

    public static final int INDEX_ID = 0;
    public static final int INDEX_USER_ID = 1;
    public static final int INDEX_ROOM_ID = 2;
    public static final int INDEX_SPEEKER = 3;
    public static final int INDEX_GENDER  = 4;
    public static final int INDEX_NICKNAME = 5;
    public static final int INDEX_AVATAR   = 6;
    public static final int INDEX_BODY     = 7;
    public static final int INDEX_MESSAGE_TYPE = 8;
    public static final int INDEX_TIMES_STAMP  = 9;

    static final String TABLE_NAME = "ChatMessage";

    /********************************************************
     * 构造方法
     * @param context
     * @param name
     * @param version
     */
    public OpenHelperChatMessage(Context context, String name, int version) {
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
