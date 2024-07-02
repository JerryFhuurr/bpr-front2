package com.bpr.front2.home.user;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;

import java.util.Calendar;

public class AccountFragment extends Fragment {

    private EditText usernameEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText phoneEdit;
    private TextView birthdayText;
    private Button editBirthdayBtn;
    private Button saveBtn;
    private Button changePassBtn;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO 添加拉取用户信息的代码
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        usernameEdit = v.findViewById(R.id.accountName);
        emailEdit = v.findViewById(R.id.emailEdit);
        phoneEdit = v.findViewById(R.id.phoneEdit);
        birthdayText = v.findViewById(R.id.birthdayText);
        editBirthdayBtn = v.findViewById(R.id.account_change_birthday);
        saveBtn = v.findViewById(R.id.update_info);
        changePassBtn = v.findViewById(R.id.change_password);


        editBirthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPassDialog();
            }
        });
        return v;
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // 用户选择的日期
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        // 在这里保存用户选择的日期，例如将其设置给一个 TextView 或保存到数据库
                        birthdayText.setText(selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    protected void showPassDialog() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View textEntryView = factory.inflate(R.layout.change_password_dialog, null);
        final EditText passEdit = (EditText) textEntryView.findViewById(R.id.editPasswordD);
        final EditText passREdit = (EditText)textEntryView.findViewById(R.id.editPasswordRD);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(getContext());
        ad1.setTitle(R.string.login_reset_title);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String password = passEdit.getText().toString();
                String newPassword = passREdit.getText().toString();

                //TODO 添加获取当前密码的代码
                if (password.equals("123456")) {
                    // 密码一致，执行相应操作
                    if (newPassword.length() >= 8) {
                        //TODO 正常操作

                    } else {
                        Toast.makeText(getContext(), R.string.account_pass_origin_d_error_2, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 密码不一致，标红并显示错误提示
                    Toast.makeText(getContext(), R.string.account_pass_origin_d_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        ad1.show();// 显示对话框

    }
}