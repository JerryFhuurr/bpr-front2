package com.bpr.front2.home.user.course.resource;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bpr.front2.R;
import com.bpr.front2.home.user.teacher.uploads.UploadItemVideoModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoPlayerFragment extends Fragment {
    private TextView titleLabel;
    private TextView warningLabel;
    private VideoView videoView;
    private Button downloadButton;
    private UploadItemVideoModel uploadItemVideoModel;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadItemVideoModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(UploadItemVideoModel.class);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video_player, container, false);
        titleLabel = v.findViewById(R.id.video_title);
        videoView = v.findViewById(R.id.detail_video_view);
        downloadButton = v.findViewById(R.id.download_video_button);
        warningLabel = v.findViewById(R.id.warning_label);

        if (!checkFile()) {
            downloadVideo(2);
        } else {
            setUpPlayer();
        }

        titleLabel.setText(uploadItemVideoModel.getUploadItem().videoFileName);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadVideo(1);
            }
        });
        return v;
    }


    private Boolean checkFile() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/tempVideo/"
                + uploadItemVideoModel.getUploadItem().videoFileName + ".mp4";
        File file = new File(path);
        return file.exists();
    }

    private void setUpPlayer() {
        Log.i(TAG, "try to set up player");
        Toast.makeText(getContext(), "Laoding complete!", Toast.LENGTH_SHORT).show();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/tempVideo/"
                + uploadItemVideoModel.getUploadItem().videoFileName + ".mp4";
        videoView.setVideoPath(path);
        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        videoView.requestFocus();
        warningLabel.setText("");
    }


    private void downloadVideo(int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = uploadItemVideoModel.getUploadItem().videoFileDownload;
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
                        File file = null;
                        if (type == 1) {
                            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download",
                                    uploadItemVideoModel.getUploadItem().videoFileName + ".mp4");
                        } else if (type == 2) {
                            warningLabel.setText("Loading video, please wait...");
                            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/tempVideo",
                                    uploadItemVideoModel.getUploadItem().videoFileName + ".mp4");
                        }
                        Log.i(ContentValues.TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
                        FileOutputStream outputStream = new FileOutputStream(file);
                        int len = 0;
                        byte[] bytes = new byte[1024 * 10];
                        while ((len = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, len);
                        }
                        Log.i(TAG, "file write ok");
                        inputStream.close();
                        outputStream.close();


                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (type == 1) {
                                    Toast.makeText(getContext(), "Download OK", Toast.LENGTH_SHORT).show();
                                } else if (type == 2) {
                                    setUpPlayer();
                                    Thread.currentThread().interrupt();
                                }

                            }
                        });
                    }

                });
            }

        }).start();
    }
}