package com.origincurly.toodletoodle.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.R;

import java.util.ArrayList;

import static com.origincurly.toodletoodle.util.TransformUtils.iconId2IconResId;

public class TaskDuringHorizontalAdapter extends RecyclerView.Adapter<TaskDuringHorizontalAdapter.ViewHolder> implements GlobalValue {

    private Context context;
    private View.OnClickListener onClickItem;

    private int selectDuring;
    private ArrayList<Integer> itemList;

    public TaskDuringHorizontalAdapter(Context context, View.OnClickListener onClickItem) {
        this.context = context;
        this.onClickItem = onClickItem;

        itemList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_during_horizontal, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Integer item = itemList.get(position);

        holder.item_Layout.setOnClickListener(onClickItem);
        holder.item_Layout.setTag(item);
        if (item == TASK_DURING_NO) {
            holder.day_Txt.setText(R.string.add_task_set_date_no);

        } else {
            String dayString = String.format(context.getString(R.string.add_task_end_date_day), item);
            holder.day_Txt.setText(dayString);

        }

        if (item == selectDuring) {
            holder.item_Layout.setBackgroundResource(R.drawable.custom_task_during_item_select_back);
            holder.day_Txt.setTextColor(context.getColor(R.color.task_during_item_select_txt));

        } else {
            holder.item_Layout.setBackgroundResource(R.drawable.custom_task_during_item_back);
            holder.day_Txt.setTextColor(context.getColor(R.color.task_during_item_txt));

        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout item_Layout;
        public TextView day_Txt;

        public ViewHolder(View itemView) {
            super(itemView);

            item_Layout = itemView.findViewById(R.id.item_Layout);
            day_Txt = itemView.findViewById(R.id.day_Txt);
        }
    }

    public void addItem(int item) {
        itemList.add(item);
    }

    public void setSelectItem(int selectItem) {
        selectDuring = selectItem;
        notifyDataSetChanged();
    }
}