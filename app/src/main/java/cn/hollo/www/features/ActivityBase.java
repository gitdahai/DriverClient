package cn.hollo.www.features;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-11-13.
 */
public class ActivityBase extends Activity {
    private static List<ActivityBase> activityCollection = new ArrayList<ActivityBase>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        activityCollection.add(this);
    }

    @Override
    public void onBackPressed() {
        Dialog dialog = Util.creteDefaultDialog(this, "提示", "您确定要退出?", "取消", "确定", null, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE){
                    closeAllActivity();
                    ServiceManager SM = ServiceManager.getInstance();
                    SM.stopService(ActivityBase.this);
                }
            }
        });
    }

    /**
     * 关闭所有Activitry
     */
    protected void closeAllActivity(){
        try{
            for (ActivityBase ac : activityCollection){
                if (ac != null && !ac.isFinishing())
                    ac.finish();
            }

            activityCollection.clear();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
