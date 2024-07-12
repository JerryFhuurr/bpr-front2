package com.bpr.front2.home.user.comment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bpr.front2.R;
import com.bpr.front2.home.user.course.CourseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<Comment> comments;
    private SharedPreferences s;

    public CommentAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.comment_item, parent, false);
        s = parent.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView senderName, scoreLabel, commentText, commentTime;
        private Button removeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_username);
            scoreLabel = itemView.findViewById(R.id.comment_score);
            commentText = itemView.findViewById(R.id.comment_text);
            removeButton = itemView.findViewById(R.id.comment_remove_button);
            commentTime = itemView.findViewById(R.id.comment_time);
        }
    }


}
