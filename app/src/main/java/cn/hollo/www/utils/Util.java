package cn.hollo.www.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
     * 创建一个默认
     * @param context
     * @param title
     * @param message
     * @param negative
     * @param positive
     * @param neutral
     * @param listner
     * @return
     */
    public static Dialog creteDefaultDialog(Context context, String title, String message, String negative, String positive, String neutral, DialogInterface.OnClickListener listner){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        if (negative != null)
            builder.setPositiveButton(negative, listner);

        if (positive != null)
            builder.setNegativeButton(positive, listner);

        if (neutral != null)
            builder.setNeutralButton(neutral, listner);

        Dialog dialog = builder.create();
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

    /**
     * 根据时间戳，生成一个由日期和时间组成的字符串
     * @param time
     * @return
     */
    public static String createDateTime(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        SimpleDateFormat sFormat = new SimpleDateFormat("MM-dd  hh:mm");
        return sFormat.format(c.getTime());
    }


    /**
     *
     * @param context
     * @param title
     * @param msg
     * @return
     */
    public static ProgressDialog createProgressDialog(Context context, String title, String msg){
        ProgressDialog dialog = new ProgressDialog(context);
        // 设置进度条风格，风格为圆形，旋转的
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // 设置ProgressDialog 标题
        dialog.setTitle(title);

        // 设置ProgressDialog 提示信息
        dialog.setMessage(msg);

        // 设置ProgressDialog 标题图标
        //dialog.setIcon(R.drawable.img1);

        // 设置ProgressDialog 的进度条是否不明确
        dialog.setIndeterminate(false);

        // 设置ProgressDialog 是否可以按退回按键取消
        dialog.setCancelable(true);
        return dialog;
    }

    /**
     * 加载字体
     * @param context
     * @param fontPath
     * @return
     */
    public static Typeface loadTypeface(Context context, String fontPath){
        Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        return tf;
    }

    /**
     * 转换成时间字符串
     * @param time
     * @return
     */
    public static String getTimeString(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(c.getTime());
    }
}
