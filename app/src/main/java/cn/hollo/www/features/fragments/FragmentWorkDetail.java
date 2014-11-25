package cn.hollo.www.features.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.hollo.www.FragmentBase;

/**
 * Created by orson on 14-11-25.
 * 工作单的详情
 */
public class FragmentWorkDetail extends FragmentBase{
    public static FragmentWorkDetail newInstance(){
        return new FragmentWorkDetail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
