package com.bpr.front2.home.user.teacher.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bpr.front2.R;

public class CreateAccountFragment extends Fragment {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private TextView errorLabel;
    private Button createButton;

    public CreateAccountFragment() {
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
        View v = inflater.inflate(R.layout.fragment_create_account, container, false);
        usernameEdit = v.findViewById(R.id.register_username);
        passwordEdit = v.findViewById(R.id.register_password);
        errorLabel = v.findViewById(R.id.error_label);
        createButton = v.findViewById(R.id.register_ok);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameGet = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (usernameGet.isEmpty()) {
                    errorLabel.setText(R.string.register_error);
                } else {
                    if (password.length() < 6) {
                        errorLabel.setText(R.string.register_password_hint);
                    } else {
                        //TODO 添加向后端发送请求的代码
                        Toast.makeText(getContext(), R.string.register_info, Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(CreateAccountFragment.this)
                                .navigate(R.id.action_createAccountFragment_to_manageAccountFragment);
                    }
                }
            }
        });
        return v;
    }
}