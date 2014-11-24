package cn.hollo.www;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by orson on 14-11-24.
 * 保存用户登录的基本信息和状态
 */
public class LoginConfig {
    public String loginName;            //用户登录帐号
    public String loginPassword;        //用户登录密码
    public boolean isAutoLogin;         //是否自动登录的状态
    public boolean isRememberPassword;  //是否记住密码的状态

    /**
     * 获取（读取）LoginConfig　对象
     * @param context
     * @return
     */
    public static LoginConfig getLoginConfig(Context context){
        LoginConfig config = new LoginConfig();
        SharedPreferences spf = context.getSharedPreferences("LoginConfig", Context.MODE_PRIVATE);

        config.loginName = spf.getString("LoginName", null);
        config.loginPassword = spf.getString("LoginPassword", null);
        config.isAutoLogin = spf.getBoolean("IsAutoLogin", true);
        config.isRememberPassword = spf.getBoolean("IsRememberPassword", true);

        return config;
    }

    /**
     * 保存LoginConfig对象
     * @param context
     * @param config
     */
    public static void saveLoginConfig(Context context, LoginConfig config){
        SharedPreferences spf = context.getSharedPreferences("LoginConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString("LoginName", config.loginName);

        //如果记住密码为true,则直接保存密码，否则密码数据清空
        if (config.isRememberPassword)
            editor.putString("LoginPassword", config.loginPassword);
        else
            editor.putString("LoginPassword", null);

        editor.putBoolean("IsAutoLogin", config.isAutoLogin);
        editor.putBoolean("IsRememberPassword", config.isRememberPassword);
        editor.commit();
    }
}
