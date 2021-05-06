package com.origincurly.toodletoodle.list;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.R;

import java.util.ArrayList;

import static com.origincurly.toodletoodle.util.TransformUtils.getDoubleNowTimeStamp;
import static com.origincurly.toodletoodle.util.TransformUtils.iconId2IconResId;
import static com.origincurly.toodletoodle.util.TransformUtils.timeStamp2String;

public class PostitVerticalAdapter extends BaseAdapter implements GlobalValue {

    private ArrayList<PostitCategoryItem> postitCategoryList;
    private ArrayList<PostitItem> itemList;
    private boolean isFirstAnimation;

    public PostitVerticalAdapter() {
        postitCategoryList = new ArrayList<>();
        itemList = new ArrayList<>();
        isFirstAnimation = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_postit_vertical, parent, false);
        }

        final RelativeLayout item_Layout = convertView.findViewById(R.id.item_Layout);
        ImageView icon_Img = convertView.findViewById(R.id.icon_Img);
        TextView title_Txt = convertView.findViewById(R.id.title_Txt);
        RelativeLayout basic_Layout = convertView.findViewById(R.id.basic_Layout);
        TextView time_Txt = convertView.findViewById(R.id.time_Txt);
        Button end_Btn = convertView.findViewById(R.id.end_Btn);
        RelativeLayout end_Layout = convertView.findViewById(R.id.end_Layout);
        Button revert_Btn = convertView.findViewById(R.id.revert_Btn);

        PostitItem item = itemList.get(position);

        icon_Img.setImageResource(item.iconResId);
        title_Txt.setText(item.title);
        time_Txt.setText(timeStamp2String(context, item.time, getDoubleNowTimeStamp()));

        if (item.state == POSTIT_STATE_NORMAL || item.state == POSTIT_STATE_NEW) {
            basic_Layout.setVisibility(View.VISIBLE);
            end_Layout.setVisibility(View.GONE);

        } else {
            basic_Layout.setVisibility(View.GONE);
            end_Layout.setVisibility(View.VISIBLE);

        }

        end_Btn.setTag(position);
        end_Btn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        int position = (int)v.getTag();
                        setItemState(position, POSTIT_STATE_END);
                        notifyDataSetChanged();
                    }
                }
        );

        revert_Btn.setTag(position);
        revert_Btn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        int position = (int)v.getTag();
                        setItemState(position, POSTIT_STATE_NORMAL);
                        notifyDataSetChanged();
                    }
                }
        );

        if (isFirstAnimation && position == 0) {
            isFirstAnimation = false;
            int colorFrom = context.getColor(R.color.postit_add_item_back);
            int colorTo = context.getColor(R.color.basic_back);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(POSTIT_ADD_ANIMATION_TIME);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    item_Layout.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
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

    public PostitCategoryItem getCategoryItem(int position) {
        return postitCategoryList.get(position);
    }

    public void addPostitCategory(int category_id, int icon_id, String title, String content) {
        PostitCategoryItem item = new PostitCategoryItem();

        item.id = category_id;
        item.iconId = icon_id;
        item.title = title;
        item.content = content;

        item.iconResId = iconId2IconResId(item.iconId);

        postitCategoryList.add(item);
    }

    public void addItem(int category_id, String title, String content, double time, int state) {
        PostitItem item = new PostitItem();

        item.categoryId = category_id;
        item.title = title;
        item.content = content;
        item.time = time;

        item.iconId = 0;
        item.iconResId = iconId2IconResId(item.iconId);
        item.state = state;

        for (PostitCategoryItem categoryItem : postitCategoryList) {
            if (item.categoryId == categoryItem.id) {
                item.iconId = categoryItem.iconId;
                item.iconResId = categoryItem.iconResId;
                break;
            }
        }

        itemList.add(0, item);
    }

    public void setItemState(int position, int state) {
        PostitItem item = itemList.get(position);

        item.state = state;

        itemList.set(position, item);
    }

    public void cleanAll() {
        postitCategoryList.clear();
        itemList.clear();
    }

    public boolean isEditItem() {
        boolean isEdit = false;
        for (PostitItem item : itemList) {
            if (item.state == POSTIT_STATE_END || item.state == POSTIT_STATE_NEW) {
                isEdit = true;
                break;
            }
        }

        return isEdit;
    }

    public ArrayList<PostitCategoryItem> getPostCategoryList() {
        return this.postitCategoryList;
    }

    public String getJsonArrayString() {
        StringBuilder stringBuilder = new StringBuilder("[");

        for (int i = itemList.size()-1; i >= 0; i--) {
            PostitItem item = itemList.get(i);
            if (item.state == POSTIT_STATE_NORMAL || item.state == POSTIT_STATE_NEW) {
                String itemString
                        = "{\"category_id\":\""
                        + item.categoryId
                        + "\", \"title\":\""
                        + item.title
                        + "\", \"content\":\""
                        + item.content
                        + "\", \"time\":"
                        + (long)item.time
                        + "},";
                stringBuilder.append(itemString);
            }
        }
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append("]");

        } else {
            stringBuilder = new StringBuilder("[{}]");
        }

        return stringBuilder.toString();
    }
    public void setFirstAnimation() {
        isFirstAnimation = true;
    }

}