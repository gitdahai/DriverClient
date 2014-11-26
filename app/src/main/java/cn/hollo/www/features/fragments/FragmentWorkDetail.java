package cn.hollo.www.features.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.hollo.www.FragmentBase;
import cn.hollo.www.R;

/*******************************************************
 * Created by orson on 14-11-25.
 * 工作单的详情
 */
public class FragmentWorkDetail extends FragmentBase{
    public static FragmentWorkDetail newInstance(){
        return new FragmentWorkDetail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_detail, null);
        return view;
    }

    /***************************************************
     *  任务单详情列表
     */
    private class WorkDetailList{
        private WorkDetailList(View view){

        }

    }

    /***************************************************
     *  任务单详情地图模式
     */
    private class WorkDetailMap{
        private WorkDetailMap(View view){

        }
    }

    /***************************************************
     *  任务详情图标模式
     */
    private class WorkDetailIco{
        private WorkDetailIco(View view){

        }
    }
}
