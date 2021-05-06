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

public class PostitCategoryMiniAdapter extends BaseAdapter implements GlobalValue {

    private ArrayList<PostitCategoryItem> itemList;
    private int selectPostitCategoryId;

    public PostitCategoryMiniAdapter(ArrayList<PostitCategoryItem> itemList, int selectPostitCategoryId) {
        this.itemList = itemList;
        this.selectPostitCategoryId = selectPostitCategoryId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_postit_category_mini, parent, false);
        }

        RelativeLayout item_Layout = convertView.findViewById(R.id.item_Layout);
        RelativeLayout postitCategory_Layout = convertView.findViewById(R.id.postitCategory_Layout);
        TextView noPostitCategory_Txt = convertView.findViewById(R.id.noProject_Txt);

        ImageView icon_Img = convertView.findViewById(R.id.icon_Img);
        TextView title_Txt = convertView.findViewById(R.id.title_Txt);
        TextView content_Txt = convertView.findViewById(R.id.content_Txt);
        ImageView check_Img = convertView.findViewById(R.id.check_Img);

        PostitCategoryItem item = itemList.get(position);

        icon_Img.setImageResource(item.iconResId);
        title_Txt.setText(item.title);
        content_Txt.setText(item.content);

        if (item.id == POSTIT_CATEGORY_NO_ID) {
            postitCategory_Layout.setVisibility(View.GONE);
            noPostitCategory_Txt.setVisibility(View.VISIBLE);

        } else {
            postitCategory_Layout.setVisibility(View.VISIBLE);
            noPostitCategory_Txt.setVisibility(View.GONE);

        }

        if (item.id == selectPostitCategoryId) {
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