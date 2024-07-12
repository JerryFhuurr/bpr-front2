package com.bpr.front2.home.user.comment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.teacher.uploads.UploadItem;
import com.bpr.front2.home.user.teacher.uploads.UploadItemVideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentFragment extends Fragment {

    private EditText commentInput;
    private RatingBar ratingBar;
    private Button sendButton;
    private UploadItem uploadItem;
    private UploadItemVideoModel uploadItemVideoModel;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RecyclerView commentR;
    private SwipeRefreshLayout refresh;
    private CommentAdapter adapter;
    private LinearLayoutManager layoutManager;

    ArrayList<Comment> comments = new ArrayList<>();

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadItemVideoModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory())
                .get(UploadItemVideoModel.class);
        uploadItem = uploadItemVideoModel.getUploadItem();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_comment, container, false);
        refresh = v.findViewById(R.id.comment_refresh_view);
        commentR = v.findViewById(R.id.comment_recycler_view);
        sendButton = v.findViewById(R.id.commit_comment_button);
        ratingBar = v.findViewById(R.id.comment_rating);
        commentInput = v.findViewById(R.id.comment_edit);

        getComments(uploadItem.videoId);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });
        return v;
    }


    private void getComments(int videoId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/comment/get/all?videoId=" + videoId;
                Request request = new Request.Builder().url(url).get().build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setCommentList(responseBody);
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "No Internet connect!", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setCommentList(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    comments.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setCommentId(o.getInt("commentId"));
                        comment.setUserId(o.getInt("userId"));
                        comment.setCourseId(o.getInt("courseId"));
                        comment.setVideoId(o.getInt("videoId"));
                        comment.setRoleId(o.getInt("roleId"));
                        comment.setSenderId(o.getInt("senderId"));
                        comment.setSenderName(o.getString("senderName"));
                        comment.setCommentText(o.getString("commentText"));
                        comment.setCommentTime(o.getLong("commentTime"));
                        comment.setCommentScore((float) o.getDouble("commentScore"));
                        comments.add(comment);
                    }
                    Log.i(TAG, String.valueOf(comments.size()));
                    setRecyclerLayout();
                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void setRecyclerLayout() {
        adapter = new CommentAdapter(comments);
        layoutManager = new LinearLayoutManager(getContext());
        commentR.setLayoutManager(layoutManager);
        commentR.setAdapter(adapter);

        // 下拉刷新
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新动画开始后 回调此方法

                //设置可见
                refresh.setRefreshing(true);

                getComments(uploadItem.videoId);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //模拟加载时间，设置不可见
                        refresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void postComment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                SharedPreferences s = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                int userId = s.getInt("userId", 0);
                Map map = new HashMap();
                map.put("senderId", String.valueOf(userId));
                map.put("videoId", String.valueOf(uploadItem.videoId));
                map.put("commentText", commentInput.getText().toString().trim());
                map.put("commentScore", String.valueOf(ratingBar.getRating()));
                JSONObject jo = new JSONObject(map);
                RequestBody requestBody = RequestBody.create(MediaType.parse(
                        "application/json; charset=utf-8"
                ), jo.toString());
                Log.i(TAG, jo.toString());
                String path = HttpUtils.baseUrl1 + "/comment/add";
                Request request = new Request.Builder()
                        .url(path)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        handlePostComment(responseBody, uploadItem.videoId);
                    }
                } catch (IOException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void handlePostComment(final String response, final int videoId) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.contains("added") || response.contains("updated")) {
                    Toast.makeText(requireContext(), R.string.comment_upload_ok, Toast.LENGTH_SHORT).show();
                    getComments(videoId);
                } else {
                    Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Unexpected code " + response);
                }
            }
        });
    }
}