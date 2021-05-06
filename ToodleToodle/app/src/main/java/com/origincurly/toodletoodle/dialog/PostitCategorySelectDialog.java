package com.origincurly.toodletoodle.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.origincurly.toodletoodle.R;
import com.origincurly.toodletoodle.list.PostitCategoryMiniAdapter;
import com.origincurly.toodletoodle.list.PostitCategoryItem;

import java.util.ArrayList;

public class PostitCategorySelectDialog extends BottomSheetDialogFragment {

    private ArrayList<PostitCategoryItem> itemList;
    private int selectPostitCategoryId;
    private AdapterView.OnItemClickListener postitCategoryItemClicked;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_postit_category_select, container, false);

        ListView postitCategory_ListView = v.findViewById(R.id.postitCategory_ListView);

        PostitCategoryMiniAdapter postitCategoryMiniAdapter = new PostitCategoryMiniAdapter(itemList, selectPostitCategoryId);
        postitCategory_ListView.setDividerHeight(0);
        postitCategory_ListView.setAdapter(postitCategoryMiniAdapter);
        postitCategory_ListView.setOnItemClickListener(postitCategoryItemClicked);

        return v;
    }

    public PostitCategorySelectDialog(ArrayList<PostitCategoryItem> itemList, int selectPostitCategoryId, AdapterView.OnItemClickListener postitCategoryItemClicked) {
        this.itemList = itemList;
        this.selectPostitCategoryId = selectPostitCategoryId;
        this.postitCategoryItemClicked = postitCategoryItemClicked;
    }
}