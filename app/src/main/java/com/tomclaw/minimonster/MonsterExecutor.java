package com.tomclaw.minimonster;

import android.util.Log;
import com.tomclaw.minimonster.dto.Monster;
import com.tomclaw.minimonster.dto.Port;
import com.tomclaw.minimonster.dto.PortsList;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/25/13
 * Time: 5:23 PM
 */
public class MonsterExecutor {

    public static PortsList fetchPorts(final Monster monster) throws Throwable {
        InputStream in = null;
        try {
            URL url = new URL(monster.getAccessibleUrl() + "?main=");
            Log.d(Settings.LOG_TAG, url.getQuery());
            URLConnection urlConnection = url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            String content = new String(readFully(in), "UTF-8");
            return parsePortsList(content);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void switchPort(final Monster monster, final Port port, final PortCallback portCallback) {
        InputStream in = null;
        try {
            URL url = new URL(monster.getAccessibleUrl() + "?sw=" + port.getIndex() + "-" + (port.getValue() ? 1 : 0));
            Log.d(Settings.LOG_TAG, url.getQuery());
            URLConnection urlConnection = url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            String content = new String(readFully(in), "UTF-8");
            portCallback.onComplete(parsePortsList(content));
        } catch (Throwable ignored) {
            portCallback.onError();
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void getTemperature(final Monster monster, final TemperatureCallback callback) {
        InputStream in = null;
        try {
            URL url = new URL(monster.getAccessibleUrl() + "?temp=");
            Log.d(Settings.LOG_TAG, url.getQuery());
            URLConnection urlConnection = url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            String content = new String(readFully(in), "UTF-8");
            int temperatureIndex = content.indexOf(",", 130) + 1;
            int closeIndex = content.indexOf(",", temperatureIndex + 1);
            String temperature = content.substring(temperatureIndex, closeIndex);
            callback.onComplete(temperature);
        } catch (Throwable ignored) {
            callback.onError();
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static PortsList parsePortsList(String content) {
        String SW_LINK_START = "<a href=\"./?sw=";
        String SW_LINK_CLOSE = "turn";
        String REG_EXP = "^<a href=\"./\\?sw=(\\d)-(\\d)\">$";
        int startIndex, closeIndex = 0;
        PortsList portsList = new PortsList();
        while ((startIndex = content.indexOf(SW_LINK_START, closeIndex)) != -1) {
            closeIndex = content.indexOf(SW_LINK_CLOSE, startIndex);
            String portInfoString = content.substring(startIndex, closeIndex);
            if (portInfoString.matches(REG_EXP)) {
                int port = Integer.parseInt(portInfoString.replaceFirst(REG_EXP, "$1"));
                boolean value = Integer.parseInt(portInfoString.replaceFirst(REG_EXP, "$2")) == 0;
                Log.d(Settings.LOG_TAG, "port: " + port + " value: " + value);
                portsList.addPort(new Port(port, value));
            } else {
                Log.d(Settings.LOG_TAG, "Couldn't parse: [" + portInfoString + "]");
            }
        }
        return portsList;
    }

    private static byte[] readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            stream.write(buffer, 0, length);
        }
        return stream.toByteArray();
    }

    public interface PortsListCallback {

        public void onComplete(PortsList portsList);

        public void onError();
    }

    public interface PortCallback {

        public void onComplete(PortsList portsList);

        public void onError();
    }

    public interface TemperatureCallback {

        public void onComplete(String temperature);

        public void onError();
    }
}
