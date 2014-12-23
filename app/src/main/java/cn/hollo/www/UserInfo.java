package cn.hollo.www;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by orson on 14-11-25.
 * 用户登录后，产生的基本信息
 */
public class UserInfo {
    private static UserInfo instance;
    private String userId;
    private String userPassword;
    private String userName = "";

    private SharedPreferences spf;

    private UserInfo(Context context){
        spf = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        userId    = spf.getString("UserId", null);
        userPassword = spf.getString("UserPassword", null);
    }

    public static UserInfo getInstance(Context context){
        if (instance == null)
            instance = new UserInfo(context.getApplicationContext());

        return instance;
    };

    public String getUserId() {return userId;}
    public String getUserPassword(){return userPassword;}
    public String getUserName(){return userName;}

    /*****************************************************
     * 保存用户的id
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
        saveField("UserId", userId);
    }

    /*****************************************************
     * 保存密码
     * @param password
     */
    public void setUserPassword(String password){
        this.userPassword = password;
        saveField("UserPassword", password);
    }

    /******************************************************
     * 保存名称
     * @param userName
     */
    public void setUserName(String userName){
        this.userName = userName;
        saveField("UserName", userName);
    }

    /**====================================================
     * 保存字段
     * @param key
     * @param value
     */
    private void saveField(String key, String value){
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
