package com.bpr.front2.home.user.comment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
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
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentFragment extends Fragment {

    private EditText commentInput;
    private RatingBar ratingBar;
    private Button sendButton;
    private TextView wordNumText;

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
        wordNumText = v.findViewById(R.id.text_size_label);

        wordNumText.setText("200 words left");

        getComments(uploadItem.resId);

        commentInput.addTextChangedListener(new TextWatcher() {
            private CharSequence wordNum; // number of words
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                wordNum = charSequence; // record number of word
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int number = 200 - editable.length();
                // Left words user can input
                wordNumText.setText(number + " words left");
                selectionStart = commentInput.getSelectionStart();
                selectionEnd = commentInput.getSelectionEnd();
                if (wordNum.length() > 200) {
                    wordNumText.setText(editable.length() + "/200 words");
                    wordNumText.setTextColor(getResources().getColor(R.color.red));
                    sendButton.setText("Too much words entered !!");
                    sendButton.setClickable(false);
                }
            }
        });

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
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                String url = HttpUtils.baseUrl1 + "/comment/get/all?videoId=" + videoId;
                Request request = new Request.Builder().url(url).get().build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, "comments:" + responseBody);
                        setCommentList(responseBody);
                    }
                } catch (IOException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                    Looper.prepare();
                    Toast.makeText(getContext(), "Internet error, please check your connection"
                            , Toast.LENGTH_LONG).show();
                    Looper.loop();
                    //throw new RuntimeException(e);
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
                        comment.setResId(o.getInt("resId"));
                        comment.setRoleId(o.getInt("roleId"));
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
        adapter = new CommentAdapter(comments, requireActivity());
        layoutManager = new LinearLayoutManager(getContext());
        commentR.setLayoutManager(layoutManager);
        commentR.setAdapter(adapter);

        DividerItemDecoration mDivider = new
                DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL);
        commentR.addItemDecoration(mDivider);


        // drag and refresh
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method is called back after the refresh animation starts

                // Set Visible
                refresh.setRefreshing(true);

                getComments(uploadItem.resId);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Simulate loading time, setting invisible
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
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                SharedPreferences s = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                int userId = s.getInt("userId", 0);
                Map map = new HashMap();
                map.put("senderId", String.valueOf(userId));
                map.put("resId", String.valueOf(uploadItem.resId));
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
                        handlePostComment(responseBody, uploadItem.resId);
                    }
                } catch (IOException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                    Looper.prepare();
                    Toast.makeText(getContext(), "Internet error, please check your connection"
                            , Toast.LENGTH_LONG).show();
                    Looper.loop();
                    //throw new RuntimeException(e);
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