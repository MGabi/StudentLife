package com.minimalart.studentlife.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;

/**
 * Created by ytgab on 29.01.2017.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String COLOR_KEY = "key_color_preference";
    private static final String ACCENT_KEY = "key_accent_preference";

    private Preference mainColorPreference;
    private Preference accentColorPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mainColorPreference = findPreference(COLOR_KEY);

        mainColorPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch(preference.getKey()){
            case COLOR_KEY:
                /*switch(newValue.toString()){
                    case "red":
                        getActivity().getApplication().setTheme(R.style.AppTheme_Red);
                        break;
                    case "pink":
                        getActivity().getApplication().setTheme(R.style.AppTheme_Pink);
                        break;
                    case "purple":
                        getActivity().getApplication().setTheme(R.style.AppTheme_Purple);
                        break;
                    case "deep_purple":
                        getActivity().getApplication().setTheme(R.style.AppTheme_DarkBlue);
                        break;
                    default:
                        getActivity().getApplication().setTheme(R.style.AppTheme_Red);
                        break;
                }
                break;*/
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
        }

        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v("PREFCHANGER", String.format("onSharedPreferenceChanged(key: %s)", key));
    }
}
