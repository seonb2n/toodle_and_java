package com.origincurly.toodletoodle.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.R;

import java.util.ArrayList;

import static com.origincurly.toodletoodle.util.TransformUtils.iconId2IconResId;

public class PostitHorizontalAdapter extends RecyclerView.Adapter<PostitHorizontalAdapter.ViewHolder> implements GlobalValue {

    private Context context;
    private View.OnClickListener onClickItem;

    private ArrayList<PostitCategoryItem> categoryList;
    private ArrayList<PostitItem> itemList;

    public PostitHorizontalAdapter(Context context, View.OnClickListener onClickItem) {
        this.context = context;
        this.onClickItem = onClickItem;

        categoryList = new ArrayList<>();
        itemList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_postit_horizontal, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PostitItem item = itemList.get(position);

        holder.item_Layout.setOnClickListener(onClickItem);
        holder.icon_Img.setImageResource(item.iconResId);
        holder.title_Txt.setText(item.title);

        //holder.textview.setTag(item);
        if (position == 0) {
            holder.start_View.setVisibility(View.VISIBLE);
            holder.end_View.setVisibility(View.GONE);

        } else if (position == itemList.size()-1) {
            holder.start_View.setVisibility(View.GONE);
            holder.end_View.setVisibility(View.VISIBLE);

        } else {
            holder.start_View.setVisibility(View.GONE);
            holder.end_View.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout item_Layout;
        public ImageView icon_Img;
        public TextView title_Txt;
        public View start_View, end_View;

        public ViewHolder(View itemView) {
            super(itemView);

            item_Layout = itemView.findViewById(R.id.item_Layout);
            icon_Img = itemView.findViewById(R.id.icon_Img);
            title_Txt = itemView.findViewById(R.id.title_Txt);
            start_View = itemView.findViewById(R.id.start_View);
            end_View = itemView.findViewById(R.id.end_View);
        }
    }

    public void addCategory(int category_id, int icon_id, String title, String content) {
        PostitCategoryItem item = new PostitCategoryItem();

        item.id = category_id;
        item.iconId = icon_id;
        item.title = title;
        item.content = content;

        item.iconResId = iconId2IconResId(item.iconId);

        categoryList.add(item);
    }

    public void addItem(int category_id, String title, String content, double time) {
        PostitItem item = new PostitItem();

        item.categoryId = category_id;
        item.title = title;
        item.content = content;
        item.time = time;

        item.iconId = 0;
        item.iconResId = iconId2IconResId(item.iconId);
        item.state = POSTIT_STATE_NORMAL;

        for (PostitCategoryItem categoryItem : categoryList) {
            if (item.categoryId == categoryItem.id) {
                item.iconId = categoryItem.iconId;
                item.iconResId = categoryItem.iconResId;
                break;
            }
        }

        itemList.add(0, item);
    }

    public void cleanAll() {
        categoryList.clear();
        itemList.clear();
    }
}