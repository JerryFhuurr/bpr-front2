package com.bpr.front2.home.user.teacher.uploadPage;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.GeneralUtils;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.handler.UriUtils;
import com.bpr.front2.home.user.course.CourseItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private ArrayList<File> filesUpload;

    public UploadResFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseItems = new ArrayList<>();
        filesUpload = new ArrayList<>(2);
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

        Log.i(TAG, "file:" + filesUpload.size());
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
                startActivityForResult(intent, 2);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userId = sharedPreferences.getInt("userId", 0);
                String roleName = sharedPreferences.getString("role", "");
                String title = fileTitleEdit.getText().toString().trim();
                String desc = dscEdit.getText().toString().trim();
                postRequest(userId, roleName, title, desc);
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
                    checkFileList(1);
                } else {
                    fileTitleText.setText(file.getName());
                    checkFileList(2);
                }
                filesUpload.add(file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void checkFileList(int code) {
        if (code == 1) {
            for (File f: filesUpload) {
                if (f.getName().endsWith(".mp4") || f.getName().endsWith(".mp4")) {
                    filesUpload.remove(f);
                }
            }
        } else if (code == 2) {
            for (File f: filesUpload) {
                if (f.getName().endsWith(".mp4") || f.getName().endsWith(".mp4")) {
                    continue;
                } else filesUpload.remove(f);
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


    private void postRequest(int userId, String roleName, String videoTitle, String videoDescription) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MultipartBody multipartBody;
                if (filesUpload.size() == 1) {
                    multipartBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM) //set as form-data
                            .addFormDataPart("userId", String.valueOf(userId))
                            .addFormDataPart("courseId", String.valueOf(courseItem.getId()))
                            .addFormDataPart("roleId", String.valueOf(GeneralUtils.getRoleId(roleName)))
                            .addFormDataPart("videoTitle", videoTitle)
                            .addFormDataPart("videoDescription", videoDescription)
                            .addFormDataPart("files", filesUpload.get(0).getName(),
                                    RequestBody.create(MediaType.parse("application/octet-stream")
                                    , filesUpload.get(0)))
                            .build();

                } else {
                    multipartBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM) //set as form-data
                            .addFormDataPart("userId", String.valueOf(userId))
                            .addFormDataPart("courseId", String.valueOf(courseItem.getId()))
                            .addFormDataPart("roleId", String.valueOf(GeneralUtils.getRoleId(roleName)))
                            .addFormDataPart("videoTitle", videoTitle)
                            .addFormDataPart("videoDescription", videoDescription)
                            .addFormDataPart("files", filesUpload.get(0).getName(),
                                    RequestBody.create(MediaType.parse("multipart/form-data")
                                            , filesUpload.get(0)))
                            .addFormDataPart("files", filesUpload.get(1).getName(),
                                    RequestBody.create(MediaType.parse("multipart/form-data"),
                                            filesUpload.get(1)))
                            .build();
                }
                Log.i(TAG, "upload:" + filesUpload.size());
                Log.i(TAG, "upload:" + multipartBody.toString());
                String path = HttpUtils.baseUrl1 + "/video/upload/";
                Request request = new Request.Builder()
                        .url(path)
                        .post(multipartBody)
                        .build();

                uploadButton.setText("Uploading...");
                uploadButton.setClickable(false);

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        getUploadResult(responseBody);
                    }
                } catch (IOException e) {
                    Looper.loop();
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void getUploadResult(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.contains("successfully")) {
                    Toast.makeText(requireContext(), R.string.upload_success, Toast.LENGTH_SHORT).show();
                    filesUpload.clear();
                    NavHostFragment.findNavController(UploadResFragment.this)
                            .navigate(R.id.action_uploadResFragment_to_teacherFragment);
                } else {
                    Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Unexpected code " + response);
                }
            }
        });
    }
}
