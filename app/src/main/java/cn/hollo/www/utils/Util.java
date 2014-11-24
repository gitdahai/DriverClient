package cn.hollo.www.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

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
}
