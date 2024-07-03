package com.bpr.front2.home.user.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bpr.front2.R;
import com.bpr.front2.home.user.teacher.uploads.UploadItem;
import com.bpr.front2.home.user.teacher.uploads.UploadsAdapter;

import java.util.ArrayList;
import java.util.Random;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{
    private ArrayList<CourseItem> items;
    private UploadsAdapter.OnItemClickListener clickListener;

    public CourseAdapter(ArrayList<CourseItem> items) {
        this.items = items;
    }

    public void setOnItemClickListener(UploadsAdapter.OnItemClickListener onItemClickListener) {
        clickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_course_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
        holder.mView.setText(items.get(position).courseName);


        if (clickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    clickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    public void addItem(ArrayList<CourseItem> list){
        list.addAll(items);
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.course_item_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
