package com.origincurly.toodletoodle.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.R;

import java.util.List;

public class TodayWorkAdapter extends RecyclerView.Adapter<TodayWorkAdapter.ViewHolder> implements GlobalValue{
    Context context;
    List<TodayWorkCardViewItem> items;
    int item_layout;

    public TodayWorkAdapter(Context context, List<TodayWorkCardViewItem> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public TodayWorkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayWorkAdapter.ViewHolder holder, int position) {
        final TodayWorkCardViewItem item = items.get(position);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        RecyclerView card_recyclerView;
        ImageView importance_imageView;
        Button edit_button;
        TextView project_title_textView;
        TextView title_textView;
        EditText work_add_action_editText;
        Button work_add_action_button;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.today_work_cardView);
            card_recyclerView = itemView.findViewById(R.id.today_work_RecyclerView);
            importance_imageView = itemView.findViewById(R.id.today_work_cardView_importance);
            edit_button = itemView.findViewById(R.id.today_work_cardView_edit_button);
            project_title_textView = itemView.findViewById(R.id.today_work_cardView_project_title);
            title_textView = itemView.findViewById(R.id.today_work_cardView_card_title);
            work_add_action_editText = itemView.findViewById(R.id.today_work_cardView_add_action_editText);
            work_add_action_button = itemView.findViewById(R.id.today_work_cardView_add_action_button);
        }
    }
}
