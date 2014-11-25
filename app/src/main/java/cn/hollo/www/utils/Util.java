package cn.hollo.www.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by orson on 14-11-24.
 */
public class Util {
    /**
     * 根据View 创建一个对话框
     * @param context
     * @param view
     * @param title
     * @param negative
     * @param positive
     * @param neutral
     * @param listner
     * @return
     */
    public static Dialog createDialog(Context context, View view, String title, String negative, String positive, String neutral, DialogInterface.OnClickListener listner){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setTitle(title);

        if (negative != null)
            builder.setNegativeButton(negative, listner);

        if (positive != null)
            builder.setPositiveButton(positive, listner);

        if (neutral != null)
            builder.setNeutralButton(neutral, listner);

        AlertDialog dialog = builder.create();

        //显示出来
        dialog.show();

        return dialog;
    }

    /**
     * 转换成json字符串
     * @param values
     * @return
     */
    public static String convertJsonString(Map<String, String> values){
        Set<Map.Entry<String, String>> entrys = values.entrySet();
        JSONObject jObj = new JSONObject();

        try {
            for (Map.Entry<String, String> entry : entrys){
                jObj.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jObj.toString();
    };

    /**
     * 显示消息
     * @param context
     * @param msg
     */
    public static void showMsg(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
