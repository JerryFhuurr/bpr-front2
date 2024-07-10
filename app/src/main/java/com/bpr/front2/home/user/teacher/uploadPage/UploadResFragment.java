package com.bpr.front2.home.user.teacher.uploadPage;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bpr.front2.R;
import com.bpr.front2.handler.UriUtils;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class UploadResFragment extends Fragment {

    private EditText fileNameEdit;
    private Spinner courseList;
    private Button chooseVideoButton;
    private Button chooseFileButton;
    private Button uploadButton;
    private static int REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> resultLauncher;

    public UploadResFragment() {
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
        View v = inflater.inflate(R.layout.fragment_upload_res, container, false);
        fileNameEdit = v.findViewById(R.id.file_name_edit);
        courseList = v.findViewById(R.id.course_cat);
        uploadButton = v.findViewById(R.id.upload);
        chooseVideoButton = v.findViewById(R.id.choose_video_button);
        chooseFileButton = v.findViewById(R.id.choose_file_button);

        //TODO 等后端完成后添加上传的逻辑代码

        // open system file to choose file
        chooseVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @NeedsPermission("Manifest.permission.READ_EXTERNAL_STORAGE")
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                //chooseFile.setType("*/*");//匹配所有的类型
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                chooseFile.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                Intent intent = Intent.createChooser(chooseFile, "title");
                startActivityForResult(intent, 1);

            }
        });
        return v;
    }

    String path;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                Log.i(TAG, "uri:" + uri);
                path = UriUtils.getFileAbsolutePath(getContext(), uri);
                Log.i(TAG, "path:" + path);
                File file = new File(path);
                Log.i(TAG, String.valueOf(file.exists()));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

}
