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
    private static final String DATA_16 = "data_16";
    private static final String DATA_17 = "data_17";
    private static final String DATA_18 = "data_18";
    private static final String DATA_19 = "data_19";
    private static final String DATA_20 = "data_20";

    //对外公开的字段名称
    public static final String ID                       = "_id";    //主键索引id
    public static final String GENDER                   = DATA_0;   //性别
    public static final String DURATION                 = DATA_1;   //语音的时长
    public static final String LONGITUDE                = DATA_2;   //经度
    public static final String LATITUDE                 = DATA_3;   //维度
    public static final String CONTENT                  = DATA_4;   //消息的内容
    public static final String GROUP_ROOM_TYPE          = DATA_5;   //房间的类型
    public static final String NICKNAME                 = DATA_6;   //昵称
    public static final String SPEEKER                  = DATA_7;   //消息的发送者的id
    public static final String AVATAR                   = DATA_8;   //头像地址
    public static final String MESSAGE_TYPE             = DATA_9;   //消息的类型
    public static final String DESCRIPTION              = DATA_10;  //位置信息的描述
    public static final String SEND_FROM_SPECIAL_USER   = DATA_11;  //消息的发送端(Driver, User...)
    public static final String ROOM_ID                  = DATA_12;  //房间的id
    public static final String MESSAGE_ID               = DATA_13;  //消息产生或者接收的时间(Long型数据)
    public static final String MESSAGE_STATUS           = DATA_14;  //消息的发送／接收状态（0=接收中，1=接收成功，2=接收失败）
    public static final String IS_READ                  = DATA_15;  //消息的状态（true=已读，false=未读）
    public static final String IS_ISSUE                 = DATA_16;  //消息是自己发送的，还是接收的(true=发送，false=接收的)

    //对外公开的字段索引
    public static final int INDEX_ID                        = 0x00;
    public static final int INDEX_GENDER                    = 0x01;
    public static final int INDEX_DURATION                  = 0x02;
    public static final int INDEX_LONGITUDE                 = 0x03;
    public static final int INDEX_LATITUDE                  = 0x04;
    public static final int INDEX_CONTENT                   = 0x05;
    public static final int INDEX_GROUP_ROOM_TYPE           = 0x06;
    public static final int INDEX_NICKNAME                  = 0x07;
    public static final int INDEX_SPEEKER                   = 0x08;
    public static final int INDEX_AVATAR                    = 0x09;
    public static final int INDEX_MESSAGE_TYPE              = 0x0a;
    public static final int INDEX_DESCRIPTION               = 0x0b;
    public static final int INDEX_SEND_FROM_SPECIAL_USER    = 0x0c;
    public static final int INDEX_ROOM_ID                   = 0x0d;
    public static final int INDEX_MESSAGE_ID                = 0x0e;
    public static final int INDEX_MESSAGE_STATUS            = 0x0f;
    public static final int INDEX_IS_READ                   = 0x10;
    public static final int INDEX_IS_ISSUE                  = 0x11;

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
        sb.append(", data_0 Text default ''");
        sb.append(", data_1 Text default ''");
        sb.append(", data_2 Text default '' ");
        sb.append(", data_3 Text default '' ");
        sb.append(", data_4 Text default '' ");
        sb.append(", data_5 Text default '' ");
        sb.append(", data_6 Text default '' ");
        sb.append(", data_7 Text default '' ");
        sb.append(", data_8 Text default '' ");
        sb.append(", data_9 Text default '' ");
        sb.append(", data_10 Text default '' ");
        sb.append(", data_11 Text default '' ");
        sb.append(", data_12 Text default '' ");
        sb.append(", data_13 Text default '' ");
        sb.append(", data_14 Text default '' ");
        sb.append(", data_15 Text default '' ");
        sb.append(", data_16 Text default '' ");
        sb.append(", data_17 Text default '' ");
        sb.append(", data_18 Text default '' ");
        sb.append(", data_19 Text default '' ");
        sb.append(", data_20 Text default '' ");
        sb.append(")");

        String sql = sb.toString();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
