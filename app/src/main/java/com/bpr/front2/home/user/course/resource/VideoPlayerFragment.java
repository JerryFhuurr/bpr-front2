package com.bpr.front2.home.user.course.resource;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bpr.front2.MainActivity;
import com.bpr.front2.R;
import com.bpr.front2.handler.OnDownloadListener;
import com.bpr.front2.home.user.teacher.uploads.UploadItemVideoModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoPlayerFragment extends Fragment {
    private TextView titleLabel, downloadProgress;
    private TextView warningLabel;
    private ProgressBar downloadBar;
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
        downloadBar = v.findViewById(R.id.video_download_bar);
        downloadProgress = v.findViewById(R.id.video_download_progress_label);

        if (!checkFile()) {
            downloadVideo(2, new OnDownloadListener() {
                @Override
                public void onDownloadSuccess() {
                    downloadProgress.setVisibility(View.GONE);
                    setUpPlayer();
                    Thread.currentThread().interrupt();
                }

                @Override
                public void onDownloading(int progress) {
                    downloadProgress.setText(progress + "%");
                    downloadBar.setProgress(progress);
                }

                @Override
                public void onDownloadFailed() {
                    downloadProgress.setText("Cannot download file!");
                }
            });
        } else {
            setUpPlayer();
        }

        titleLabel.setText(uploadItemVideoModel.getUploadItem().videoFileName);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadVideo(1, new OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        downloadProgress.setText("Download finished !");
                    }

                    @Override
                    public void onDownloading(int progress) {
                        downloadProgress.setText(progress + "%");
                        downloadBar.setProgress(progress);
                    }

                    @Override
                    public void onDownloadFailed() {
                        downloadProgress.setText("Cannot download file!");
                    }
                });
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


    private void downloadVideo(int type, final OnDownloadListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .readTimeout(3, TimeUnit.SECONDS)
                        .build();
                String url = uploadItemVideoModel.getUploadItem().videoFileDownload;
                Request request = new Request.Builder().url(url).get().build();
                Call call = client.newCall(request);

                //3.发起异步请求
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        listener.onDownloadFailed();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        createDownloadNotify();

                        InputStream inputStream = response.body().byteStream();
                        byte[] buf = new byte[2048];
                        File file = null;
                        if (type == 1) {
                            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download",
                                    uploadItemVideoModel.getUploadItem().videoFileName + ".mp4");
                        } else if (type == 2) {
                            warningLabel.setText(R.string.detail_loading);
                            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/tempVideo",
                                    uploadItemVideoModel.getUploadItem().videoFileName + ".mp4");
                        }
                        long total = uploadItemVideoModel.getUploadItem().videoSize;
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
                            Log.i(TAG, "progress:" + progress + ",sum=" + sum + ",total=" + total);
                        }
                        outputStream.flush();
                        // 下载完成
                        listener.onDownloadSuccess();
                        Log.i(TAG, "file write ok");
                        inputStream.close();
                        outputStream.close();

                    }

                });
            }

        }).start();
    }

    private void createDownloadNotify() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

        // 在 MainActivity 或其他合适的地方创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //申请通知权限
            if (ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }

            String channelId = "001";   //通知渠道的标识符
            CharSequence channelName = "Study";    //通知渠道的位置
            String channelDescription = "Message from Study";    //通知渠道的描述

            //设置通知渠道的级别
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            //创建通知渠道
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.setDescription(channelDescription);//可以省略


            //在系统中注册消息
            notificationManager.createNotificationChannel(notificationChannel);

            //创建通知
            Notification notification = new NotificationCompat.Builder(requireActivity(), "001")
                    .setContentTitle("Download start")    //消息的标题
                    .setContentText("Staring download, please wait")  //消息的内容
                    .setWhen(System.currentTimeMillis())    //指定通知被创建的时间
                    .setSmallIcon(R.drawable.notification_icon)    //通知的小图标
                    .setLargeIcon(BitmapFactory.decodeResource
                            (getResources(), R.drawable.notification_icon)) //通知的大图标
                    .build();

            //显示一个通知
            if (ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
            notificationManager.notify(1, notification);
        }
    }
}