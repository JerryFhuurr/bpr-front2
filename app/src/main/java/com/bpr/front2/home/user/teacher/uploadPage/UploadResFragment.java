package com.bpr.front2.home.user.teacher.uploadPage;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.handler.UriUtils;
import com.bpr.front2.home.user.course.CourseItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class UploadResFragment extends Fragment {

    private EditText fileTitleEdit;
    private EditText dscEdit;
    private TextView videoTitleText;
    private TextView fileTitleText;
    private Spinner courseListSpinner;
    private Button chooseVideoButton;
    private Button chooseFileButton;
    private Button uploadButton;
    private static int REQUEST_CODE = 1;
    private ArrayList<CourseItem> courseItems;
    private CourseItem courseItem = new CourseItem();

    public UploadResFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseItems = new ArrayList<>();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload_res, container, false);
        fileTitleEdit = v.findViewById(R.id.file_title_edit);
        dscEdit = v.findViewById(R.id.file_desc_edit);
        courseListSpinner = v.findViewById(R.id.course_cat);
        uploadButton = v.findViewById(R.id.upload);
        chooseVideoButton = v.findViewById(R.id.choose_video_button);
        chooseFileButton = v.findViewById(R.id.choose_file_button);
        videoTitleText = v.findViewById(R.id.video_title);
        fileTitleText = v.findViewById(R.id.file_title);


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        getCourseList(username);
        //TODO 等后端完成后添加上传的逻辑代码

        // open system file to choose file
        chooseVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @NeedsPermission("Manifest.permission.READ_EXTERNAL_STORAGE")
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                //chooseFile.setType("*/*");//匹配所有的类型
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                chooseFile.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                Intent intent = Intent.createChooser(chooseFile, "title");
                startActivityForResult(intent, 1);
            }
        });

        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");//匹配所有的类型
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //chooseFile.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                Intent intent = Intent.createChooser(chooseFile, "title");
                startActivityForResult(intent, 1);
            }
        });
        return v;
    }

    String path;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                path = UriUtils.getFileAbsolutePath(getContext(), uri);
                File file = new File(path);
                Log.i(TAG, "path:" + path);
                if (path.endsWith(".mp4") || path.endsWith(".avi")) {
                    videoTitleText.setText(file.getName());
                } else {
                    fileTitleText.setText(file.getName());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void setCourseList() {
        int[] ids = new int[courseItems.size()];
        String[] courseNames = new String[courseItems.size()];
        for (int i = 0; i < courseItems.size(); i++) {
            CourseItem c = courseItems.get(i);
            ids[i] = c.getId();
            courseNames[i] = c.getCourseName();
        }
        Log.i(TAG, "ids:" + ids.length);
        courseListSpinner.setAdapter(new UploadFragmentCourseAdapter(ids, courseNames, requireContext()));
        courseListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                courseItem.setId(ids[i]);
                courseItem.setCourseName(courseNames[i]);
                Log.i(TAG, "select:" + courseItem.getCourseName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //HTTP
    private void getCourseList(String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/course/get/user?username=" + username;
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, "upload page: " + responseBody);
                        setCourseList(responseBody);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setCourseList(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    courseItems.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        CourseItem courseItem = new CourseItem();
                        courseItem.setId(o.getInt("courseId"));
                        courseItem.setCourseName(o.getString("courseName"));
                        courseItems.add(courseItem);
                    }
                    setCourseList();
                    Log.i(TAG, String.valueOf(courseItems.size()));
                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }


    private void postRequest() {

    }
}
