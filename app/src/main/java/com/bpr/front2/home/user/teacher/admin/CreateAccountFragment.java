package com.bpr.front2.home.user.teacher.admin;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;
import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.course.CourseItem;
import com.bpr.front2.home.user.teacher.admin.account.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Native;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateAccountFragment extends Fragment {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private TextView errorLabel;
    private Button createButton;
    private Button selectCourse, selectRole;
    private ArrayList<CourseItem> courseItems;
    private String[] selected;
    private int[] selectedCourseIds;
    private String role = "";

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseItems = new ArrayList<>(7);
        getCourses();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_account, container, false);
        usernameEdit = v.findViewById(R.id.register_username);
        passwordEdit = v.findViewById(R.id.register_password);
        errorLabel = v.findViewById(R.id.error_label);
        createButton = v.findViewById(R.id.register_ok);
        selectCourse = v.findViewById(R.id.open_course_list);
        selectRole = v.findViewById(R.id.open_role_list);


        selectCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(requireContext());
                selectDialog.setTitle(R.string.register_course_button);

                for (int i = 0; i < courseItems.size(); i++) {
                    selected[i] = courseItems.get(i).getCourseName();
                }
                final boolean[] options = new boolean[courseItems.size()];
                for (int i = 0; i < options.length; i++) {
                    options[i] = false;
                }

                ArrayList<String> selected2 = new ArrayList<>();

                selectDialog.setMultiChoiceItems(selected, options, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        Log.i(TAG, "select:" + selected[i] + b);
                        selected2.add(selected[i]);
                    }
                });

                selectDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<Integer> selected = new ArrayList<>();
                        for (CourseItem c : courseItems) {
                            for (String s : selected2) {
                                if (c.getCourseName().equals(s)) {
                                    selected.add(c.getId());
                                }
                            }
                        }
                        selectedCourseIds = new int[selected.size()];
                        for (int j = 0; j < selected.size(); j++) {
                            selectedCourseIds[j] = selected.get(j);
                            Log.i(TAG, "select2:" + selectedCourseIds[j]);
                        }
                    }
                });

                selectDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                selectDialog.create().show();
            }

        });


        selectRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder selectDialog = new AlertDialog.Builder(requireContext());
                selectDialog.setTitle(R.string.register_role_button);
                final String[] roles = {"admin", "teacher", "student"};

                ArrayList<String> selectRole = new ArrayList<>(1);
                selectDialog.setSingleChoiceItems(roles, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "select:" + roles[i]);
                        selectRole.add(roles[i]);
                    }
                });

                selectDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        role = selectRole.get(0);
                    }
                });

                selectDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                selectDialog.create().show();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameGet = usernameEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                if (usernameGet.isEmpty()) {
                    errorLabel.setText(R.string.register_error);
                } else {
                    if (password.length() < 6) {
                        errorLabel.setText(R.string.register_password_hint);
                    } else {
                        int count = 0;
                        if (selectedCourseIds == null) {
                            errorLabel.setText(R.string.register_course_error);
                        } else {
                            for (int i : selectedCourseIds) {
                                if (i == 0) {
                                    count++;
                                }
                            }
                            if (count != 0) {
                                errorLabel.setText(R.string.register_course_error);
                            } else {
                                if (role.equals("")) {
                                    errorLabel.setText(R.string.register_role_error);
                                } else {
                                    // 1. 遍历全部成员，将当前项目与左边项逐个进
                                    int newArr[] = new int[selectedCourseIds.length];
                                    int x = 0;
                                    for (int i = 0; i < selectedCourseIds.length; i++) {
                                        for (int j = 0; j <= i; j++) {
                                            if (selectedCourseIds[i] == selectedCourseIds[j]) {
                                                if (i == j) {
                                                    newArr[x] = selectedCourseIds[i];
                                                    x++;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    int result[] = Arrays.copyOf(newArr, x);
                                    createButton.setText("Updating, please wait");
                                    addUser(result, usernameGet, password, role);
                                }

                            }
                        }

                    }
                }
            }
        });
        return v;
    }


    private void getCourses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = HttpUtils.baseUrl1 + "/course/get/all";
                Request request = new Request.Builder().url(url).get().build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        setCourseList(responseBody);
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "No Internet connect!", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void setCourseList(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    courseItems.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        CourseItem courseItem = new CourseItem();
                        courseItem.setId(o.getInt("courseId"));
                        courseItem.setCourseName(o.getString("courseName"));
                        courseItems.add(courseItem);
                    }
                    Log.i(TAG, String.valueOf(courseItems.size()));
                    selected = new String[courseItems.size()];
                } catch (JSONException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void addUser(int[] selectedIds, String username, String password, String role) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Map map = new HashMap();
                map.put("username", username);
                map.put("password", password);
                map.put("role", role);
                map.put("courses", selectedIds);
                JSONObject jo = new JSONObject(map);
                RequestBody requestBody = RequestBody.create(MediaType.parse(
                        "application/json; charset=utf-8"
                ), jo.toString());
                Log.i(TAG, "request:" + jo.toString());
                String path = HttpUtils.baseUrl1 + "/user/add";
                Request request = new Request.Builder()
                        .url(path)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i(TAG, responseBody);
                        saveUser(responseBody);
                    }
                } catch (IOException e) {
                    Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void saveUser(final String response) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.contains("successfully")) {
                    Toast.makeText(requireContext(), R.string.account_setOK, Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(CreateAccountFragment.this)
                            .navigate(R.id.action_createAccountFragment_to_manageAccountFragment);
                } else {
                    errorLabel.setText(response);
                    Log.d(TAG, "Unexpected code " + response);
                }
            }
        });

    }
}