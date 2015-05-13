package com.tomclaw.minimonster;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/26/13
 * Time: 7:55 PM
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String INIT_SETTINGS = "init_settings";
    private boolean isInitSettings;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInitSettings = getIntent().getBooleanExtra(INIT_SETTINGS, false);

        ActionBar bar = getActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
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
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
