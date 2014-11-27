package cn.hollo.www.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import cn.hollo.www.LoginConfig;
import cn.hollo.www.R;
import cn.hollo.www.UserInfo;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.features.ActivityFeatures;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;
import cn.hollo.www.utils.Util;

/*******************************************************************
 * Created by orson on 14-11-13.
 * 用户登录
 */
public class ActivityLogin extends Activity{
    private EditText loginNameEdit;             //登录帐号
    private EditText loginPasswordEdit;         //登录密码
    private CheckBox rememberPasswordCheckBox;  //记住密码
    private CheckBox autoLoginCheckBox;         //自动登录
    private Button   loginButton;               //登录按钮
    private Listener listener;
    private LoginConfig loginConfig;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

        loginButton = (Button)findViewById(R.id.loginButton);
        loginNameEdit = (EditText)findViewById(R.id.loginNameEdit);
        loginPasswordEdit = (EditText)findViewById(R.id.loginPasswordEdit);
        autoLoginCheckBox = (CheckBox)findViewById(R.id.autoLoginCheckBox);
        rememberPasswordCheckBox = (CheckBox)findViewById(R.id.rememberPasswordCheckBox);

        listener = new Listener();
        loginNameEdit.addTextChangedListener(listener);
        loginPasswordEdit.addTextChangedListener(listener);
        loginButton.setOnClickListener(listener);
        autoLoginCheckBox.setOnCheckedChangeListener(listener);
        rememberPasswordCheckBox.setOnCheckedChangeListener(listener);

        //首先禁用登录按钮
        //loginButton.setEnabled(false);
        //读取用户登录的配置
        loginConfig = LoginConfig.getLoginConfig(this);
        //设置登录帐号
        if (loginConfig.loginName != null)
            loginNameEdit.setText(loginConfig.loginName);
        //设置登录密码
        if (loginConfig.loginPassword != null)
            loginPasswordEdit.setText(loginConfig.loginPassword);
        //设置记住密码的状态
        rememberPasswordCheckBox.setChecked(loginConfig.isRememberPassword);
        //设置自动登录的状态
        autoLoginCheckBox.setChecked(loginConfig.isAutoLogin);
        //判断是否需要自动登录
        if (loginConfig.isAutoLogin && loginNameEdit.length() > 0 && loginPasswordEdit.length() > 0)
             onLogin();
    }

    /*****************************************************************
     * 自动登录选择框触发的事件
     * @param isChecked
     */
    private void onAutoLoginChecked(boolean isChecked){
        loginConfig.isAutoLogin = isChecked;

        if (isChecked){
            loginConfig.isRememberPassword = true;
            rememberPasswordCheckBox.setChecked(true);
        }
    }

    /**
     * 记住密码选择框被触发
     * @param isChecked
     */
    private void onRememberPasswordChecked(boolean isChecked){
        loginConfig.isRememberPassword = isChecked;

        if (!isChecked){
            loginConfig.isAutoLogin = false;
            autoLoginCheckBox.setChecked(false);
        }
    }

    /**
     * 登录事件被触发
     */
    private void onLogin(){
        /*Intent intent = new Intent(ActivityLogin.this, ActivityFeatures.class);
        intent.putExtra("Features", ActivityFeatures.Features.TaskList);
        startActivity(intent);
        finish();*/

        if (loginNameEdit.getText().length() == 0){
            Util.showMsg(this, "请输入用户名!");
            return;
        }

        if (loginPasswordEdit.getText().length() == 0){
            Util.showMsg(this, "请输入密码!");
            return;
        }

        //读取参数
        loginConfig.loginName = loginNameEdit.getText().toString().trim();
        loginConfig.loginPassword = loginPasswordEdit.getText().toString().trim();
        //设置请求参数
        RequestLogin login = new RequestLogin();
        login.login_name = loginConfig.loginName;
        login.password = loginConfig.loginPassword;
        login.lisntener = listener;
        //发送请求
        HttpStringRequest request = new HttpStringRequest(login);
        HttpManager httpManager = HttpManager.getInstance();
        httpManager.addRequest(request);

        //显示提示框
        if (progressDialog == null)
            progressDialog = Util.createProgressDialog(this, "提示", "登录中...");

        progressDialog.show();
    }

    /**
     * 监听用户帐号和登录密码文本框文本的变化
     * @param s
     */
    private void onEditTextChanged(CharSequence s){
        if (loginNameEdit.length() == 0){
            loginButton.setEnabled(false);
            return;
        }

        if (loginPasswordEdit.length() == 0){
            loginButton.setEnabled(false);
            return;
        }

        loginButton.setEnabled(true);
    }
    /***************************************************************
     * 事件监听器类
     */
    private class Listener implements CompoundButton.OnCheckedChangeListener, View.OnClickListener ,TextWatcher, OnRequestListener {
        public void onClick(View v) {onLogin();}
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {onEditTextChanged(s);}
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch(buttonView.getId()){
                case R.id.autoLoginCheckBox:        onAutoLoginChecked(isChecked);       break;
                case R.id.rememberPasswordCheckBox: onRememberPasswordChecked(isChecked);break;
            }
        }

        public void onResponse(int code, String response) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (code == 200){
                //保存配置信息
                LoginConfig.saveLoginConfig(ActivityLogin.this, loginConfig);
                //解析登录用户信息
                UserInfo userInfo = UserInfo.getInstance(ActivityLogin.this);
                ParserJson.parserUserInfo(response, userInfo);
                //进入任务列表
                Intent intent = new Intent(ActivityLogin.this, ActivityFeatures.class);
                intent.putExtra("Features", ActivityFeatures.Features.TaskList);
                startActivity(intent);
                finish();

                //启动服务
                ServiceManager SM = ServiceManager.getInstance();
                SM.startService(ActivityLogin.this);
            }
        }
    }
}
