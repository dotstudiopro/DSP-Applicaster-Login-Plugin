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

public class LoginPlugin /*extends BaseLoginContract*/ implements LoginContract, GenericPluginI, PluginScreen, HookScreen {

    private static String TAG = "LoginPlugin";
    public Context mContext;

    @Override
    public void executeOnApplicationReady(Context context, HookListener listener) {
        if(context != null) {
            mContext = context;
        }
        /*super.executeOnApplicationReady(context, listener);
        Log.d(TAG, "executeOnApplicationReady: CALLED");
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        if(SPLTLoginPluginConstants.show_on_startup) {
            SPLTAuth0LoginUtility.getInstance().initialize(context);
            SPLTAuth0LoginUtility.getInstance().login(context);
        }*/
    }

    /***
     * this function called after Plugins loaded, you can add logic that not related to the application data
     * as Zapp strings or applicaster models.
     * @param context APIntroActivity
     * @param listener listener to continue the application flow after execution finished.
     */
    @Override
    public void executeOnStartup(Context context, HookListener listener) {
        if(context != null) {
            mContext = context;
        }
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
        Log.d(TAG, "login: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "login: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        SPLTAuth0LoginUtility.getInstance().initialize(context);
        SPLTAuth0LoginUtility.getInstance().login(context, new SPLTAuth0LoginUtility.ILoginPlugin() {
            @Override
            public void loginResponse(boolean result, String token) {
                callback.onResult(result);
            }
        });
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
        Log.d(TAG, "login: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "login: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        SPLTAuth0LoginUtility.getInstance().initialize(context);
        SPLTAuth0LoginUtility.getInstance().login(context, new SPLTAuth0LoginUtility.ILoginPlugin() {
            @Override
            public void loginResponse(boolean result, String token) {
                callback.onResult(result);
            }
        });
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
        Log.d(TAG, "logout: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "logout: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        SPLTAuth0LoginUtility.getInstance().initialize(context);
        SPLTAuth0LoginUtility.getInstance().logout(context);
        if(callback != null)
            callback.onResult(true);
    }

    public boolean isItemLocked(Object model) {
        return false;
    }

    /**
     * @return true if the login provider has a valid token - in most cases it would be just checking the the token is persisted.
     * This method shouldn't consider authorization for this token and user - means you don't need to really validate the token is
     */
    @Override
    public boolean isTokenValid() {
        Log.d(TAG, "isTokenValid: CALLED");
        Log.d(TAG, "isTokenValid: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "isTokenValid: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        boolean isClientTokenValid = false;
        if(mContext != null) {
            SPLTAuth0LoginUtility.getInstance().initialize(mContext);
            isClientTokenValid = SPLTAuth0LoginUtility.getInstance().isClientTokenValid();
        }
        return isClientTokenValid;
    }

    /**
     * @return The token held by the current login provider.
     */
    @Override
    public String getToken() {
        if(mContext != null) {
            SPLTAuth0LoginUtility.getInstance().initialize(mContext);
            return SPLTAuth0LoginUtility.getInstance().getClientToken(mContext);
        }
        return null;
    }

    /**
     * This method allows external screens / JavaScript / React to set the token.
     *
     * @param token The new token.
     */
    @Override
    public void setToken(String token) {
        SPLTAuth0LoginUtility.getInstance().initialize(mContext);
        SPLTAuth0LoginUtility.getInstance().setClientTokenFromExternalInterface(mContext, token);
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
        System.out.println("generateFragment screenMap==>"+screenMap != null?screenMap.toString():"");
        return null;
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
        System.out.println("setPluginConfigurationParams: params==>"+params != null?params.toString():"");
    }

    @Override
    public boolean handlePluginScheme(Context context, Map<String, String> data) {
        System.out.println("handlePluginScheme:data==>"+data!=null?data.toString():"");
        return false;
    }

    @NotNull
    @Override
    public HashMap<String, String> getHook() {
        System.out.println("getHook");
        return null;
    }

    @Override
    public void setHook(@NotNull HashMap<String, String> hashMap) {
        System.out.println("setHook:hashMap==>"+hashMap!=null?hashMap.toString():"");
    }

    @Override
    public void executeHook(@NotNull Context context, @NotNull HookScreenListener hookScreenListener, @Nullable Map<String, ?> map) {
        System.out.println("executeHook:map==>"+map != null?map.toString():"");
    }

    @NotNull
    @Override
    public HookScreenListener getListener() {
        System.out.println("getListener");
        return null;
    }

    @Override
    public void hookDismissed() {
        System.out.println("hookDismissed");
    }

    @Override
    public boolean isFlowBlocker() {
        System.out.println("isFlowBlocker");
        return false;
    }

    @Override
    public boolean isRecurringHook() {
        System.out.println("isRecurringHook");
        return false;
    }

    @Override
    public boolean shouldPresent() {
        System.out.println("shouldPresent");
        return true;
    }
}