package com.origincurly.toodletoodle.list;

import android.content.Context;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.R;

import java.util.List;

public class TodayWorkAdapter extends RecyclerView.Adapter<TodayWorkAdapter.ViewHolder> implements GlobalValue {
    Context context;
    List<TodayWorkCardViewItem> items;

    public TodayWorkAdapter(Context context, List<TodayWorkCardViewItem> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public TodayWorkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayWorkAdapter.ViewHolder holder, int position) {

        final TodayWorkCardViewItem item = items.get(position);
        holder.project_title_textView.setText(item.cardViewTitle);
        holder.title_textView.setText(item.projectTitle);
        setImportance(holder, item.importance);
        setTodo(holder, item.toDoItems);

        holder.work_add_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TodayWorkToDoItem toDoItem = new TodayWorkToDoItem();
                toDoItem.content = holder.work_add_action_editText.getText().toString();
                item.toDoItems.add(toDoItem);
                setTodo(holder, item.toDoItems);
            }
        });

        //아이템의 첫 번째, 마지막 아이템 마진 주기 위한 코드
        if(position == 0 || position == items.size() - 1) {
            holder.today_work_cardView_item_layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            FrameLayout.LayoutParams mLayoutParam = (FrameLayout.LayoutParams) holder.today_work_cardView_item_layout.getLayoutParams();

            if(position == 0) {
                mLayoutParam.leftMargin = (screenWidth - holder.today_work_cardView_item_layout.getMeasuredWidthAndState()) / 2;
            }
            else {
                mLayoutParam.rightMargin = (screenWidth - holder.today_work_cardView_item_layout.getMeasuredWidthAndState()) / 2;
            }
        }
    }

    private void setTodo(ViewHolder holder, List<TodayWorkToDoItem> toDoItems) {

        holder.today_work_todo_LinearLayout.removeAllViews();

        for (int i = 0; i < toDoItems.size(); i++) {
            View item = LayoutInflater.from(context).inflate(R.layout.item_today_cardview_action_row, null, false);
            TextView textView = item.findViewById(R.id.cardView_todo_scrollView_LinearLayout_Row_Content);
            textView.setText(toDoItems.get(i).content);
            ImageView imageView = item.findViewById(R.id.today_work_cardView_cardView_done_imageView);
            Button button = item.findViewById(R.id.today_work_cardView_cardView_done_button);
            RelativeLayout doneLayout = item.findViewById(R.id.today_work_cardView_Row_done_RelativeLayout);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View paramView) {
                    if(imageView.getVisibility() == View.VISIBLE) {
                        imageView.setVisibility(View.GONE);
                        doneLayout.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        imageView.setVisibility(View.VISIBLE);
                        doneLayout.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                    }
                }
            });

            holder.today_work_todo_LinearLayout.addView(item);
        }
    }



    private void setImportance(TodayWorkAdapter.ViewHolder holder, int importance) {

        holder.importance_imageView1.setVisibility(View.GONE);
        holder.importance_imageView2.setVisibility(View.GONE);
        holder.importance_imageView3.setVisibility(View.GONE);

        switch (importance) {
            case 1:
                holder.importance_imageView1.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.importance_imageView2.setVisibility(View.VISIBLE);
                break;
            case 3:
                holder.importance_imageView3.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout today_work_cardView_item_layout;
        ImageView importance_imageView1;
        ImageView importance_imageView2;
        ImageView importance_imageView3;
        Button edit_button;
        TextView project_title_textView;
        TextView title_textView;
        EditText work_add_action_editText;
        Button work_add_action_button;
        ScrollView today_work_todo_scrollView;
        LinearLayout today_work_todo_LinearLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.today_work_cardView);
            today_work_cardView_item_layout = itemView.findViewById(R.id.today_work_cardView_item_layout);
            importance_imageView1 = itemView.findViewById(R.id.today_work_cardView_importance1);
            importance_imageView2 = itemView.findViewById(R.id.today_work_cardView_importance2);
            importance_imageView3 = itemView.findViewById(R.id.today_work_cardView_importance3);
            edit_button = itemView.findViewById(R.id.today_work_cardView_edit_button);
            today_work_todo_scrollView = itemView.findViewById(R.id.cardView_todo_scrollView);
            today_work_todo_LinearLayout = itemView.findViewById(R.id.cardView_todo_scrollView_LinearLayout);
            project_title_textView = itemView.findViewById(R.id.today_work_cardView_project_title);
            title_textView = itemView.findViewById(R.id.today_work_cardView_card_title);
            work_add_action_editText = itemView.findViewById(R.id.today_work_cardView_add_action_editText);
            work_add_action_button = itemView.findViewById(R.id.today_work_cardView_add_action_button);
        }
    }
}


