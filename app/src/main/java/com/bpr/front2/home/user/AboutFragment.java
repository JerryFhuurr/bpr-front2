package com.bpr.front2.home.user;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bpr.front2.R;

public class AboutFragment extends Fragment {

    private TextView version;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        version = v.findViewById(R.id.version);

        PackageManager manager = getContext().getPackageManager();
        String versionText = "Version ";
        try {
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            versionText += String.valueOf(info.versionName);
            version.setText(versionText);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("version error", e.getMessage());
        }
        return v;
    }
}