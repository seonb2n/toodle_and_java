package com.origincurly.toodletoodle.list;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.origincurly.toodletoodle.R;

import java.util.ArrayList;
import java.util.List;

public class AddFastPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private final List<Fragment> fragmentList = new ArrayList<>();

    public AddFastPagerAdapter(FragmentManager fragmentManager, int behavior, Context context) {
        super(fragmentManager, behavior);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }

    public void setOnSelectView(TabLayout tabLayout, int position) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View selected = tab.getCustomView();
        TextView iv_text = selected.findViewById(R.id.tab_Txt);
        iv_text.setTextColor(ContextCompat.getColor(context, R.color.tab_txt_on));
    }

    public void setUnSelectView(TabLayout tabLayout, int position) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View selected = tab.getCustomView();
        TextView iv_text = selected.findViewById(R.id.tab_Txt);
        iv_text.setTextColor(ContextCompat.getColor(context, R.color.tab_txt_off));
    }
}