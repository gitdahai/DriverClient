package cn.hollo.www.login;

import org.json.JSONException;
import org.json.JSONObject;

import cn.hollo.www.UserInfo;

/**
 * Created by orson on 14-11-25.
 */
public class ParserJson {
    /**
     * 解析用户信息
     * @param jsonString
     * @param userInfo
     */
    public static void parserUserInfo(String jsonString, UserInfo userInfo){
        if (jsonString == null || userInfo == null)
            return;

        try {
            JSONObject jObj = new JSONObject(jsonString);

            if (jObj.has("login_name") && !jObj.isNull("login_name"))
                userInfo.setLoginName(jObj.getString("login_name"));

            if (jObj.has("user_id") && !jObj.isNull("user_id"))
                userInfo.setUserId(jObj.getString("user_id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
