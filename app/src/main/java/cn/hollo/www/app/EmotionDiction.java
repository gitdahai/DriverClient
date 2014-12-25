package cn.hollo.www.app;


import android.content.Context;

import java.util.HashMap;

import cn.hollo.www.R;

public class EmotionDiction{
    private static EmotionDiction instance;
    private HashMap<String, Integer> emotionIconResIdMap = new HashMap<String,Integer>();
    private Context ctx;

    /**********************************************
     * 构造方法
     * @param ctx
     */
    private EmotionDiction(Context ctx){
        this.ctx = ctx;
        addMapEntry(R.string.text_1059,R.drawable.motion_1);
        addMapEntry(R.string.text_1060,R.drawable.motion_2);
        addMapEntry(R.string.text_1061,R.drawable.motion_3);
        addMapEntry(R.string.text_1062,R.drawable.motion_4);
        addMapEntry(R.string.text_1063,R.drawable.motion_5);
        addMapEntry(R.string.text_1064,R.drawable.motion_6);
        addMapEntry(R.string.text_1065,R.drawable.motion_7);
        addMapEntry(R.string.text_1066,R.drawable.motion_8);
        addMapEntry(R.string.text_1067,R.drawable.motion_9);
        addMapEntry(R.string.text_1068,R.drawable.motion_10);
        addMapEntry(R.string.text_1069,R.drawable.motion_11);
        addMapEntry(R.string.text_1070,R.drawable.motion_12);
        addMapEntry(R.string.text_1071,R.drawable.motion_13);
        addMapEntry(R.string.text_1072,R.drawable.motion_14);
        addMapEntry(R.string.text_1073,R.drawable.motion_15);
        addMapEntry(R.string.text_1074,R.drawable.motion_16);
        addMapEntry(R.string.text_1075,R.drawable.motion_17);
    }

    /***************************************************
     * 返回该类的实例对象
     * @param ctx
     * @return
     */
    public static EmotionDiction getInstance(Context ctx){
        if (instance == null)
            instance = new EmotionDiction(ctx);

        return instance;
    }

    /***************************************************
     * 返回对应的表情
     * @param emotionText
     * @return
     */
    public Integer getMotionIconResId(String emotionText){
        return emotionIconResIdMap.get(emotionText);
    }

    /***************************************************
     * 添加数据
     * @param emotionTextResId
     * @param emotionIconResId
     */
    private void addMapEntry(int emotionTextResId, int emotionIconResId){
        emotionIconResIdMap.put(ctx.getText(emotionTextResId).toString(), emotionIconResId);
    }
}
