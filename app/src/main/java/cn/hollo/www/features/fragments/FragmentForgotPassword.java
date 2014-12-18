package cn.hollo.www.features.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.hollo.www.R;

/**
 * Created by orson on 14-11-25.
 * 忘记密码
 *
 */
public class FragmentForgotPassword extends Fragment {
    public static FragmentForgotPassword newInstance(){
        return new FragmentForgotPassword();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_forgot_password, null);
        return view;
    }
}
