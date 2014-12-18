package cn.hollo.www.features;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import cn.hollo.www.LoginConfig;
import cn.hollo.www.R;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.login.ActivityLogin;
import cn.hollo.www.others.DialogModifyPassword;

/**
 * Created by orson on 14-11-13.
 */
public class ActivityBase extends Activity {
    /*************************************************
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    /**************************************************
     * 加载菜单
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_features, menu);
        return true;
    }

    /**************************************************
     * 惨淡菜单事件
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionModifyPassword: onModifyPassword(); return true;
            case R.id.actionLogout:         onLogout();         return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***************************************************
     * 修改密码
     */
    private void onModifyPassword(){
        DialogModifyPassword dialog = new DialogModifyPassword(this);
        dialog.show();
    }

    /***************************************************
     * 退出登录
     */
    private void onLogout(){
        LoginConfig config = LoginConfig.getLoginConfig(this);
        config.loginPassword = null;
        LoginConfig.saveLoginConfig(this, config);

        //停止服务
        ServiceManager SM = ServiceManager.getInstance(this);
        SM.stopService();

        //重新开启登录
        Intent intent = new Intent(this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }
}
