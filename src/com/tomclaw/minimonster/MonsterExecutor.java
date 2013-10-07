package com.tomclaw.minimonster;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 9/25/13
 * Time: 5:23 PM
 */
public class MonsterExecutor {

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
    private final ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
            KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);

    private static class Holder {

        static MonsterExecutor instance = new MonsterExecutor();
    }

    public static MonsterExecutor getInstance() {
        return Holder.instance;
    }

    private SwitchersList parseSwitchersList(String content) {
        String SW_LINK_START = "<a href=\"./?sw=";
        String SW_LINK_CLOSE = "turn";
        String REG_EXP = "^<a href=\"./\\?sw=(\\d)-(\\d)\">$";
        int startIndex, closeIndex = 0;
        SwitchersList switchersList = new SwitchersList();
        while ((startIndex = content.indexOf(SW_LINK_START, closeIndex)) != -1) {
            closeIndex = content.indexOf(SW_LINK_CLOSE, startIndex);
            String switcher = content.substring(startIndex, closeIndex);
            if (switcher.matches(REG_EXP)) {
                int port = Integer.parseInt(switcher.replaceFirst(REG_EXP, "$1"));
                boolean value = Integer.parseInt(switcher.replaceFirst(REG_EXP, "$2")) == 0;
                Log.d(Settings.LOG_TAG, "port: " + port + " value: " + value);
                switchersList.add(new Switcher(port, value));
            } else {
                Log.d(Settings.LOG_TAG, "Couldn't parse: [" + switcher + "]");
            }
        }
        return switchersList;
    }

    public void fetchSwitchers(final ListCallback listCallback) {
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                try {
                    URL url = new URL(getMonsterUrl() + "?main=");
                    Log.d(Settings.LOG_TAG, url.getQuery());
                    URLConnection urlConnection = url.openConnection();
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    String content = new String(readFully(in), "UTF-8");
                    listCallback.onComplete(parseSwitchersList(content));
                } catch (Throwable ignored) {
                    listCallback.onError();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
    }

    public void switchPort(final int port, final boolean value, final SwitchCallback switchCallback) {
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                try {
                    URL url = new URL(getMonsterUrl() + "?sw=" + port + "-" + (value ? 1 : 0));
                    Log.d(Settings.LOG_TAG, url.getQuery());
                    URLConnection urlConnection = url.openConnection();
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    String content = new String(readFully(in), "UTF-8");
                    switchCallback.onComplete(parseSwitchersList(content));
                } catch (Throwable ignored) {
                    switchCallback.onError();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
    }

    public void getTemperature(final TemperatureCallback callback) {
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                try {
                    URL url = new URL(getMonsterUrl() + "?main=");
                    Log.d(Settings.LOG_TAG, url.getQuery());
                    URLConnection urlConnection = url.openConnection();
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    String content = new String(readFully(in), "UTF-8");
                    int temperatureIndex = content.indexOf("t = ") + 4;
                    String temperature = content.substring(temperatureIndex);
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
        });
    }

    private byte[] readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            stream.write(buffer, 0, length);
        }
        return stream.toByteArray();
    }

    private static String getMonsterUrl() {
        return Settings.getInstance().getMonsterUrl() + "/" + Settings.getInstance().getMonsterPassword() + "/";
    }

    public interface ListCallback {

        public void onComplete(SwitchersList switchersList);

        public void onError();
    }

    public interface SwitchCallback {

        public void onComplete(SwitchersList switchersList);

        public void onError();
    }

    public interface TemperatureCallback {

        public void onComplete(String temperature);

        public void onError();
    }
}
