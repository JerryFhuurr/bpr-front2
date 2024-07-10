package com.bpr.front2.home.user.teacher.uploadPage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bpr.front2.R;

public class UploadFragmentCourseAdapter extends BaseAdapter {
    private int[] ids;
    private String[] courseNames;
    private Context context;
    private ViewHolder viewHolder;

    public UploadFragmentCourseAdapter(int[] ids, String[] names, Context context) {
        this.ids = ids;
        this.courseNames = names;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ids.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View mView, ViewGroup viewGroup) {
        if (mView == null) {
            mView = View.inflate(context, R.layout.upload_fra_course_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.courseIdText = mView.findViewById(R.id.course_id_text);
            viewHolder.courseNameText = mView.findViewById(R.id.course_name_text);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }

        viewHolder.courseIdText.setText(String.valueOf(ids[i]));
        viewHolder.courseNameText.setText(courseNames[i]);
        return mView;
    }

    class ViewHolder {
        private TextView courseIdText;
        private TextView courseNameText;
    }
}
