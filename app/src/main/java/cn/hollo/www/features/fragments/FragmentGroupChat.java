package cn.hollo.www.features.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.hollo.www.R;
import cn.hollo.www.features.FragmentBase;

/**
 * Created by orson on 14-12-18.
 * 群组聊天页面
 */
public class FragmentGroupChat extends FragmentBase {
    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, null);
        return view;
    }
}
