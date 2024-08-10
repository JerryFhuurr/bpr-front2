package com.bpr.front2.home.user.comment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bpr.front2.MainActivity;
import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private ArrayList<Comment> comments;
    private SharedPreferences s;
    private View inflater;
    FragmentActivity activity;

    public CommentAdapter(ArrayList<Comment> comments, FragmentActivity activity) {
        this.comments = comments;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        s = parent.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        MyViewHolder myViewHolder = new MyViewHolder(inflater);
        myViewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeComment(comments.get(myViewHolder.getAdapterPosition()));
            }
        });

        myViewHolder.senderName.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View view) {
                getUser(comments.get(myViewHolder.getAdapterPosition()).getSenderName(), parent.getContext());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.senderName.setText(comments.get(position).getSenderName());
        holder.scoreLabel.setText(String.valueOf(comments.get(position).getCommentScore()));

        long time = comments.get(position).getCommentTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(time);
        System.out.println(time);
        System.out.println(format.format(date));

        holder.commentTime.setText(format.format(date));
        holder.commentText.setText(comments.get(position).getCommentText());

        int userId = s.getInt("userId", 0);
        Log.i(TAG, "user id:" + userId);
        if (userId != comments.get(position).getSenderId()) {
            holder.removeButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView senderName, scoreLabel, commentText, commentTime;
        private Button removeButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_username);
            scoreLabel = itemView.findViewById(R.id.comment_score);
            commentText = itemView.findViewById(R.id.comment_text);
            removeButton = itemView.findViewById(R.id.comment_remove_button);
            commentTime = itemView.findViewById(R.id.comment_time);
        }
    }


    private void removeComment(Comment comment) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/comment/remove?commentId=" + comment.getCommentId();
                Request request = new Request.Builder().url(url).delete().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(ContentValues.TAG, responseBody);
                        if (responseBody.equals("Comment removed")) {
                            Log.i(TAG, "comment remove");
                            comments.remove(comment);

                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void getUser(String username, Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();

                String url = HttpUtils.baseUrl1 + "/user/getinfo?username=" + username;
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(ContentValues.TAG, responseBody);
                        setUserInfo(responseBody);
                    }
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(context, "No Internet connect!", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    //throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setUserInfo(final String response) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject(response);
                    int id = object.getInt("userId");
                    String username = object.getString("username");
                    String email = object.getString("email");
                    String phone = object.getString("phone");
                    String birth = object.getString("birth");


                    final AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View view1 = LayoutInflater.from(inflater.getContext()).inflate(R.layout.dialog_profile, null);
                    builder.setView(view1);

                    final AlertDialog dialog = builder.show();
                    final EditText usernameText = view1.findViewById(R.id.profileAccountName);
                    final EditText userEmail = view1.findViewById(R.id.profileEmailEdit);
                    final EditText userPhone = view1.findViewById(R.id.profilePhoneEdit);
                    final TextView userBirth = view1.findViewById(R.id.profileBirthdayText);
                    final TextView error = view1.findViewById(R.id.profileErrorLabel);

                    usernameText.setText(username);
                    if (email.equals("null")) {
                        userEmail.setText(R.string.account_profile_empty_value);
                    } else {
                        userEmail.setText(email);
                    }

                    if (phone.equals("null")) {
                        userPhone.setText(R.string.account_profile_empty_value);
                    } else {
                        userPhone.setText(phone);
                    }

                    if (birth.equals("null")) {
                        userBirth.setText(R.string.account_profile_empty_value);
                    } else {
                        userBirth.setText(birth);
                    }


                    view1.findViewById(R.id.profileOKButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } catch (JSONException e) {
                    Log.w(ContentValues.TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

}
