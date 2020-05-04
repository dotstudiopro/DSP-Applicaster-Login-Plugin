package com.dotstudioz;

import android.content.Context;
import android.util.Log;

import com.applicaster.atom.model.APAtomEntry;
import com.applicaster.atom.model.APAtomEntryAdsPlayable;
import com.applicaster.hook_screen.HookScreen;
import com.applicaster.hook_screen.HookScreenListener;
import com.applicaster.plugin_manager.GenericPluginI;
import com.applicaster.plugin_manager.Plugin;
import com.applicaster.plugin_manager.hook.HookListener;
import com.applicaster.plugin_manager.login.BaseLoginContract;
import com.applicaster.plugin_manager.login.LoginContract;
import com.applicaster.plugin_manager.playersmanager.Playable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class LoginPlugin extends BaseLoginContract implements LoginContract, GenericPluginI, HookScreen {

    private static String TAG = "LoginPlugin";

    private Context mContext;

    @Override
    public void executeOnApplicationReady(Context context, HookListener listener) {
        super.executeOnApplicationReady(context, listener);
        mContext = context;
        Log.d(TAG, "executeOnApplicationReady: CALLED");
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "executeOnStartup: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        Log.d(TAG, "executeOnApplicationReady: SPLTLoginPluginConstants.show_on_startup==>"+SPLTLoginPluginConstants.show_on_startup);
        if(SPLTLoginPluginConstants.show_on_startup) {
            SPLTAuth0LoginUtility.getInstance().initialize(context);
            SPLTAuth0LoginUtility.getInstance().login(context);
        }
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
        mContext = context;
        Log.d(TAG, "login: CALLED ctx, addParams, callable");
        Log.d(TAG, "login: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "login: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        Log.d(TAG, "login: requestForSubscription==>"+LoginPluginSharedSingleton.getInstance().requestForSubscription);
        Log.d(TAG, "login: SPLTLoginPluginConstants.getInstance().strClientToken==>"+SPLTLoginPluginConstants.getInstance().strClientToken);
        Log.d(TAG, "login: SPLTAuth0LoginUtility.getInstance().isClientTokenValid()==>"+SPLTAuth0LoginUtility.getInstance().isClientTokenValid());
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
        mContext = context;
        Log.d(TAG, "login: CALLED ctx, Playable, addParams, callable");
        Log.d(TAG, "login: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "login: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        Log.d(TAG, "login: requestForSubscription==>"+LoginPluginSharedSingleton.getInstance().requestForSubscription);
        Log.d(TAG, "login: SPLTLoginPluginConstants.getInstance().strClientToken==>"+SPLTLoginPluginConstants.getInstance().strClientToken);
        Log.d(TAG, "login: SPLTAuth0LoginUtility.getInstance().isClientTokenValid()==>"+SPLTAuth0LoginUtility.getInstance().isClientTokenValid());
        if (SPLTLoginPluginConstants.getInstance().strClientToken != null && SPLTLoginPluginConstants.getInstance().strClientToken.length() > 0 &&
                SPLTAuth0LoginUtility.getInstance().isClientTokenValid()) {
            checkSubscription(context, dspChannelId);
        } else {
            SPLTAuth0LoginUtility.getInstance().initialize(context);
            SPLTAuth0LoginUtility.getInstance().login(context, new SPLTAuth0LoginUtility.ILoginPlugin() {
                @Override
                public void loginResponse(boolean result, String token) {
                    callback.onResult(result);
                }
            });
        }
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
        mContext = context;
        Log.d(TAG, "logout: CALLED");
        Log.d(TAG, "logout: SPLTLoginPluginConstants.apiKey==>"+SPLTLoginPluginConstants.apiKey);
        Log.d(TAG, "logout: SPLTLoginPluginConstants.auth0ClientId==>"+SPLTLoginPluginConstants.auth0ClientId);
        SPLTAuth0LoginUtility.getInstance().initialize(context);
        SPLTAuth0LoginUtility.getInstance().logout(context);
        if(callback != null)
            callback.onResult(true);
    }

    /**
     * Implement this method in order to start the login process. At the end of your process - you must call the `notifyEvent` method.
     *
     * @param context
     * @param additionalParams Extra parameters you would like to provide to the current login provider.
     */
    @Override
    protected void login(Context context, Map additionalParams) {
        mContext = context;
        Log.d(TAG, "login: CALLED ctx & addParams");
    }

    /**
     * Implement this method in order to start the login process. At the end of your process - you must call the `notifyEvent` method.
     *
     * @param context
     * @param playable         The playable we want to perform login for.
     * @param additionalParams Extra parameters you would like to provide to the current login provider.
     */
    @Override
    protected void login(Context context, Playable playable, Map additionalParams) {
        mContext = context;
        Log.d(TAG, "login: playable CALLED");
    }

    /**
     * Implement this method in order to start the logout process. At the end of your process - you must call the `notifyEvent` method.
     *
     * @param context
     * @param additionalParams Extra parameters you would like to provide to the current login provider.
     */
    @Override
    protected void logout(Context context, Map additionalParams) {
        mContext = context;
        Log.d(TAG, "lgout: CALLED");
    }

    private String selectedChannelId;
    /**
     * @param model The model we check for.
     * @return true if the given model requires login (regardless of the user's login status). false otherwise.
     */
    @Override
    public boolean isItemLocked(Object model) {
        boolean flag = false;
        Log.d(TAG, "isItemLocked: CALLED CALLED model==>"+model.toString());
        if(model instanceof APAtomEntry) {
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getTitle()==>"+((APAtomEntry)model).getTitle());
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getAudioMediaUrl()==>"+((APAtomEntry)model).getAudioMediaUrl());
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getId() Selected Item ID==>"+((APAtomEntry)model).getId());
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getContainerId() Parent ID==>"+((APAtomEntry)model).getContainerId());
            selectedChannelId = ((APAtomEntry)model).getContainerId();
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getSummary()==>"+((APAtomEntry)model).getSummary());
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getContent()==>"+((APAtomEntry)model).getContent());
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getAuthor()==>"+((APAtomEntry)model).getAuthor());
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getPromotionName()==>"+((APAtomEntry)model).getPromotionName());
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getSubPromotionName()==>"+((APAtomEntry)model).getSubPromotionName());
            Log.d(TAG, "isItemLocked: ((APAtomEntry)model).getType().name()==>"+((APAtomEntry)model).getType().name());
            flag = false;
        } else if(model instanceof APAtomEntryAdsPlayable) {
            Log.d(TAG, "isItemLocked: Selected Channel ID==>"+selectedChannelId);
            Log.d(TAG, "isItemLocked: ((APAtomEntryAdsPlayable)model).getPlayableId()==>"+((APAtomEntryAdsPlayable)model).getPlayableId());
            Log.d(TAG, "isItemLocked: ((APAtomEntryAdsPlayable)model).getPlayableName()==>"+((APAtomEntryAdsPlayable)model).getPlayableName());
            Log.d(TAG, "isItemLocked: ((APAtomEntryAdsPlayable)model).getPlayableDescription()==>"+((APAtomEntryAdsPlayable)model).getPlayableDescription());
            Log.d(TAG, "isItemLocked: ((APAtomEntryAdsPlayable)model).getSummary()==>"+((APAtomEntryAdsPlayable)model).getVmapUrl());
            Log.d(TAG, "isItemLocked: ((APAtomEntryAdsPlayable)model).isFree()==>"+((APAtomEntryAdsPlayable)model).isFree());
            Log.d(TAG, "isItemLocked: ((APAtomEntryAdsPlayable)model).getFeedName()==>"+((APAtomEntryAdsPlayable)model).getFeedName());
            Log.d(TAG, "isItemLocked: ((APAtomEntryAdsPlayable)model).getContentVideoURL()==>"+((APAtomEntryAdsPlayable)model).getContentVideoURL());

            flag = true;

        }

        if(flag) {
            Log.d(TAG, "isItemLocked: LoginPluginSharedSingleton.getInstance().lastSubscriptionResult==>"+LoginPluginSharedSingleton.getInstance().lastSubscriptionResult);
            if(LoginPluginSharedSingleton.getInstance().lastSubscriptionResult)
                return false;
            else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * initialization of the player plugin configuration with a Plugin,
     * which contains configuration
     *
     * @param plugin
     */
    @Override
    public void setPluginModel(Plugin plugin) {
        if(plugin != null)
            readPluginParameters(plugin.configuration);
    }

    @NotNull
    @Override
    public HashMap<String, String> getHook() {
        Log.d(TAG, "getHook: CALLED");
        return hookScreen;
    }

    @Override
    public void setHook(@NotNull HashMap<String, String> hashMap) {
        Log.d(TAG, "setHook: CALLED");
        this.hookScreen = hookScreen;
    }

    public String dspChannelId = "5bbc1a1c97f815395ed6dabc";

    HookScreenListener hookListener;
    HashMap<String, String> hookScreen = new HashMap<>();
    @Override
    public void executeHook(@NotNull Context context, @NotNull HookScreenListener hookScreenListener, @Nullable Map<String, ?> map) {
        mContext = context;
        Log.d(TAG, "executeHook: CALLED");

        this.hookListener = hookScreenListener;
        SPLTAuth0LoginUtility.getInstance().initialize(context);

        if(this.hookListener == null)
            Log.d(TAG, "executeHook: this.hookListener is null");

        if(map != null) {
            Log.d(TAG, "executeHook: map==>"+map.toString());
        }

        if (SPLTLoginPluginConstants.getInstance().strClientToken != null && SPLTLoginPluginConstants.getInstance().strClientToken.length() > 0 &&
                SPLTAuth0LoginUtility.getInstance().isClientTokenValid()) {
            checkSubscription(context, dspChannelId);
        } else {
            SPLTAuth0LoginUtility.getInstance().initialize(context);
            SPLTAuth0LoginUtility.getInstance().login(context, new SPLTAuth0LoginUtility.ILoginPlugin() {
                @Override
                public void loginResponse(boolean result, String token) {
                    if(result) {
                        checkSubscription(context, dspChannelId);
                    }
                }
            });
        }
    }

    @NotNull
    @Override
    public HookScreenListener getListener() {
        Log.d(TAG, "getListener: HookScreenListener CALLED");
        return hookListener;
    }

    @Override
    public void hookDismissed() {
        Log.d(TAG, "hookDismissed: CALLED");
        getListener().hookFailed(null);
    }

    @Override
    public boolean isFlowBlocker() {
        Log.d(TAG, "isFlowBlocker: CALLED");
        return true;
    }

    @Override
    public boolean isRecurringHook() {
        Log.d(TAG, "isRecurringHook: CALLED");
        return true;
    }

    @Override
    public boolean shouldPresent() {
        Log.d(TAG, "shouldPresent: CALLED");
        return true;
    }


    public SPLTSubscriptionUtility spltSubscriptionUtility;
    private void checkSubscription(Context context, String dspChanId) {
        spltSubscriptionUtility = new SPLTSubscriptionUtility();
        spltSubscriptionUtility.setISPLTSubscriptionUtilityHookInterface(new SPLTSubscriptionUtility.ISPLTSubscriptionUtilityHookInterface() {
            @Override
            public void spltSubscriptionUtilityHookCompleted(boolean flag) {
                Log.d(TAG, "spltSubscriptionUtilityHookCompleted: CALLED!!! flag==>"+flag);
                hookCompleted(!flag);
            }
        });
        spltSubscriptionUtility.isSubscriptionValid(context, dspChanId);
    }
    private void hookCompleted(boolean flag) {
        Log.d(TAG, "hookCompleted: CALLED, flag==>"+flag);
        LoginPluginSharedSingleton.getInstance().lastSubscriptionResult = flag;
        //flag = false;
        if(this.hookListener != null) {
            Log.d(TAG, "hookCompleted: flag==>"+flag);
            if (flag) {
                //Toast.makeText(mContext, "Subscribed!!!", Toast.LENGTH_SHORT).show();
                this.hookListener.hookCompleted(null);
            } else {
                //Toast.makeText(mContext, "Not Subscribed!!!", Toast.LENGTH_SHORT).show();
                LoginPluginSharedSingleton.getInstance().requestForSubscription = false;
                (new SPLTSubscriptionUtility()).showSubscriptionAlertDialog(mContext, hookListener);
                //this.hookListener.hookFailed(null);
            }
        }
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
                    } else if(keyString.equals(SPLTLoginPluginConstants.VISIT_WEBSITE_MESSAGE_KEY)) {
                        SPLTLoginPluginConstants.getInstance().visitWebsiteMessage = ((Map)obj).get(keyString).toString();
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
