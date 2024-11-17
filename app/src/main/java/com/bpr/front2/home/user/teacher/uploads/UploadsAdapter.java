package com.bpr.front2.home.user.teacher.uploads;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bpr.front2.R;

import java.util.ArrayList;

public class UploadsAdapter extends RecyclerView.Adapter<UploadsAdapter.ViewHolder>{
    private ArrayList<UploadItem> items;
    private OnItemClickListener clickListener;

    public UploadsAdapter(ArrayList<UploadItem> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        clickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UploadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_upload_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadsAdapter.ViewHolder holder, int position) {
        holder.mView.setText(items.get(position).resTitle);

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

    public void searchItem(String text) {
        for (UploadItem ui : items) {
            if (!ui.resTitle.contains(text)) {
                items.remove(ui);
            }
        }
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
            mView = itemView.findViewById(R.id.upload_item_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
