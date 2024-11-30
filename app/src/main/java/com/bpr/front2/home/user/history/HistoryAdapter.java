package com.bpr.front2.home.user.history;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private ArrayList<History> histories = new ArrayList<>();

    public HistoryAdapter(ArrayList<History> items) {
        this.histories = items;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.history_item, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText("res id: " + String.valueOf(histories.get(position).getResId()));
        //holder.upName.setText(histories.get(position).getUpName());

        long time = histories.get(position).getWatchTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(time);
        System.out.println(format.format(date));

        holder.watchTime.setText(format.format(date));
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeHistory(histories.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    private void removeHistory(History history) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/history/delete?id=" + history.getHId();
                Request request = new Request.Builder().url(url).delete().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(ContentValues.TAG, responseBody);
                        if (responseBody.equals("History removed")) {
                            Log.i(TAG, "history remove");
                            histories.remove(history);

                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, upName, watchTime;
        public Button remove;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.history_title);
            upName = itemView.findViewById(R.id.history_up_name);
            watchTime = itemView.findViewById(R.id.history_time);
            remove = itemView.findViewById(R.id.history_remove_button);
        }
    }
}
