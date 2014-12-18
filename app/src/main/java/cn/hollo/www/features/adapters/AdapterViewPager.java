package cn.hollo.www.features.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import cn.hollo.www.features.FragmentBase;

/**
 * Created by orson on 14-12-18.
 * viewPager页面适配器
 */
public class AdapterViewPager extends FragmentStatePagerAdapter {
    private FragmentBase[] fragments;
    private String[] titles;

    /**************************************************
     *
     * @param fm
     * @param fragments : 需要切换的页面
     * @param titles    : 标题列表
     */
    public AdapterViewPager(FragmentManager fm, FragmentBase[] fragments, String[] titles) {
        super(fm);

        this.fragments = fragments;
        this.titles    = titles;
    }

    public int getCount() {return titles.length;}
    public Fragment getItem(int i) {return fragments[i];}
    public CharSequence getPageTitle(int position){return titles[position];}
}
