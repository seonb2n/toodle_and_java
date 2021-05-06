package com.origincurly.toodletoodle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.origincurly.toodletoodle.list.ProjectDoneAdapter;
import com.origincurly.toodletoodle.list.ProjectItem;
import com.origincurly.toodletoodle.util.ErrorCodeEnum;
import com.origincurly.toodletoodle.util.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.origincurly.toodletoodle.util.ErrorCodeEnum.CODE_JSON_ERROR;
import static com.origincurly.toodletoodle.util.ErrorCodeEnum.int2Enum;

public class ProjectDoneActivity extends BasicActivity {

    //region View

    private TextView npTaskCount_Txt;
    private ListView project_ListView;
    private RelativeLayout projectNull_Layout;
    private TextView projectCount_Txt;

    //endregion

    //region Variable

    private ProjectDoneAdapter projectDoneAdapter;
    private ArrayList<ProjectItem> projectItems;

    private int projectIndex;
    private String nowNumberId;
    private int nowProjectId;

    private int projectCount;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_done);
        setActivity(this, this);

        init();
    }

    private void init() {
        npTaskCount_Txt = findViewById(R.id.npTaskCount_Txt);
        project_ListView = findViewById(R.id.project_ListView);
        projectNull_Layout = findViewById(R.id.projectNull_Layout);
        projectCount_Txt = findViewById(R.id.projectCount_Txt);

        project_ListView.setDividerHeight(0);
        project_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjectItem item = projectItems.get(position);

                Intent intent = new Intent(mContext, ProjectDoneDetailActivity.class);

                intent.putExtra("number_id", item.numberId);
                intent.putExtra("project_id", item.id);

                startActivityIntent(intent, R.anim.animation_fade_in, R.anim.animation_stop_short);

                /**
                 * 수신 part
                 *
                 Intent intent = getIntent();
                 startPage = Integer.parseInt(intent.getStringExtra("startPage"));
                 */
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        new ProjectListTask().execute();
    }

    //region Button

    public void ProjectAddClicked(View v) {
        startActivityClass(ProjectAddActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
    }

    public void DoingClicked(View v) {
        startActivityClass(ProjectDoingActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
    }

    //endregion

    //region Api
    private class ProjectListTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + userPartition + "/project_id_list.php?mode=one_uuid");
                RequestBody formBody = new FormBody.Builder()
                        .add("user_uuid", userUuid)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        String content_json = jsonObject.getString("content_json");
                        JSONArray jsonArray = new JSONArray(content_json);
                        setProjectList(jsonArray);
                        break;

                    case CODE_PROJECT_ID_LIST_WRONG:
                        showToastMessage(R.string.msg_db_check_need);
                        startActivityClass(DbCheckActivity.class, R.anim.animation_fade_in, R.anim.animation_stop_short);
                        break;

                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }
    private void setProjectList(JSONArray jsonArray) {
        projectDoneAdapter = new ProjectDoneAdapter();
        projectItems = new ArrayList<>();
        projectIndex = 0;
        projectCount = 0;

        if (jsonArray.length() > 0) {
            projectNull_Layout.setVisibility(View.GONE);

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    ProjectItem item = new ProjectItem();

                    item.numberId = jsonObject.getString("number_id");
                    item.id = jsonObject.getInt("project_id");

                    projectItems.add(item);

                } catch (JSONException e) { Log.d(TAG, "unexpected JSON exception", e); }
            }

            getProjectData();

        } else {
            projectNull_Layout.setVisibility(View.VISIBLE);
        }
    }

    private void getProjectData() {
        if (projectItems.size() == projectIndex) {
            if (projectCount == 0) {
                projectNull_Layout.setVisibility(View.VISIBLE);

            } else {
                projectNull_Layout.setVisibility(View.GONE);
                project_ListView.setAdapter(projectDoneAdapter);
                //TODO projectCount 입력

            }

        } else {
            ProjectItem item = projectItems.get(projectIndex);

            nowNumberId = item.numberId;
            nowProjectId = item.id;

            new ProjectTask().execute();
        }
    }

    private class ProjectTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = NetworkManager.getInstance(mContext).getClient();

            try {
                URL url = new URL(apiUrl + nowNumberId + "/project.php?mode=one_uuid");
                RequestBody formBody = new FormBody.Builder()
                        .add("id", String.valueOf(nowProjectId))
                        .add("user_uuid", userUuid)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.d(TAG, "Task Result:"+value);

            try {
                JSONObject jsonObject = new JSONObject(value);
                int errorCode = jsonObject.getInt("code");
                ErrorCodeEnum errorCodeEnum = int2Enum(errorCode);

                switch (errorCodeEnum) {
                    case CODE_NORMAL:
                        String title = jsonObject.getString("title");
                        boolean isDone = (jsonObject.getInt("done") == PROJECT_DONE);
                        int taskTotal = jsonObject.getInt("task_total");
                        int taskDone = jsonObject.getInt("task_done");
                        String updateTime = jsonObject.getString("update_time");

                        if (isDone) {
                            projectCount++;
                            projectDoneAdapter.addItem(nowNumberId, nowProjectId, title, taskTotal, taskDone, updateTime);
                        }

                        projectIndex++;
                        getProjectData();
                        break;

                    case CODE_PROJECT_WRONG:
                    case CODE_PROJECT_AUTH_WRONG:
                        projectIndex++;
                        getProjectData();
                        break;

                    default:
                        showServerErrorRedo(errorCodeEnum, errorCode);
                        break;
                }

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                showServerErrorRedo(CODE_JSON_ERROR);
            }
        }
    }

    //endregion

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            exitApp();
        }

        return true;
    }
}
