package com.example.minimonster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/27/13
 * Time: 12:52 PM
 */
public class SwitcherActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Settings.getInstance().initSettings(this);

        final String switcherTitle = getIntent().getStringExtra(String.valueOf(com.example.minimonster.R.id.switcher_title));
        final int position = Settings.getInstance().getSwitcherPosition(switcherTitle);

        if(position == Settings.POSITION_INVALID) {
            showError();
        } else {
            final ProgressDialog dialog = ProgressDialog.show(this,
                    getString(com.example.minimonster.R.string.loading),
                    getString(com.example.minimonster.R.string.please_wait), true);
            MonsterExecutor.ListCallback listCallback = new MonsterExecutor.ListCallback() {
                @Override
                public void onComplete(final SwitchersList switchersList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if(switchersList.size() > position) {
                                showSwitcher(switchersList.get(position), switcherTitle);
                            } else {
                                showError();
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
                            Toast.makeText(getApplicationContext(), com.example.minimonster.R.string.list_failed,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            };
            MonsterExecutor.getInstance().fetchSwitchers(listCallback);
        }
    }

    private void showError() {
        finish();
        Toast.makeText(getApplicationContext(), com.example.minimonster.R.string.no_more_switcher,
                Toast.LENGTH_SHORT).show();
    }

    private void showSwitcher(final Switcher switcher, String switcherTitle) {
        int toggleState = switcher.isSwitcherValue() ? com.example.minimonster.R.string.turned_on
                : com.example.minimonster.R.string.turned_off;
        int toggleAction = switcher.isSwitcherValue() ? com.example.minimonster.R.string.turn_off
                : com.example.minimonster.R.string.turn_on;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(com.example.minimonster.R.string.switch_state);
        builder.setMessage(getString(com.example.minimonster.R.string.switcher_in_state, switcherTitle,
                getString(toggleState)));
        builder.setCancelable(true);
        builder.setPositiveButton(toggleAction, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toggleSwitcher(switcher.getSwitcherPort(), !switcher.isSwitcherValue());
            }
        });
        builder.setNeutralButton(com.example.minimonster.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    private void toggleSwitcher(int port, boolean value) {
        final ProgressDialog dialog = ProgressDialog.show(this,
                getString(com.example.minimonster.R.string.loading),
                getString(com.example.minimonster.R.string.please_wait), true);
        MonsterExecutor.SwitchCallback callback = new MonsterExecutor.SwitchCallback() {
            @Override
            public void onComplete(final SwitchersList switchersList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        finish();
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
                        finish();
                        Toast.makeText(SwitcherActivity.this, com.example.minimonster.R.string.switch_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        MonsterExecutor.getInstance().switchPort(port, value, callback);
    }
}
