package com.tomclaw.minimonster;

import android.content.Context;
import com.podio.sdk.Podio;
import com.podio.sdk.Request;
import com.podio.sdk.Store;
import com.tomclaw.minimonster.dto.Model;
import com.tomclaw.minimonster.dto.Monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by solkin on 13.05.15.
 */
public class MonstersController {

    private static class Holder {

        static MonstersController instance = new MonstersController();
    }

    public static MonstersController getInstance() {
        return Holder.instance;
    }
    private Store localStore = null;
    private volatile Model model = null;
    private volatile List<GetModelCallback> callbacks = new ArrayList<>();

    public void openStorage(Context context) {
        Podio.store
                .open(context, Settings.storeName, Settings.cacheSizeInKb)
                .withResultListener(new Request.ResultListener<Store>() {
                    @Override
                    public boolean onRequestPerformed(Store store) {
                        localStore = store;
                        loadModel();
                        return false;
                    }
                })
                .withErrorListener(new Request.ErrorListener() {

                    @Override
                    public boolean onErrorOccured(Throwable cause) {
                        cause.printStackTrace();
                        return false;
                    }
                });
    }

    private void loadModel() {
        localStore.get("model", Model.class)
                .withResultListener(new Request.ResultListener<Model>() {

                    @Override
                    public boolean onRequestPerformed(final Model model) {
                        setModel(model);
                        return false;
                    }
                })
                .withErrorListener(new Request.ErrorListener() {
                    @Override
                    public boolean onErrorOccured(Throwable throwable) {
                        setModel(null);
                        return false;
                    }
                });
    }

    public void getModel(GetModelCallback callback) {
        if(model != null) {
            callback.onModel(model);
        } else {
            callbacks.add(callback);
        }
    }

    public void setModel(Model model) {
        if(model == null) {
            model = new Model();
        }
        this.model = model;
        for(GetModelCallback callback : callbacks) {
            callback.onModel(model);
        }
    }

    public void insertMonster(Monster monster, boolean active) {
        model.getMonsters().add(monster);
        if(active) {
            model.setActive(model.getMonsters().indexOf(monster));
        }
    }

    public void update() {
        localStore.set("model", model);
    }

    public interface GetModelCallback {
        public void onModel(Model model);
    }
}
