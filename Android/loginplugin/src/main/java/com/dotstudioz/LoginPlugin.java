package com.dotstudioz;

import com.applicaster.hook_screen.HookScreen;
import com.applicaster.hook_screen.HookScreenListener;
import com.applicaster.plugin_manager.GenericPluginI;
import com.applicaster.plugin_manager.Plugin;
import com.applicaster.plugin_manager.hook.ApplicationLoaderHookUpI;
import com.applicaster.plugin_manager.login.BaseLoginContract;
import com.applicaster.plugin_manager.hook.HookListener;
import com.applicaster.plugin_manager.login.LoginContract;
import com.applicaster.plugin_manager.playersmanager.Playable;
import com.applicaster.plugin_manager.screen.PluginScreen;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoginPlugin extends BaseLoginContract implements GenericPluginI, PluginScreen {

    private static String TAG = "LoginPlugin";

    @Override
    public void executeOnApplicationReady(Context context, HookListener listener) {
        super.executeOnApplicationReady(context, listener);
        Log.d(TAG, "executeOnApplicationReady: CALLED");
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        if(SPLTLoginPluginConstants.show_on_startup) {
            SPLTAuth0LoginUtility.getInstance().initialize(context);
            SPLTAuth0LoginUtility.getInstance().login(context);
        }
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

    /**
     * initialization of the player plugin configuration with a Plugin,
     * which contains configuration
     *
     * @param plugin
     */
    @Override
    public void setPluginModel(Plugin plugin) {
        if(plugin != null && plugin.configuration != null)
            readPluginParameters(plugin.configuration);
    }
    private void readPluginParameters(Object obj) {
        if(obj != null) {
            if (obj instanceof List) {

            } else if (obj instanceof Map) {
                Iterator<String> itrk = ((Map)obj).keySet().iterator();
                while(itrk.hasNext()) {
                    String keyString = itrk.next();
                    Log.d(TAG, "logd: "+keyString+"==>"+((Map)obj).get(keyString));

                    if(keyString.equals(SPLTLoginPluginConstants.API_KEY_STRING)) {
                        SPLTLoginPluginConstants.getInstance().apiKey = ((Map)obj).get(keyString).toString();
                    } else if(keyString.equals(SPLTLoginPluginConstants.AUTH0_CLIENT_ID_STRING)) {
                        SPLTLoginPluginConstants.getInstance().auth0ClientId = ((Map)obj).get(keyString).toString();
                    } else if(keyString.equals(SPLTLoginPluginConstants.BACKGROUND_COLOR_KEY)) {
                        SPLTLoginPluginConstants.getInstance().backgroundColor = ((Map)obj).get(keyString).toString();
                    } else if(keyString.equals(SPLTLoginPluginConstants.HEADER_COLOR_KEY)) {
                        SPLTLoginPluginConstants.getInstance().headerColor = ((Map)obj).get(keyString).toString();
                    } else if(keyString.equals(SPLTLoginPluginConstants.TITLE_KEY)) {
                        SPLTLoginPluginConstants.getInstance().title = ((Map)obj).get(keyString).toString();
                    } else if(keyString.equals(SPLTLoginPluginConstants.TITLE_COLOR_KEY)) {
                        SPLTLoginPluginConstants.getInstance().titleColor = ((Map)obj).get(keyString).toString();
                    } else if(keyString.equals(SPLTLoginPluginConstants.LOGO_KEY)) {
                        SPLTLoginPluginConstants.getInstance().logo = ((Map)obj).get(keyString).toString();
                    } else if(keyString.equals(SPLTLoginPluginConstants.SHOW_ON_STARTUP_KEY)) {
                        SPLTLoginPluginConstants.getInstance().show_on_startup = false;
                        if(((Map)obj).get(keyString).toString().equalsIgnoreCase("true"))
                            SPLTLoginPluginConstants.getInstance().show_on_startup = true;
                    }
                }
            } else {

            }
        }
    }

    @Override
    public void present(Context context, HashMap<String, Object> screenMap, Serializable dataSource, boolean isActivity) {
        Log.d(TAG, "present: CALLED");
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        SPLTAuth0LoginUtility.getInstance().initialize(context);
        SPLTAuth0LoginUtility.getInstance().login(context);
    }

    @Override
    public Fragment generateFragment(HashMap<String, Object> screenMap, Serializable dataSource) {
        return null;
    }
}