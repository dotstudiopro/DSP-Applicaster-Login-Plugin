package com.dotstudioz;

import com.applicaster.plugin_manager.login.BaseLoginContract;
import com.applicaster.plugin_manager.hook.HookListener;
import com.applicaster.plugin_manager.playersmanager.Playable;

import android.content.Context;

import java.util.Map;

public class LoginPlugin extends BaseLoginContract {

    @Override
    public void executeOnApplicationReady(Context context, HookListener listener) {
        super.executeOnApplicationReady(context, listener);
    }

    protected void logout(Context context, Map additionalParams) {
    	// TODO:
    }

    protected void login(Context context, Playable playable, Map additionalParams) {
    	// TODO:
    }

    protected void login(Context context, Map additionalParams) {
    	// TODO:
    }

    public boolean isItemLocked(Object model) {
    	return false;
    }
}
