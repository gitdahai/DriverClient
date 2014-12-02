package cn.hollo.www.features;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cn.hollo.www.LoginConfig;
import cn.hollo.www.R;
import cn.hollo.www.app.ServiceManager;
import cn.hollo.www.features.fragments.FragmentForgotPassword;
import cn.hollo.www.features.fragments.FragmentTaskList;
import cn.hollo.www.features.fragments.FragmentWorkDetail;
import cn.hollo.www.login.ActivityLogin;
import cn.hollo.www.others.DialogModifyPassword;

/**
 * Created by orson on 14-11-24.
 * 功能服务
 */
public class ActivityFeatures extends ActivityBase {
    private FragmentBase fragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_features);

        Intent intent = this.getIntent();
        int feature = intent.getIntExtra("Features", Features.None);

        //过滤功能
        switch (feature){
            case Features.TaskList:         loadFragment(FragmentTaskList.newInstance());       break;
            case Features.ForgotPassword:   loadFragment(FragmentForgotPassword.newInstance()); break;
            case Features.WorkDetail:       loadFragment(FragmentWorkDetail.newInstance());     break;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_features, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionModifyPassword: onModifyPassword(); return true;
            case R.id.actionLogout:         onLogout();         return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载Fragment
     * @param fragment
     */
    private void loadFragment(FragmentBase fragment){
        Intent intent = this.getIntent();
        Bundle mBundle = intent.getExtras();

        this.fragment = fragment;
        this.fragment.setArguments(mBundle);
        FragmentTransaction tf = this.getFragmentManager().beginTransaction();
        tf.replace(R.id.featuresContainer, fragment);
        tf.commit();
    }

    /**
     * 修改密码
     */
    private void onModifyPassword(){
        DialogModifyPassword dialog = new DialogModifyPassword(this);
        dialog.show();
    }

    /**
     * 退出登录
     */
    private void onLogout(){
        LoginConfig config = LoginConfig.getLoginConfig(this);
        config.loginPassword = null;
        LoginConfig.saveLoginConfig(this, config);

        //停止服务
        ServiceManager SM = ServiceManager.getInstance();
        SM.stopService(this);

        //重新开启登录
        Intent intent = new Intent(this, ActivityLogin.class);
        startActivity(intent);
        closeAllActivity();
    }

    /*************************************************************
     *
     */
    public interface Features{
        int None = 0;
        int TaskList = 1;           //任务列表
        int ForgotPassword = 2;     //忘记密码
        int WorkDetail = 3;         //工作任务详情
    }
}
