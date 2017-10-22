package com.tomclaw.minimonster;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tomclaw.minimonster.dto.Monster;
import com.tomclaw.minimonster.views.MonsterImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/25/13
 * Time: 7:32 PM
 */
public class MonstersAdapter extends BaseAdapter {

    private final Context context;
    private LayoutInflater inflater;
    private List<Monster> monsters;

    public MonstersAdapter(Activity context) {
        this.context = context;
        this.inflater = context.getLayoutInflater();
        this.monsters = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return monsters.size();
    }

    @Override
    public Monster getItem(int position) {
        if(monsters.size() > position) {
            return monsters.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        try {
            if (convertView == null) {
                view = inflater.inflate(R.layout.monster_item, parent, false);
            } else {
                view = convertView;
            }
            bindView(view, position);
        } catch (Throwable ex) {
            view = inflater.inflate(R.layout.monster_item, parent, false);
            Log.d(Settings.LOG_TAG, "exception in getView: " + ex.getMessage());
        }
        return view;
    }

    private void bindView(View view, int position) {
        Monster monster = getItem(position);
        if(monster != null) {
            MonsterImageView monsterIcon = (MonsterImageView) view.findViewById(R.id.monster_icon);
            TextView monsterName = (TextView) view.findViewById(R.id.monster_name);

            monsterIcon.setMonster(monster);
            monsterName.setText(monster.getName());
        }
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters.clear();
        this.monsters.addAll(monsters);
    }
}
