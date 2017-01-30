package com.minimalart.studentlife.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private static final String COLOR_KEY = "key_color_preference";
    private static final String ACCENT_KEY = "key_accent_preference";

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme = preferences.getString(COLOR_KEY, "red");
        switch(theme){
            case "red":
                setTheme(R.style.AppTheme_Red);
                break;
            case "pink":
                setTheme(R.style.AppTheme_Pink);
                break;
            case "purple":
                setTheme(R.style.AppTheme_Purple);
                break;
            case "deep_blue":
                setTheme(R.style.AppTheme_DarkBlue);
                break;
            default:
                setTheme(R.style.AppTheme_Red);
                break;
        }
        setContentView(R.layout.activity_settings);
        setupActionBar((Toolbar)findViewById(R.id.toolbar_settings_activity));
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_activity_frame, new SettingsFragment())
                .commit();

    }

    /**
     * Setting up the actionbar
     * Enables the backbutton at the top-left
     */
    private void setupActionBar(@Nullable Toolbar toolbar) {
        toolbar.setTitleTextColor(Color.parseColor("#FAFAFA"));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back, getTheme()));
        }
    }

    /**
     * Handle the click event on the top-left back button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }
}
