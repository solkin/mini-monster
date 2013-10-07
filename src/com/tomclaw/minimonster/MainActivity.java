package com.tomclaw.minimonster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    private SwitchersAdapter mSwitchersAdapter;
    private ActionMode mActionMode;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Settings.getInstance().initSettings(this);

        mSwitchersAdapter = new SwitchersAdapter(this);

        final ListView switchersListView = (ListView) findViewById(R.id.switchers_list_view);
        switchersListView.setAdapter(mSwitchersAdapter);
        switchersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                if(mActionMode != null) {
                    return false;
                }
                view.setActivated(true);
                startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mActionMode = mode;
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        getMenuInflater().inflate(R.menu.action_menu, menu);
                        mode.setTitle(Settings.getInstance().getSwitcherTitle(position));
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.content_edit: {
                                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                                alert.setTitle(R.string.edit_switcher_title);
                                final View editView = getLayoutInflater().inflate(R.layout.switcher_edit_view, null);
                                final EditText editText = (EditText) editView.findViewById(R.id.edit_switcher_text);
                                editText.setText(Settings.getInstance().getSwitcherTitle(position));
                                alert.setView(editView);
                                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String title = editText.getText().toString();
                                        // Checking for such titles.
                                        if(!mSwitchersAdapter.checkTitleExist(title)) {
                                            Settings.getInstance().setSwitcherTitle(position, title);
                                            mSwitchersAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(MainActivity.this, R.string.title_already_exist,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                alert.setNegativeButton(R.string.cancel, null);
                                alert.show();
                                break;
                            }
                            case R.id.rating_important: {
                                // Save default title...
                                String title = Settings.getInstance().getSwitcherTitle(position);
                                Settings.getInstance().setSwitcherTitle(position, title);
                                // ... and create shortcut.
                                createShortcut(Settings.getInstance().getSwitcherTitle(position));
                                break;
                            }
                        }
                        mode.finish();
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        view.setActivated(false);
                        mActionMode = null;
                    }
                });
                return true;
            }
        });

        refreshSwitchersList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                refreshSwitchersList();
                return true;
            }
            case R.id.action_info: {
                final ProgressDialog dialog = ProgressDialog.show(this,
                        getString(R.string.loading), getString(R.string.please_wait), true);
                MonsterExecutor.TemperatureCallback temperatureCallback = new MonsterExecutor.TemperatureCallback() {
                    @Override
                    public void onComplete(final String temperature) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(R.string.temperature_title);
                                builder.setMessage(getString(R.string.temerature, temperature));
                                builder.setPositiveButton(R.string.close, null);
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Toast.makeText(MainActivity.this, R.string.temperature_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };
                MonsterExecutor.getInstance().getTemperature(temperatureCallback);
                return true;
            }
            case R.id.action_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onToggleClicked(View view) {
        ToggleButton switcherToggle = (ToggleButton) view;
        Integer port = (Integer) view.getTag(R.string.switcher_port);
        final ProgressDialog dialog = ProgressDialog.show(this,
                getString(R.string.loading), getString(R.string.please_wait), true);
        MonsterExecutor.SwitchCallback callback = new MonsterExecutor.SwitchCallback() {
            @Override
            public void onComplete(final SwitchersList switchersList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwitchersAdapter.setSwitchersList(switchersList);
                        mSwitchersAdapter.notifyDataSetChanged();
                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        // Returning changes back.
                        mSwitchersAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, R.string.switch_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        MonsterExecutor.getInstance().switchPort(port, switcherToggle.isChecked(), callback);
    }

    private void refreshSwitchersList() {
        final ProgressDialog dialog = ProgressDialog.show(this,
                getString(R.string.loading), getString(R.string.please_wait), true);
        dialog.setCancelable(true);
        MonsterExecutor.ListCallback listCallback = new MonsterExecutor.ListCallback() {
            @Override
            public void onComplete(final SwitchersList switchersList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwitchersAdapter.setSwitchersList(switchersList);
                        mSwitchersAdapter.notifyDataSetChanged();
                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        SwitchersList switchersList = new SwitchersList();
                        for(int c=0;c<5;c++){
                            switchersList.add(new Switcher(c, false));
                        }
                        mSwitchersAdapter.setSwitchersList(switchersList);
                        mSwitchersAdapter.notifyDataSetChanged();

                        Toast.makeText(MainActivity.this, R.string.list_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        MonsterExecutor.getInstance().fetchSwitchers(listCallback);
    }

    private void createShortcut(String shortcutName) {
        Intent shortcutIntent = new Intent(getApplicationContext(),
                SwitcherActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.putExtra(String.valueOf(R.id.switcher_title), shortcutName);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.drawable.ic_launcher));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }
}
