package com.tomclaw.minimonster;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/25/13
 * Time: 5:34 PM
 */
public class Settings {

    public static String LOG_TAG = "MiniMonster";

    private static final String SWITCHER_PREFIX = "switcher_";
    public static final int POSITION_INVALID = -1;

    private static SharedPreferences mPreferences;
    private static Context mContext;

    private static class Holder {

        static Settings instance = new Settings();
    }

    public static Settings getInstance() {
        return Holder.instance;
    }

    public void initSettings(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public String getMonsterUrl() {
        return mPreferences.getString("monster_url", mContext.getString(R.string.default_host));
    }

    public String getMonsterPassword() {
        return mPreferences.getString("monster_password", mContext.getString(R.string.default_password));
    }

    public String getSwitcherTitle(int position) {
        return mPreferences.getString(SWITCHER_PREFIX + position,
                mContext.getString(R.string.switcher_title, position + 1));
    }

    public void setSwitcherTitle(int position, String title) {
        if(TextUtils.isEmpty(title)) {
            mPreferences.edit().remove(SWITCHER_PREFIX + position).commit();
        } else {
            mPreferences.edit().putString(SWITCHER_PREFIX + position, title).commit();
        }
    }

    public int getSwitcherPosition(String switcherTitle) {
        Collection values = mPreferences.getAll().values();
        int position = 0;
        for(Object value : values) {
            if(value instanceof String && value.equals(switcherTitle)) {
                String key = (String) mPreferences.getAll().keySet().toArray()[position];
                return Integer.parseInt(key.substring(SWITCHER_PREFIX.length()));
            }
            position++;
        }
        return POSITION_INVALID;
    }
}
