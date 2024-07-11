package com.bpr.front2.home.user.course.resource;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
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

        setUpVideoPlayer();

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadVideo();
            }
        });
        return v;
    }


    private void setUpVideoPlayer() {
        String path = uploadItemVideoModel.getUploadItem().videoFileDownload;
        Log.i(TAG, uploadItemVideoModel.getUploadItem().toString());
        videoView.setVideoPath(path);
        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        videoView.requestFocus();

        Log.i(TAG, "path:" + path);
    }

    private void downloadVideo() {
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
                        final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download",
                                uploadItemVideoModel.getUploadItem().videoFileName + ".mp4");
                        Log.i(ContentValues.TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
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
}