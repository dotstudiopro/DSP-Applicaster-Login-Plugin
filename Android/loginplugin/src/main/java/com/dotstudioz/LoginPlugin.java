package com.dotstudioz;

import com.applicaster.hook_screen.HookScreen;
import com.applicaster.hook_screen.HookScreenListener;
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

public class LoginPlugin extends BaseLoginContract implements LoginContract, PluginScreen, HookScreen, ApplicationLoaderHookUpI {

    private static String TAG = "LoginPlugin";

    private HookListener hookListener;
    @Override
    public void executeOnApplicationReady(Context context, HookListener listener) {
        //super.executeOnApplicationReady(context, listener);
        this.hookListener = listener;
        Log.d(TAG, "executeOnApplicationReady: CALLED");
        getPluginParams();
    }

    /***
     * this function called after Plugins loaded, you can add logic that not related to the application data
     * as Zapp strings or applicaster models.
     * @param context APIntroActivity
     * @param listener listener to continue the application flow after execution finished.
     */
    @Override
    public void executeOnStartup(Context context, HookListener listener) {
        Log.d(TAG, "executeOnStartup: CALLED");
        //getPluginParams();
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        SPLTAuth0LoginUtility.getInstance().initialize(context);
        SPLTAuth0LoginUtility.getInstance().login(context);
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

    /**
     * This method performs login in the current provider.
     *
     * @param context
     * @param additionalParams Extra parameters you would like to provide to the current login provider.
     * @param callback         The callback to be invoked when the login process is done.
     */
    @Override
    public void login(Context context, Map additionalParams, Callback callback) {
        Log.d(TAG, "login: CALLED");
        //callback.onResult(true);
    }

    /**
     * This method performs login in the current provider.
     * This login method is being called before trying to play an item.
     *
     * @param context
     * @param playable         The playable we want to perform login for.
     * @param additionalParams Extra parameters you would like to provide to the current login provider.
     * @param callback         The callback to be invoked when the login process is done.
     */
    @Override
    public void login(Context context, Playable playable, Map additionalParams, Callback callback) {
        Log.d(TAG, "login: CALLED");
    }

    /**
     * Call this method in order to perform logout from the current provider.
     *
     * @param context
     * @param additionalParams Extra parameters you would like to provide to the current login provider.
     * @param callback         The callback to be invoked when the logout process is done.
     * @return
     */
    @Override
    public void logout(Context context, Map additionalParams, Callback callback) {
        Log.d(TAG, "logout: CALLED");
    }

    public boolean isItemLocked(Object model) {
        Log.d(TAG, "isItemLocked: CALLED");
        return false;
    }

    /**
     * @return true if the login provider has a valid token - in most cases it would be just checking the the token is persisted.
     * This method shouldn't consider authorization for this token and user - means you don't need to really validate the token is
     */
    @Override
    public boolean isTokenValid() {
        return false;
    }

    /**
     * @return The token held by the current login provider.
     */
    @Override
    public String getToken() {
        return null;
    }

    /**
     * This method allows external screens / JavaScript / React to set the token.
     *
     * @param token The new token.
     */
    @Override
    public void setToken(String token) {

    }

    @Override
    public void present(Context context, HashMap<String, Object> screenMap, Serializable dataSource, boolean isActivity) {
        Log.d(TAG, "present: CALLED");
        logd("present", screenMap);
    }

    @Override
    public Fragment generateFragment(HashMap<String, Object> screenMap, Serializable dataSource) {
        Log.d(TAG, "generateFragment: CALLED");
        logd("generateFragment", screenMap);
        return null;
    }

    

    @NotNull
    @Override
    public HashMap<String, String> getHook() {
        Log.d(TAG, "getHook: CALLED");
        return null;
    }

    @Override
    public void setHook(@NotNull HashMap<String, String> hashMap) {
        Log.d(TAG, "setHook: CALLED");
        logd("setHook", hashMap);
    }

    @Override
    public void executeHook(@NotNull Context context, @NotNull HookScreenListener hookScreenListener, @Nullable Map<String, ?> map) {
        Log.d(TAG, "executeHook: CALLED");
        logd("executeHook", map);
    }

    @NotNull
    @Override
    public HookScreenListener getListener() {
        Log.d(TAG, "getListener: CALLED");
        return null;
    }

    @Override
    public void hookDismissed() {
        Log.d(TAG, "hookDismissed: CALLED");
    }

    @Override
    public boolean isFlowBlocker() {
        Log.d(TAG, "isFlowBlocker: CALLLED");
        return false;
    }

    @Override
    public boolean isRecurringHook() {
        Log.d(TAG, "isRecurringHook: CALLED");
        return false;
    }

    @Override
    public boolean shouldPresent() {
        Log.d(TAG, "shouldPresent: CALLED");
        return false;
    }

    /**
     * This interface is being deprecated due to not passing all information about plugin
     * PLEASE USE GenericPluginI instead
     * initialization of the player plugin configuration with a Map params
     *
     * @param params
     */
    @Override
    public void setPluginConfigurationParams(Map params) {
        Log.d(TAG, "setPluginConfigurationParams: CALLED");
        logd("setPluginConfigurationParams", params);
        readPluginParameters(params);
    }

    @Override
    public boolean handlePluginScheme(Context context, Map<String, String> data) {
        Log.d(TAG, "handlePluginScheme: CALLED");
        logd("handlePluginScheme", data);
        return false;
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
                    }
                }
            } else {

            }
        }
    }

    private void logd(String methodName, Object obj) {
        if(obj != null) {
            if (obj instanceof List) {
                Log.d(TAG, "logd: "+methodName+" : Length==>"+((List)obj).size());
            } else if (obj instanceof Map) {
                Log.d(TAG, "logd: "+methodName+" : Length==>"+((Map)obj).size());
                Iterator<String> itrk = ((Map)obj).keySet().iterator();
                while(itrk.hasNext()) {
                    String keyString = itrk.next();
                    Log.d(TAG, "logd: "+keyString+"==>"+((Map)obj).get(keyString));
                }
            } else {
                Log.d(TAG, "logd: "+methodName+" : Length==>"+obj.toString());
            }
        } else {
            Log.d(TAG, "logd: "+methodName+" : Empty");
        }
    }
}
