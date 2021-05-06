package com.origincurly.toodletoodle.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.R;

import java.util.ArrayList;

public class ProjectMiniAdapter extends BaseAdapter implements GlobalValue {

    private ArrayList<ProjectItem> itemList;
    private int selectProjectId;

    public ProjectMiniAdapter(ArrayList<ProjectItem> itemList, int selectProjectId) {
        this.itemList = itemList;
        this.selectProjectId = selectProjectId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_project_mini, parent, false);
        }

        RelativeLayout item_Layout = convertView.findViewById(R.id.item_Layout);
        RelativeLayout project_Layout = convertView.findViewById(R.id.project_Layout);
        TextView noProject_Txt = convertView.findViewById(R.id.noProject_Txt);

        ImageView projectColor_Img = convertView.findViewById(R.id.projectColor_Img);
        TextView title_Txt = convertView.findViewById(R.id.title_Txt);
        ImageView check_Img = convertView.findViewById(R.id.check_Img);

        ProjectItem item = itemList.get(position);

        projectColor_Img.setImageResource(item.colorResId);
        title_Txt.setText(item.title);

        if (item.id == PROJECT_NO_ID) {
            project_Layout.setVisibility(View.GONE);
            noProject_Txt.setVisibility(View.VISIBLE);

        } else {
            project_Layout.setVisibility(View.VISIBLE);
            noProject_Txt.setVisibility(View.GONE);

        }

        if (item.id == selectProjectId) {
            item_Layout.setBackgroundResource(R.color.dialog_bottom_sheet_item_select);
            check_Img.setVisibility(View.VISIBLE);

        } else {
            item_Layout.setBackgroundResource(R.color.dialog_bottom_sheet_item);
            check_Img.setVisibility(View.GONE);

        }

        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }
}