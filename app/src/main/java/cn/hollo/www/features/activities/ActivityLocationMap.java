package cn.hollo.www.features.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

import cn.hollo.www.R;
import cn.hollo.www.features.fragments.FragmentGetLocation;
import cn.hollo.www.features.fragments.FragmentShowLocation;

/********************************************************
 * Created by orson on 14-12-27.
 * 显示位置坐标和获取位置坐标的功能
 */
public class ActivityLocationMap extends Activity {
    /**显示位置信息*/
    public static final int TYPE_SHOW_LOCATION = 1;
    /**选取一个位置信息*/
    public static final int TYPE_GET_LOCATION = 2;

    private Fragment fragment;
    /**==================================================
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_location_map);

        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        //检查需要显示地图的类型
        Intent intent = this.getIntent();
        int type = intent.getIntExtra("Type", 0);

        //位置信息
        LocationInfo locationInfo = new LocationInfo();

        if (TYPE_SHOW_LOCATION == type){
            actionbar.setTitle("显示位置");
            locationInfo.description = intent.getStringExtra("Description");
            locationInfo.lat = intent.getDoubleExtra("Lat", 0);
            locationInfo.lng = intent.getDoubleExtra("Lng", 0);
            fragment = new FragmentShowLocation();
        }
        else if (TYPE_GET_LOCATION == type){
            actionbar.setTitle("选择位置");
            fragment = new FragmentGetLocation();
        }
        else
            actionbar.setTitle("干点什么呢？");

        //传递参数
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("LocationInfo", locationInfo);
        fragment.setArguments(mBundle);

        //添加试图
        FragmentTransaction ft =this.getFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment).commit();
    }

    /***************************************************
     * 位置信息类
     */
    public class LocationInfo implements Serializable {
        public String description;
        public double lat;
        public double lng;
    }
}
