package com.tomclaw.minimonster.views;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.tomclaw.minimonster.R;
import com.tomclaw.minimonster.SettingsActivity;

/**
 * Created by Igor on 04.07.2014.
 */
public class MonstersDrawerLayout extends DrawerLayout {

    private Activity activity;
    // private MonstersAdapter accountsAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout drawerContent;
    private CharSequence title;
    private CharSequence drawerTitle;

    public MonstersDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(final Activity activity) {
        this.activity = activity;
        setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerContent = (LinearLayout) findViewById(R.id.left_drawer);

        final ActionBar actionBar = activity.getActionBar();
        drawerToggle = new ActionBarDrawerToggle(activity, this,
                R.drawable.ic_drawer, R.string.switchers, R.string.monsters) {

            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                actionBar.setTitle(title);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setTitle(drawerTitle);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        setDrawerListener(drawerToggle);

        // Buttons.

        Button settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                activity.startActivity(intent);
                closeAccountsPanel();
            }
        });
        /*final Button connectionButton = (Button) findViewById(R.id.connection_button);
        final View.OnClickListener connectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activity.getServiceInteraction().connectAccounts();
                } catch (Throwable ignored) {
                    // Heh... Nothing to do in this case.
                    Toast.makeText(activity, R.string.unable_to_connect_account,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        final View.OnClickListener disconnectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activity.getServiceInteraction().disconnectAccounts();
                } catch (RemoteException e) {
                    // Heh... Nothing to do in this case.
                    Toast.makeText(activity, R.string.unable_to_shutdown_account,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        // Accounts list.
        ListView accountsList = (ListView) findViewById(R.id.accounts_list_view);
        // Creating adapter for accounts list.
        accountsAdapter = new AccountsAdapter(activity, activity.getLoaderManager());
        accountsAdapter.setOnAvatarClickListener(new AccountsAdapter.OnAvatarClickListener() {

            @Override
            public void onAvatarClicked(int accountDbId, boolean isConnected) {
                if (isConnected) {
                    // Account is online and we can show it's brief info.
                    final AccountInfoTask accountInfoTask =
                            new AccountInfoTask(activity, accountDbId);
                    TaskExecutor.getInstance().execute(accountInfoTask);
                    closeAccountsPanel();
                }
            }
        });
        // Bind to our new adapter.
        accountsList.setAdapter(accountsAdapter);
        accountsList.setMultiChoiceModeListener(new AccountsMultiChoiceModeListener());
        accountsList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDrawerTitle(String drawerTitle) {
        this.drawerTitle = drawerTitle;
    }

    public void syncToggleState() {
        drawerToggle.syncState();
    }

    public void onToggleConfigurationChanged(Configuration newConfig) {
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onToggleOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item);
    }

    private void closeAccountsPanel() {
        closeDrawers();
    }
}
