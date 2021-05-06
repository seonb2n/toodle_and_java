package com.origincurly.toodletoodle.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.R;

import java.util.ArrayList;

public class ProjectDoneAdapter extends BaseAdapter implements GlobalValue {

    private ArrayList<ProjectItem> itemList;

    public ProjectDoneAdapter() {
        itemList = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_project_done, parent, false);
        }

        TextView title_Text = convertView.findViewById(R.id.title_Text);
        TextView task_Task = convertView.findViewById(R.id.task_Task);
        TextView deadLine_Txt = convertView.findViewById(R.id.deadLine_Txt);

        ProjectItem item = itemList.get(position);

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

    public void addItem(String numberId, int id, String title, int taskTotal, int taskDone, String updateTime) {

        ProjectItem item = new ProjectItem();

        item.id = id;
        item.numberId = numberId;
        item.title = title;
        item.taskTotal = taskTotal;
        item.taskDone = taskDone;
        item.updateTime = updateTime;

        itemList.add(item);
    }
}