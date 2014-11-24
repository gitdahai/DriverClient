package cn.hollo.www.features;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.hollo.www.FragmentBase;

/**
 * Created by orson on 14-11-24.
 * 任务列表
 */
public class FragmentTaskList extends FragmentBase {
    public static FragmentTaskList newInstance(){
        return new FragmentTaskList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
