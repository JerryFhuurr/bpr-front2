package com.bpr.front2.home.user.course.resource;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bpr.front2.R;

public class VideoPlayerFragment extends Fragment {
    private TextView titleLabel;
    private VideoView videoView;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video_player, container, false);
        titleLabel = v.findViewById(R.id.video_title);
        videoView = v.findViewById(R.id.detail_video_view);


        setUpVideoPlayer();
        return v;
    }


    private void setUpVideoPlayer() {
        String path = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.test_video;
        videoView.setVideoURI(Uri.parse(path));
        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        videoView.requestFocus();

        Log.i(TAG, "path:" + path);
    }
}