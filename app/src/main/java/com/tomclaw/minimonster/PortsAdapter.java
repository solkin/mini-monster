package com.tomclaw.minimonster;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tomclaw.minimonster.dto.Port;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/25/13
 * Time: 7:32 PM
 */
public class PortsAdapter extends BaseAdapter {

    private final Context context;
    private LayoutInflater inflater;
    private List<Port> ports;

    public PortsAdapter(Activity context) {
        this.context = context;
        this.inflater = context.getLayoutInflater();
        ports = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return ports.size();
    }

    @Override
    public Port getItem(int position) {
        if(ports.size() > position) {
            return ports.get(position);
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
                view = inflater.inflate(R.layout.port_item, parent, false);
            } else {
                view = convertView;
            }
            bindView(view, position);
        } catch (Throwable ex) {
            view = inflater.inflate(R.layout.port_item, parent, false);
            Log.d(Settings.LOG_TAG, "exception in getView: " + ex.getMessage());
        }
        return view;
    }

    private void bindView(View view, int position) {
        Port port = getItem(position);
        if(port != null) {
            TextView switcherTitle = (TextView) view.findViewById(R.id.switcher_title);
            TextView switcherPort = (TextView) view.findViewById(R.id.switcher_port);
            SwitchCompat switcherToggle = (SwitchCompat) view.findViewById(R.id.switcher_toggle);

            switcherTitle.setText(Settings.getInstance().getSwitcherTitle(position));
            switcherPort.setText(context.getString(R.string.switcher_port, port.getIndex()));
            switcherToggle.setChecked(port.getValue());
            switcherToggle.setTag(R.string.switcher_port, port.getIndex());
        }
    }

    public void setPorts(List<Port> ports) {
        this.ports.clear();
        this.ports.addAll(ports);
    }
}
