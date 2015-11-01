package com.tomclaw.minimonster;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.SwitchCompat;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/25/13
 * Time: 7:32 PM
 */
public class SwitchersAdapter extends BaseAdapter {

    private final Context mContext;
    private LayoutInflater mInflater;
    private SwitchersList mSwitchersList;

    private PortSwitchListener portSwitchListener;

    public SwitchersAdapter(Activity context) {
        this.mContext = context;
        this.mInflater = context.getLayoutInflater();
        mSwitchersList = new SwitchersList();
    }

    @Override
    public int getCount() {
        return mSwitchersList.size();
    }

    @Override
    public Switcher getItem(int position) {
        if(mSwitchersList.size() > position) {
            return mSwitchersList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(mSwitchersList.size() > position) {
            return mSwitchersList.get(position).getSwitcherPort();
        } else {
            return -1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        try {
            if (convertView == null) {
                view = mInflater.inflate(R.layout.switcher_item, parent, false);
            } else {
                view = convertView;
            }
            bindView(view, position);
        } catch (Throwable ex) {
            view = mInflater.inflate(R.layout.switcher_item, parent, false);
            Log.d(Settings.LOG_TAG, "exception in getView: " + ex.getMessage());
        }
        return view;
    }

    private void bindView(View view, int position) {
        final Switcher switcher = getItem(position);
        if(switcher != null) {
            TextView switcherTitle = (TextView) view.findViewById(R.id.switcher_title);
            TextView switcherPort = (TextView) view.findViewById(R.id.switcher_port);
            final SwitchCompat switcherToggle = (SwitchCompat) view.findViewById(R.id.switcher_toggle);
            ImageView switcherIcon = (ImageView) view.findViewById(R.id.switcher_icon);

            switcherTitle.setText(Settings.getInstance().getSwitcherTitle(position));
            switcherPort.setText(mContext.getString(R.string.switcher_port, switcher.getSwitcherPort()));
            switcherToggle.setChecked(switcher.isSwitcherValue());
            switcherIcon.setImageResource(switcher.isSwitcherValue() ? R.drawable.ic_socket_plugged : R.drawable.ic_socket_free);
            switcherToggle.setTag(R.string.switcher_port, switcher.getSwitcherPort());
            switcherToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (portSwitchListener != null) {
                        portSwitchListener.onPortSwitched(switcher.getSwitcherPort(), switcherToggle.isChecked());
                    }
                }
            });
        }
    }

    public void setSwitchersList(SwitchersList switchersList) {
        mSwitchersList.clear();
        mSwitchersList.addAll(switchersList);
    }

    public boolean checkTitleExist(String title) {
        for(int position = 0; position < mSwitchersList.size(); position++) {
            if(Settings.getInstance().getSwitcherTitle(position).equals(title)) {
                return true;
            }
        }
        return false;
    }

    public void setPortSwitchListener(PortSwitchListener portSwitchListener) {
        this.portSwitchListener = portSwitchListener;
    }

    public static interface PortSwitchListener {
        public void onPortSwitched(int port, boolean isChecked);
    }
}
