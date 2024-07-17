package com.bpr.front2.home.user.course.resource;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.handler.OnDownloadListener;
import com.bpr.front2.home.user.teacher.uploads.UploadItem;
import com.bpr.front2.home.user.teacher.uploads.UploadItemVideoModel;
import com.bpr.front2.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResDetailFragment extends Fragment {
    private EditText titleText;
    private TextView rateText;
    private EditText descText;
    private TextView videoNameText;
    private TextView fileNameText, fileDownloadLabel;
    private ProgressBar downloadBar;
    private Button playVideoButton;
    private Button downloadFileButton;
    private Button editButton;
    private Button removeButton;
    private Button commentButton;
    private UploadItem uploadItem;
    private UploadItemVideoModel uploadItemVideoModel;


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
        editButton = v.findViewById(R.id.edit_title_button);
        removeButton = v.findViewById(R.id.delete_video_button);
        commentButton = v.findViewById(R.id.go_to_comments);
        downloadBar = v.findViewById(R.id.file_download_bar);
        fileDownloadLabel = v.findViewById(R.id.file_download_progress_label);

        titleText.setInputType(EditorInfo.TYPE_NULL);
        descText.setInputType(EditorInfo.TYPE_NULL);

        SharedPreferences s = requireActivity().getSharedPreferences("user",MODE_PRIVATE);
        String role = s.getString("role", "student");
        if (role.equals("student")) {
            editButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.GONE);
        }

        getVideoItem();
        downloadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(new OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        fileDownloadLabel.setText(R.string.download_ok);
                    }

                    @Override
                    public void onDownloading(int progress) {
                        fileDownloadLabel.setText(progress + "%");
                        downloadBar.setProgress(progress);
                    }

                    @Override
                    public void onDownloadFailed() {
                        fileDownloadLabel.setText(R.string.download_fail);
                    }
                });
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

                removeButton.setVisibility(View.GONE);
                editButton.setText(R.string.edit_detail_apply_button);

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

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder removeVideo = new AlertDialog.Builder(requireContext());
                removeVideo.setTitle(R.string.detail_remove_dia_title);
                removeVideo.setMessage(R.string.detail_remove_dia_content);
                removeVideo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeVideo();
                    }
                });
                removeVideo.show();
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ResDetailFragment.this)
                        .navigate(R.id.action_resDetailFragment_to_commentFragment);
            }
        });

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
                    uploadItem.roleId = o.getInt("roleId");
                    uploadItem.videoScore = o.getInt("videoScore");
                    uploadItem.videoDescription = o.getString("videoDescription");
                    uploadItem.videoPath = o.getString("videoPath");
                    uploadItem.fileUrl = o.getString("fileUrl");
                    uploadItem.videoFileName = o.getString("videoFileName");
                    uploadItem.fileName = o.getString("fileName");
                    uploadItem.videoFileDownload = o.getString("videoFileDownload");
                    uploadItem.fileNameDownload = o.getString("fileNameDownload");
                    uploadItem.videoSize = o.getLong("videoSize");
                    uploadItem.fileSize = o.getLong("fileSize");
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
            fileNameText.setText(R.string.file_not_found);
            downloadFileButton.setVisibility(View.GONE);
            downloadBar.setVisibility(View.GONE);
            fileDownloadLabel.setVisibility(View.GONE);
        }

        SharedPreferences s = requireActivity().getSharedPreferences("user",MODE_PRIVATE);
        int id = s.getInt("userId", 0);
        addHistory(id);
    }

    private void downloadFile(final OnDownloadListener listener) {
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

                        byte[] buf = new byte[2048];
                        if (!file.exists()) {
                            long total = uploadItemVideoModel.getUploadItem().fileSize;
                            Log.i(ContentValues.TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
                            FileOutputStream outputStream = new FileOutputStream(file);
                            int len = 0;
                            long sum = 0;
                            while ((len = inputStream.read(buf)) != -1) {
                                outputStream.write(buf, 0, len);
                                sum += len;
                                int progress = (int) (sum * 1.0f / total * 100);
                                // 下载中
                                listener.onDownloading(progress);
                                Log.i(MotionEffect.TAG, "progress:" + progress + ",sum=" + sum + ",total=" + total);
                            }
                            outputStream.flush();
                            // 下载完成
                            listener.onDownloadSuccess();
                            Log.i(MotionEffect.TAG, "file write ok");
                            inputStream.close();
                            outputStream.close();
                        }
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

                        removeButton.setVisibility(View.VISIBLE);
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

    private void removeVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String path = HttpUtils.baseUrl1 + "/video/remove?videoId=" + uploadItem.videoId;
                Request request = new Request.Builder().url(path).delete().build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        if (responseBody.contains("Successfully")) {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Resource remove!", Toast.LENGTH_LONG).show();
                                    NavHostFragment.findNavController(ResDetailFragment.this)
                                            .navigate(R.id.action_resDetailFragment_to_teacherFragment);
                                }
                            });

                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "No Internet connect!", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void addHistory(int watcherId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Map map = new HashMap();
                map.put("userId", uploadItem.userId);
                map.put("courseId", uploadItem.courseId);
                map.put("videoId", uploadItem.videoId);
                map.put("roleId", uploadItem.roleId);
                map.put("watcherId", watcherId);
                JSONObject jo = new JSONObject(map);
                RequestBody requestBody = RequestBody.create(MediaType.parse(
                        "application/json; charset=utf-8"
                ), jo.toString());
                Log.i(TAG, "req: " + jo.toString());
                String path = HttpUtils.baseUrl1 + "/history/add";
                Request request = new Request.Builder()
                        .url(path)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                    }
                } catch (IOException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}