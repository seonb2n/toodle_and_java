package com.origincurly.toodletoodle;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.origincurly.toodletoodle.dialog.AddFastCancelDialog;
import com.origincurly.toodletoodle.list.AddFastPagerAdapter;
import com.origincurly.toodletoodle.fragment.AddFastPostitFragment;
import com.origincurly.toodletoodle.fragment.AddFastScheduleFragment;
import com.origincurly.toodletoodle.fragment.AddFastTaskFragment;
import com.origincurly.toodletoodle.ui.NonSwipeViewPager;

public class AddFastActivity extends BasicActivity {

    //region View

    private RelativeLayout init_Layout;
    private RelativeLayout add_Layout;
    private NonSwipeViewPager fastAdd_ViewPager;
    private TabLayout fastAdd_TabLayout;

    //endregion

    //region Variable

    private int tabNo;
    private AddFastPagerAdapter addFastPagerAdapter;
    private AddFastTaskFragment addFastTaskFragment;
    private AddFastScheduleFragment addFastScheduleFragment;
    private AddFastPostitFragment addFastPostitFragment;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fast);
        setActivity(this, this);

        init();
    }

    private void init() {
        init_Layout = findViewById(R.id.init_Layout);
        add_Layout = findViewById(R.id.add_Layout);
        fastAdd_ViewPager = findViewById(R.id.fastAdd_ViewPager);
        fastAdd_TabLayout = findViewById(R.id.fastAdd_TabLayout);

        addFastPagerAdapter = new AddFastPagerAdapter(getSupportFragmentManager(), 0, mContext);
        addFastTaskFragment = new AddFastTaskFragment(mContext, mActivity);
        addFastScheduleFragment = new AddFastScheduleFragment(mContext, mActivity);
        addFastPostitFragment = new AddFastPostitFragment(mContext, mActivity);
        addFastPagerAdapter.addFragment(addFastTaskFragment);
        addFastPagerAdapter.addFragment(addFastScheduleFragment);
        addFastPagerAdapter.addFragment(addFastPostitFragment);
        fastAdd_ViewPager.setAdapter(addFastPagerAdapter);

        fastAdd_TabLayout.addTab(fastAdd_TabLayout.newTab().setCustomView(createTabView(R.string.add_fast_task)));
        fastAdd_TabLayout.addTab(fastAdd_TabLayout.newTab().setCustomView(createTabView(R.string.add_fast_schedule)));
        fastAdd_TabLayout.addTab(fastAdd_TabLayout.newTab().setCustomView(createTabView(R.string.add_fast_postit)));
        
        fastAdd_ViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(fastAdd_TabLayout));
        fastAdd_TabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabNo = tab.getPosition();
                fastAdd_ViewPager.setCurrentItem(tabNo);
                addFastPagerAdapter.setOnSelectView(fastAdd_TabLayout, tabNo);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                addFastPagerAdapter.setUnSelectView(fastAdd_TabLayout, tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tabNo = tab.getPosition();
                fastAdd_ViewPager.setCurrentItem(tabNo);
                addFastPagerAdapter.setOnSelectView(fastAdd_TabLayout, tabNo);
            }
        });

        tabNo = ADD_FAST_INIT;
    }

    private View createTabView(int resId) {
        View tabView = LayoutInflater.from(mContext).inflate(R.layout.tab_add_fast, null);
        TextView txt_name = tabView.findViewById(R.id.tab_Txt);
        txt_name.setText(resId);

        return tabView;
    }

    //region Init Button

    public void InitTaskClicked(View v) {
        showAddTab(ADD_FAST_TASK_TAB);

    }

    public void InitScheduleClicked(View v) {
        showAddTab(ADD_FAST_SCHEDULE_TAB);

    }

    public void InitPostitClicked(View v) {
        showAddTab(ADD_FAST_POSTIT_TAB);

    }

    public void CancelClicked(View v) {
        checkSaveOrNot();

    }

    //endregion

    private void showAddTab(int position) {
        init_Layout.setVisibility(View.GONE);
        add_Layout.setVisibility(View.VISIBLE);
        fastAdd_TabLayout.selectTab(fastAdd_TabLayout.getTabAt(position));

    }

    private boolean isValidTaskFragment() {
        return (addFastTaskFragment !=null && addFastTaskFragment.isInit);
    }
    private boolean isValidScheduleFragment() {
        return (addFastScheduleFragment !=null && addFastScheduleFragment.isInit);
    }
    private boolean isValidPostitFragment() {
        return (addFastPostitFragment !=null && addFastPostitFragment.isInit);
    }

    //region Api


    //endregion

    //region Task Button

    public void TaskTitleDeleteClicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.TaskTitleDeleteClicked();
        }
    }

    public void TaskImportance5Clicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.TaskImportance5Clicked();
        }
    }

    public void TaskImportance3Clicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.TaskImportance3Clicked();
        }
    }

    public void TaskImportance1Clicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.TaskImportance1Clicked();
        }
    }

    public void TaskProjectSetClicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.TaskProjectSetClicked();
        }
    }

    public void EndDatePlusClicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.EndDatePlusClicked();
        }
    }

    public void EndDateMinusClicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.EndDateMinusClicked();
        }
    }

    public void TaskDuringShadowClicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.TaskDuringShadowClicked();
        }
    }

    public void ActionAddClicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.ActionAddClicked();
        }
    }

    public void TaskAddClicked(View v) {
        if (isValidTaskFragment()) {
            addFastTaskFragment.TaskAddClicked();
        }
    }
    //endregion

    //region Schedule Button

    public void ScheduleTitleDeleteClicked(View v) {
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.ScheduleTitleDeleteClicked();
        }
    }

    public void DatePickerClicked(View v) {
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.DatePickerClicked();
        }
    }

    public void TimePickerClicked(View v) {
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.TimePickerClicked();
        }
    }

    public void ScheduleProjectAddClicked(View v) {
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.ScheduleProjectAddClicked();
        }
    }

    public void ScheduleProjectSetClicked(View v) {
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.ProjectSetClicked();
        }
    }

    public void ScheduleContentAddClicked(View v) {
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.ScheduleContentAddClicked();
        }
    }

    public void ScheduleContentDeleteClicked(View v) {
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.ScheduleContentDeleteClicked();
        }
    }

    public void ScheduleAddClicked(View v) {
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.ScheduleAddClicked();
        }
    }
    //endregion

    //region Postit Button

    public void PostitTitleDeleteClicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.PostitTitleDeleteClicked();
        }
    }

    public void ProjectCategory1Clicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.ProjectCategory1Clicked();
        }
    }

    public void ProjectCategory2Clicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.ProjectCategory2Clicked();
        }
    }

    public void ProjectCategory3Clicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.ProjectCategory3Clicked();
        }
    }

    public void ProjectCategory4Clicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.ProjectCategory4Clicked();
        }
    }

    public void ProjectCategory5Clicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.ProjectCategory5Clicked();
        }
    }

    public void ProjectCategory6Clicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.ProjectCategory6Clicked();
        }
    }

    public void ProjectCategory7Clicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.ProjectCategory7Clicked();
        }
    }

    public void ProjectCategory8Clicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.ProjectCategory8Clicked();
        }
    }

    public void PostitAddClicked(View v) {
        if (isValidPostitFragment()) {
            addFastPostitFragment.PostitAddClicked();
        }
    }

    //endregion

    @Override
    public void clearFocusBundle() {
        if (isValidTaskFragment()) {
            addFastTaskFragment.clearFocusBundle();
        }
        if (isValidScheduleFragment()) {
            addFastScheduleFragment.clearFocusBundle();
        }
        if (isValidPostitFragment()) {
            addFastPostitFragment.clearFocusBundle();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (tabNo) {
                case ADD_FAST_TASK_TAB:
                case ADD_FAST_SCHEDULE_TAB:
                case ADD_FAST_POSTIT_TAB:
                    checkSaveOrNot();
                    break;

                case ADD_FAST_INIT:
                default:
                    backActivity();
                    break;
            }
        }

        return true;
    }

    private void checkSaveOrNot() {
        addFastCancelDialog = new AddFastCancelDialog(mContext, addFastCancelNegativeClickListener, addFastCancelPositiveClickListener);
        addFastCancelDialog.show();
    }

    private AddFastCancelDialog addFastCancelDialog;
    public View.OnClickListener addFastCancelNegativeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addFastCancelDialog.dismiss();
        }
    };
    public View.OnClickListener addFastCancelPositiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addFastCancelDialog.dismiss();
            backActivity();
        }
    };
}