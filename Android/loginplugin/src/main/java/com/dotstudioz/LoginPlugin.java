package com.dotstudioz;

import com.applicaster.hook_screen.HookScreenListener;
import com.applicaster.plugin_manager.login.BaseLoginContract;
import com.applicaster.plugin_manager.hook.HookListener;
import com.applicaster.plugin_manager.login.LoginContract;
import com.applicaster.plugin_manager.playersmanager.Playable;
import com.applicaster.plugin_manager.screen.PluginScreen;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LoginPlugin extends BaseLoginContract implements PluginScreen, HookScreenListener {

    private static String TAG = "LoginPlugin";

    @Override
    public void executeOnApplicationReady(Context context, HookListener listener) {
        super.executeOnApplicationReady(context, listener);
        Log.d(TAG, "executeOnApplicationReady: CALLED");
    }

    protected void logout(Context context, Map additionalParams) {
    	// TODO:
        Log.d(TAG, "logout: CALLED");
    }

    protected void login(Context context, Playable playable, Map additionalParams) {
    	// TODO:
        Log.d(TAG, "login: playable CALLED");
    }

    protected void login(Context context, Map additionalParams) {
    	// TODO:
        Log.d(TAG, "login: CALLED");
    }

    public boolean isItemLocked(Object model) {
        Log.d(TAG, "isItemLocked: CALLED");
        return false;
    }

    @Override
    public void present(Context context, HashMap<String, Object> screenMap, Serializable dataSource, boolean isActivity) {
        Log.d(TAG, "present: CALLED");
    }

    @Override
    public Fragment generateFragment(HashMap<String, Object> screenMap, Serializable dataSource) {
        Log.d(TAG, "generateFragment: CALLED");
        return null;
    }

    @Override
    public void hookCompleted(@Nullable Map<String, Object> map) {
        Log.d(TAG, "hookCompleted: CALLED");
    }

    @Override
    public void hookFailed(@Nullable Map<String, Object> map) {
        Log.d(TAG, "hookFailed: CALLED");
    }
}
