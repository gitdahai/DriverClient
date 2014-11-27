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

            //用户的id
            if (jObj.has("user_id") && !jObj.isNull("user_id"))
                userInfo.setUserId(jObj.getString("user_id"));

            //用户的openfir的登录密码
            if (jObj.has("openfire_key") && !jObj.isNull("openfire_key"))
                userInfo.setUserPassword(jObj.getString("openfire_key"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
