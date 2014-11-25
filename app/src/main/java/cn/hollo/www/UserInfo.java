package cn.hollo.www;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by orson on 14-11-25.
 * 用户登录后，产生的基本信息
 */
public class UserInfo {
    private static UserInfo instance;
    private String loginName;
    private String userId;
    private SharedPreferences spf;

    private UserInfo(Context context){
        spf = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        loginName = spf.getString("LoginName", null);
        userId    = spf.getString("UserId", null);
    }

    public static UserInfo getInstance(Context context){
        if (instance == null)
            instance = new UserInfo(context.getApplicationContext());

        return instance;
    };

    public String getUserId() {return userId;}
    public String getLoginName() {return loginName;}

    public void setLoginName(String loginName) {
        this.loginName = loginName;
        saveField("LoginName", loginName);
    }

    public void setUserId(String userId) {
        this.userId = userId;
        saveField("UserId", userId);
    }

    /**
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
