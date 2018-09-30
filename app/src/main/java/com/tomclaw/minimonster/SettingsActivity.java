package com.tomclaw.minimonster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/26/13
 * Time: 7:55 PM
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String INIT_SETTINGS = "init_settings";
    private boolean isInitSettings;

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        isInitSettings = getIntent().getBooleanExtra(INIT_SETTINGS, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        settingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, settingsFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(isInitSettings && Settings.getInstance().isSettingsInitialized()) {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            case R.id.action_help: {
                openPromoPage();
                break;
            }
        }
        return true;
    }

    private void openPromoPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Settings.PROMO_URL));
        startActivity(browserIntent);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource.
            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}
