package cn.hollo.www.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Field;

import cn.hollo.www.R;

/**
 * Created by orson on 14-11-24.
 * 修改密码对话框
 */
public class DialogModifyPassword implements DialogInterface.OnClickListener {
    private Context context;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText newPasswordAgainEditText;
    private View     dialogView;
    private Dialog   dialog;

    public DialogModifyPassword(Context context){
        this.context = context;

        initView();
    }

    private void initView(){
        dialogView = View.inflate(context, R.layout.modify_password_view, null);
        oldPasswordEditText = (EditText)dialogView.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = (EditText)dialogView.findViewById(R.id.newPasswordEditText);
        newPasswordAgainEditText = (EditText)dialogView.findViewById(R.id.newPasswordAgainEditText);
    }

    /**
     * 显示密码修改试图
     */
    public void show(){
        dialog = Util.createDialog(context, dialogView, "修改密码", "取消", "提交", null, this);
        dialog.setCanceledOnTouchOutside(false);
        updateDialogFiled(false);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_NEGATIVE:   onCancel(); break;
            case DialogInterface.BUTTON_POSITIVE:   onCommit(); break;
            case DialogInterface.BUTTON_NEUTRAL:    break;
        }
    }

    /**
     * 关闭事件
     */
    private void onCancel(){
        closeDialog();
    }

    /**
     * 提交事件
     */
    private void onCommit(){

    }

    /**
     *
     * @param b
     */
    private void updateDialogFiled(boolean b){
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭对话话框
     */
    private void closeDialog(){
        updateDialogFiled(true);
        dialog.dismiss();
        dialog = null;
        context = null;
    }
}
