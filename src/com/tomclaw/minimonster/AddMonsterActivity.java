package com.tomclaw.minimonster;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;
import com.tomclaw.minimonster.dto.Monster;
import com.tomclaw.minimonster.dto.PortsList;
import com.tomclaw.minimonster.tasks.PleaseWaitTask;
import com.tomclaw.minimonster.tasks.TaskExecutor;

import java.lang.ref.WeakReference;

/**
 * Adding new monster activity.
 * Created by solkin on 13.05.15.
 */
public class AddMonsterActivity extends AppCompatActivity {

    private FloatingLabelEditText nameView;
    private FloatingLabelEditText urlView;
    private FloatingLabelEditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_monster_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.add_monster);
        }

        nameView = (FloatingLabelEditText) findViewById(R.id.module_name);
        urlView = (FloatingLabelEditText) findViewById(R.id.module_url);
        passwordView = (FloatingLabelEditText) findViewById(R.id.module_password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_monster_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.complete: {
                String name = nameView.getInputWidget().getText().toString();
                String url = urlView.getInputWidget().getText().toString();
                String password = passwordView.getInputWidget().getText().toString();
                final Monster monster = new Monster(name, url, password);
                TaskExecutor.getInstance().execute(new AddMonsterTask(this, monster));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class AddMonsterTask extends PleaseWaitTask {

        private final Monster monster;
        private WeakReference<AddMonsterActivity> weakActivity;

        public AddMonsterTask(AddMonsterActivity activity, Monster monster) {
            super(activity);
            this.weakActivity = new WeakReference<>(activity);
            this.monster = monster;
        }

        @Override
        public void executeBackground() throws Throwable {
            PortsList portsList = MonsterExecutor.fetchPorts(monster);
            monster.setPortsList(portsList);
            MonstersController.getInstance().insertMonster(monster, true);
            MonstersController.getInstance().update();
        }

        @Override
        public void onSuccessMain() {
            AddMonsterActivity activity = weakActivity.get();
            if(activity != null) {
                activity.setResult(RESULT_OK);
                activity.finish();
            }
        }

        @Override
        public void onFailMain() {
            AddMonsterActivity activity = weakActivity.get();
            if(activity != null) {
                Toast.makeText(activity, R.string.unable_to_connect_to_monster, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
