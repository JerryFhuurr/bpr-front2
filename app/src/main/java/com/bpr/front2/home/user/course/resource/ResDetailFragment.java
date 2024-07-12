package com.bpr.front2.home.user.course.resource;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.teacher.uploadPage.UploadFileItem;
import com.bpr.front2.home.user.teacher.uploads.UploadItem;
import com.bpr.front2.home.user.teacher.uploads.UploadItemVideoModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResDetailFragment extends Fragment {
    private EditText titleText;
    private TextView rateText;
    private EditText descText;
    private TextView videoNameText;
    private TextView fileNameText;
    private Button playVideoButton;
    private Button downloadFileButton;
    private Button editButton;
    private Button remvoeButton;
    private SwipeRefreshLayout commentRefresh;
    private RecyclerView commentRecycler;
    private Button sendButton;
    private UploadItem uploadItem;
    private UploadItemVideoModel uploadItemVideoModel;

    ArrayList<UploadFileItem> fileItems = new ArrayList<>();

    public ResDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadItem = new UploadItem();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_res_detail, container, false);
        uploadItemVideoModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(UploadItemVideoModel.class);
        titleText = v.findViewById(R.id.res_title_label);
        rateText = v.findViewById(R.id.video_rate_score_label);
        descText = v.findViewById(R.id.res_desc_label);
        videoNameText = v.findViewById(R.id.video_title);
        playVideoButton = v.findViewById(R.id.choose_video_button);
        fileNameText = v.findViewById(R.id.file_title);
        downloadFileButton = v.findViewById(R.id.choose_file_button);
        commentRecycler = v.findViewById(R.id.comment_recycler_view);
        commentRefresh = v.findViewById(R.id.comment_refresh_view);
        sendButton = v.findViewById(R.id.commit_comment_button);
        editButton = v.findViewById(R.id.edit_title_button);
        remvoeButton = v.findViewById(R.id.delete_video_button);

        titleText.setInputType(EditorInfo.TYPE_NULL);
        descText.setInputType(EditorInfo.TYPE_NULL);

        SharedPreferences s = requireActivity().getSharedPreferences("user",MODE_PRIVATE);
        String role = s.getString("role", "student");
        if (role.equals("student")) {
            editButton.setVisibility(View.GONE);
            remvoeButton.setVisibility(View.GONE);
        }

        getVideoItem();
        downloadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
            }
        });

        playVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ResDetailFragment.this)
                        .navigate(R.id.action_resDetailFragment_to_videoPlayerFragment);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                descText.setInputType(EditorInfo.TYPE_CLASS_TEXT);

                remvoeButton.setVisibility(View.GONE);
                editButton.setText("Apply changes");

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String titleNew = titleText.getText().toString().trim();
                        String descNew = descText.getText().toString().trim();
                        if (titleNew.equals("")) {
                            titleNew = uploadItem.videoTitle;
                        }
                        uploadChange(titleNew, descNew);
                    }
                });
            }
        });
        // TODO 添加评论相关的adapter
        // TODO 添加发布评论的代码
        return v;
    }


    // HTTP
    private void getVideoItem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/video/get?videoId="
                        + uploadItemVideoModel.getUploadItem().videoId;
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setItem(responseBody);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setItem(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject o = new JSONObject(response);
                    uploadItem.videoTitle = o.getString("videoTitle");
                    uploadItem.videoId = o.getInt("videoId");
                    uploadItem.courseId = o.getInt("courseId");
                    uploadItem.userId = o.getInt("userId");
                    uploadItem.videoScore = o.getInt("videoScore");
                    uploadItem.videoDescription = o.getString("videoDescription");
                    uploadItem.videoPath = o.getString("videoPath");
                    uploadItem.fileUrl = o.getString("fileUrl");
                    uploadItem.videoFileName = o.getString("videoFileName");
                    uploadItem.fileName = o.getString("fileName");
                    uploadItem.videoFileDownload = o.getString("videoFileDownload");
                    uploadItem.fileNameDownload = o.getString("fileNameDownload");
                    uploadItemVideoModel.setUploadItem(uploadItem);
                    setTextView();
                } catch (JSONException e) {
                    Log.e(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void setTextView() {
        titleText.setText(uploadItem.videoTitle);
        descText.setText(uploadItem.videoDescription);
        videoNameText.setText(uploadItem.videoFileName);
        rateText.setText(String.valueOf(uploadItem.videoScore));
        if (!uploadItem.fileUrl.equals("null")) {
            fileNameText.setText(uploadItem.fileName);
        } else {
            fileNameText.setText("No file");
            downloadFileButton.setVisibility(View.GONE);
        }
    }

    private void downloadFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = uploadItem.fileNameDownload;
                Request request = new Request.Builder().url(url).get().build();
                Call call = client.newCall(request);

                //3.发起异步请求
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream inputStream = response.body().byteStream();
                        final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download",
                                uploadItem.fileName);
                        Log.i(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
                        if (!file.exists()) {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            int len = 0;
                            byte[] bytes = new byte[1024 * 10];
                            while ((len = inputStream.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, len);
                            }
                            inputStream.close();
                            outputStream.close();
                        }

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Download OK", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                });
            }

        }).start();
    }

    private void uploadChange(String title, String desc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/video/update/info";
                RequestBody body = new FormBody.Builder()
                        .add("videoId", String.valueOf(uploadItem.videoId))
                        .add("userId", String.valueOf(uploadItem.userId))
                        .add("videoTitle", title)
                        .add("videoDescription", desc)
                        .build();

                Request request = new Request.Builder().url(url).put(body).build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        handleUpdateResponse(responseBody);
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "No Internet connect!", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void handleUpdateResponse(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (response.contains("Successfully")) {
                        titleText.setInputType(EditorInfo.TYPE_NULL);
                        descText.setInputType(EditorInfo.TYPE_NULL);

                        remvoeButton.setVisibility(View.VISIBLE);
                        editButton.setText(R.string.upload_edit);
                        getVideoItem();
                        Toast.makeText(getContext(), "Update applied", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

}