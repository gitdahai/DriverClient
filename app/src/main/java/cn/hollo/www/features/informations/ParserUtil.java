package cn.hollo.www.features.informations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by orson on 14-11-25.
 * 解析工具类
 */
public class ParserUtil {
    /**
     * 解析工作任务列表
     * @param json
     * @return
     */
    public static List<WorkTask> parserWorkTasks(String json){
        Type listType = new TypeToken<ArrayList <WorkTask>>(){}.getType();
        Gson gson = new Gson();
        ArrayList list = gson.fromJson(json, listType);
        return list;
    }

}
