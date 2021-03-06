package cn.hollo.www.features;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import cn.hollo.www.UserInfo;
import cn.hollo.www.content_provider.OpenHelperWorkTask;
import cn.hollo.www.content_provider.ProviderWorkTask;
import cn.hollo.www.features.informations.MissionInfo;
import cn.hollo.www.features.params.ActionTaskFinishedParam;
import cn.hollo.www.https.HttpManager;
import cn.hollo.www.https.HttpStringRequest;
import cn.hollo.www.https.OnRequestListener;

/**
 * Created by orson on 14-12-2.
 * 数据维护服务
 */
public class ServiceData extends Service {
    private List<MissionInfo> wtes;

    public void onCreate() {
        super.onCreate();

        wtes = new ArrayList<MissionInfo>();
        String selection = OpenHelperWorkTask.TASK_STATE + "=?";
        String[] selectionArgs = {"2"};

        Cursor cursor = getContentResolver().query(ProviderWorkTask.CONTENT_URI, null, selection, selectionArgs, null);

        if (cursor == null){
            this.stopSelf();
            return;
        }

        if (cursor.getCount() == 0){
            cursor.close();
            this.stopSelf();
            return;
        }

        MissionInfo wte = null;

        while (cursor.moveToNext()){
            wte = MissionInfo.createWorkTaskExpand(cursor);
            wtes.add(wte);
        }

        cursor.close();

        //执行操作
        execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    /**************************************************
     * 进行下一组操作
     */
    private void execute(){
        if (wtes.size() > 0){
            MissionInfo wte = wtes.remove(0);
            new Deletion(wte).doDelete(this);
        }
        else
            this.stopSelf();
    }

    /**************************************************
     * 删除数据
     */
    private class Deletion implements OnRequestListener{
        private MissionInfo wte;

        private Deletion(MissionInfo wte){
            this.wte = wte;
        }

        private void doDelete(Context context){
            //准备请求参数
            UserInfo userInfo = UserInfo.getInstance(context);
            ActionTaskFinishedParam param = new ActionTaskFinishedParam();
            param.task_id = wte.task_id;
            param.user_id = userInfo.getUserId();
            param.listener = this;

            //发送删除的请求
            HttpStringRequest request = new HttpStringRequest(param);
            HttpManager httpManager = HttpManager.getInstance();
            httpManager.addRequest(request);
        }

        @Override
        public void onResponse(int code, String response) {
            if (code == 200){
                String where = OpenHelperWorkTask.TASK_ID + "=?";
                String[] selectionArgs = {wte.task_id};
                getContentResolver().delete(ProviderWorkTask.CONTENT_URI, where, selectionArgs);
            }

            execute();
        }
    }
}
