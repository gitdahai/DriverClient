package cn.hollo.www.others;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Field;

import cn.hollo.www.R;
import cn.hollo.www.UserInfo;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-11-24.
 * 修改密码对话框
 */
public class DialogModifyPassword implements DialogInterface.OnClickListener, OnRequestListener {
    private Context context;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText newPasswordAgainEditText;
    private View     dialogView;
    private Dialog   dialog;
    private ProgressDialog progressDialog;

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
        //检查旧密码
        if (oldPasswordEditText.length() == 0){
            Util.showMsg(context, "旧密码不能为空!");
            return;
        }

        //检查新密码
        if (newPasswordEditText.length() == 0){
            Util.showMsg(context, "新密码不能为空!");
            return;
        }

        //读取参数
        String oldPass  = oldPasswordEditText.getText().toString();
        String newPass1 = newPasswordEditText.getText().toString();
        String newPass2 = newPasswordAgainEditText.getText().toString();

        //检查２次密码
        if (!newPass1.equals(newPass2)){
            Util.showMsg(context, "两次新密码不一致!");
            return;
        }

        //准备参数
        UserInfo userInfo = UserInfo.getInstance(context);
        RequestModifyPasswordPram param = new RequestModifyPasswordPram();
        param.old_password = oldPass;
        param.new_password = newPass1;
        param.listener = this;
        param.user_id  = userInfo.getUserId();
        //发送请求
        HttpStringRequest request = new HttpStringRequest(param);
        HttpManager httpManager = HttpManager.getInstance();
        httpManager.addRequest(request);

        if (progressDialog == null)
            progressDialog = Util.createProgressDialog(context, "提示", "正在重置密码...");

        progressDialog.show();
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

    @Override
    public void onResponse(int code, String response) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        //关闭对话框
        closeDialog();

        if (code == 200)
            Util.showMsg(context, "修改密码成功!");
    }
}
