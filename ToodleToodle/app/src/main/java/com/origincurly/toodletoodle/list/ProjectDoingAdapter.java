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

public class ProjectDoingAdapter extends BaseAdapter implements GlobalValue {

    private ArrayList<ProjectDoubleItem> itemList;

    public ProjectDoingAdapter() {
        itemList = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_project_doing, parent, false);
        }

        RelativeLayout itemLeft_Layout = convertView.findViewById(R.id.itemLeft_Layout);
        ImageView iconLeft_Img = convertView.findViewById(R.id.iconLeft_Img);
        TextView dDayLeft_Text = convertView.findViewById(R.id.dDayLeft_Text);
        TextView titleLeft_Text = convertView.findViewById(R.id.titleLeft_Text);
        ProgressBar progressLeft_PrgBar = convertView.findViewById(R.id.progressLeft_PrgBar);
        TextView percentLeft_Text = convertView.findViewById(R.id.percentLeft_Text);
        Button itemLeft_Btn = convertView.findViewById(R.id.itemLeft_Btn);

        RelativeLayout itemRight_Layout = convertView.findViewById(R.id.itemRight_Layout);
        ImageView iconRight_Img = convertView.findViewById(R.id.iconRight_Img);
        TextView dDayRight_Text = convertView.findViewById(R.id.dDayRight_Text);
        TextView titleRight_Text = convertView.findViewById(R.id.titleRight_Text);
        ProgressBar progressRight_PrgBar = convertView.findViewById(R.id.progressRight_PrgBar);
        TextView percentRight_Text = convertView.findViewById(R.id.percentRight_Text);
        Button itemRight_Btn = convertView.findViewById(R.id.itemRight_Btn);

        ProjectDoubleItem item = itemList.get(position);

        if (item.idLeft != 0) {
            itemLeft_Layout.setVisibility(View.VISIBLE);

            switch (item.colorIdLeft) {
                case 1:
                    break;
            }
            /*
            iconLeft_Img.setImageResource(R.drawable.account);
            itemLeft_Layout.setBackgroundResource(R.color.dialog_bottom_sheet_item_select);
            itemLeft_Layout.setBackgroundResource(R.color.dialog_bottom_sheet_item_select);
            itemLeft_Layout.setBackgroundResource(R.color.dialog_bottom_sheet_item_select);
            */

        } else {
            itemLeft_Layout.setVisibility(View.INVISIBLE);
        }

        if (item.idRight != 0) {
            itemRight_Layout.setVisibility(View.VISIBLE);

            switch (item.colorIdRight) {
                case 1:
                    break;
            }

        } else {
            itemRight_Layout.setVisibility(View.INVISIBLE);
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

    public void addItem(String numberIdLeft, int idLeft, int colorIdLeft, String titleLeft, String contentLeft, boolean isDoneLeft, String deadlineLeft, int colorResIdLeft
            , String numberIdRight, int idRight, int colorIdRight, String titleRight, String contentRight, boolean isDoneRight, String deadlineRight, int colorResIdRight) {

        ProjectDoubleItem item = new ProjectDoubleItem();

        item.idLeft = idLeft;
        item.numberIdLeft = numberIdLeft;
        item.colorIdLeft = colorIdLeft;
        item.titleLeft = titleLeft;
        item.contentLeft = contentLeft;
        item.isDoneLeft = isDoneLeft;
        item.deadlineLeft = deadlineLeft;
        item.colorResIdLeft = colorResIdLeft;

        item.idRight = idRight;
        item.numberIdRight = numberIdRight;
        item.colorIdRight = colorIdRight;
        item.titleRight = titleRight;
        item.contentRight = contentRight;
        item.isDoneRight = isDoneRight;
        item.deadlineRight = deadlineRight;
        item.colorResIdRight = colorResIdRight;

        itemList.add(item);
    }
}