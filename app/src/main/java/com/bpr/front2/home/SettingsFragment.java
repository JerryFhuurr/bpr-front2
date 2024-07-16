package com.bpr.front2.home;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.bpr.front2.R;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Preference clearVideo, clearCache, about;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        initView();
    }

    private void initView() {
        about = findPreference("about");
        if (about != null) {
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    NavHostFragment.findNavController(SettingsFragment.this)
                            .navigate(R.id.action_settingsFragment_to_aboutFragment);
                    return false;
                }
            });
        }

        clearVideo = findPreference("clear_cache_video");
        if (clearVideo != null) {
            clearVideo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    deleteCacheMovie();
                    Toast.makeText(getContext(), R.string.cache_video_clear_text, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

    }

    private void deleteCacheMovie() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/tempVideo/";

        File file = new File(path);
        File[] files = file.listFiles();        // get files in this dir
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {         // delete if find a file
                File photoFile = new File(files[i].getPath());
                Log.d("Path -->> ", photoFile.getPath());
                photoFile.delete();

            }

        }
    }

}