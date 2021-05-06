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
import com.origincurly.toodletoodle.list.ProjectItem;
import com.origincurly.toodletoodle.list.ProjectMiniAdapter;

import java.util.ArrayList;

public class ProjectSelectDialog extends BottomSheetDialogFragment {

    private ArrayList<ProjectItem> itemList;
    private int selectProjectId;
    private AdapterView.OnItemClickListener projectItemClicked;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_project_select, container, false);

        ListView project_LisView = v.findViewById(R.id.project_LisView);

        ProjectMiniAdapter projectMiniAdapter = new ProjectMiniAdapter(itemList, selectProjectId);
        project_LisView.setDividerHeight(0);
        project_LisView.setAdapter(projectMiniAdapter);
        project_LisView.setOnItemClickListener(projectItemClicked);

        return v;
    }

    public ProjectSelectDialog(ArrayList<ProjectItem> itemList, int selectProjectId, AdapterView.OnItemClickListener projectItemClicked) {
        this.itemList = itemList;
        this.selectProjectId = selectProjectId;
        this.projectItemClicked = projectItemClicked;
    }
}