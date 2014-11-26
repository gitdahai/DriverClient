package cn.hollo.www;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.utils.Util;

/**
 * Created by orson on 14-11-13.
 */
public class ActivityBase extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    @Override
    public void onBackPressed() {
        Dialog dialog = Util.creteDefaultDialog(this, "提示", "您确定要退出?", "取消", "确定", null, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE){
                    ActivityBase.this.finish();
                    ServiceManager SM = ServiceManager.getInstance();
                    SM.stopLocationService(ActivityBase.this);
                }
            }
        });
    }
}
