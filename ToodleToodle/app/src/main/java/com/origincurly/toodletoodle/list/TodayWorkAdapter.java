package com.origincurly.toodletoodle.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
    }

    private void setTodo(ViewHolder holder, List<TodayWorkToDoItem> toDoItems) {

        //TODO UI Thread 호출해서 layout inflater 로 뷰 띄워줄 것

//        holder.today_work_todo_LinearLayout.removeAllViews();
//        toDoItems.forEach(toDoItem -> {
//                    View item_today_todo = LayoutInflater.from(context).inflate(R.layout.item_today_cardview_action_row, null, false);
//                    TextView textView = (TextView)item_today_todo.findViewById(R.id.cardView_todo_scrollView_LinearLayout_Row_Content);
//                    textView.setText(toDoItem.content);
//                    holder.today_work_todo_LinearLayout.addView(textView);
//                });
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


