package com.dotstudioz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.applicaster.hook_screen.HookScreenListener;
import com.dotstudioz.dotstudioPRO.services.services.CheckChannelSubscriptionStatusService_V1;
import com.dotstudioz.services.APIClient;
import com.dotstudioz.services.APIInterface;

import retrofit2.Call;

public class SPLTSubscriptionUtility {
    private static String TAG = "SPLTSubscriptionUtility";
    /*private static final SPLTSubscriptionUtility ourInstance = new SPLTSubscriptionUtility();

    public static SPLTSubscriptionUtility getInstance() {
        return ourInstance;
    }*/

    public SPLTSubscriptionUtility() {
    }

    public interface ISPLTSubscriptionUtilityHookInterface {
        void spltSubscriptionUtilityHookCompleted(boolean flag);
    }
    private ISPLTSubscriptionUtilityHookInterface ispltSubscriptionUtilityHookInterface;
    public void setISPLTSubscriptionUtilityHookInterface(ISPLTSubscriptionUtilityHookInterface ispltSUHI) {
        if(ispltSUHI != null && ispltSUHI instanceof ISPLTSubscriptionUtilityHookInterface) {
            this.ispltSubscriptionUtilityHookInterface = ispltSUHI;
        } else {
            this.ispltSubscriptionUtilityHookInterface = null;
        }
    }

    public void isSubscriptionValid(Context context, String dspChannelId) {
        Log.d(TAG, "isSubscriptionValid: CALLED");
        if(this.ispltSubscriptionUtilityHookInterface != null) {
            Log.d(TAG, "isSubscriptionValid: interface valid");
            if (SPLTLoginPluginConstants.getInstance().strClientToken != null && SPLTLoginPluginConstants.getInstance().strClientToken.length() > 0 &&
                    SPLTAuth0LoginUtility.getInstance().isClientTokenValid()) {
                Log.d(TAG, "isSubscriptionValid: user already logged in");
                //if the user is already logged in and is valid, then check for subscription
                checkSubscription(context, dspChannelId);
            } else if (SPLTLoginPluginConstants.getInstance().strClientToken != null && SPLTLoginPluginConstants.getInstance().strClientToken.length() > 0 &&
                    !SPLTAuth0LoginUtility.getInstance().isClientTokenValid()) {
                Log.d(TAG, "isSubscriptionValid: user logged in, but client token expired");
                //else if user is logged in, but not valid, then refresh client token, and then check for subscription
                SPLTAuth0LoginUtility.getInstance().setRefreshClientTokenInterface(new SPLTAuth0LoginUtility.IRefreshClientToken() {
                    @Override
                    public void clientTokenRefreshSuccess(String clientToken) {
                        checkSubscription(context, dspChannelId);
                    }

                    @Override
                    public void clientTokenRefreshFailed(String error) {
                        ispltSubscriptionUtilityHookInterface.spltSubscriptionUtilityHookCompleted(false);
                    }
                });
                SPLTAuth0LoginUtility.getInstance().refreshClientTokenInterface(context);
            } else if (SPLTLoginPluginConstants.getInstance().strClientToken == null || SPLTLoginPluginConstants.getInstance().strClientToken.length() == 0) {
                //else if user is not logged in, then open up login screen and then check for subscription
                Log.d(TAG, "isSubscriptionValid: no user logged in!");
                Log.d(TAG, "login: SPLTLoginPluginConstants.apiKey==>" + SPLTLoginPluginConstants.apiKey);
                Log.d(TAG, "login: SPLTLoginPluginConstants.auth0ClientId==>" + SPLTLoginPluginConstants.auth0ClientId);
                SPLTAuth0LoginUtility.getInstance().initialize(context);
                SPLTAuth0LoginUtility.getInstance().login(context, new SPLTAuth0LoginUtility.ILoginPlugin() {
                    @Override
                    public void loginResponse(boolean result, String token) {
                        if (result) {
                            checkSubscription(context, dspChannelId);
                        } else {
                            ispltSubscriptionUtilityHookInterface.spltSubscriptionUtilityHookCompleted(false);
                        }
                    }
                });
            } else {
                ispltSubscriptionUtilityHookInterface.spltSubscriptionUtilityHookCompleted(false);
            }
        }
    }
    private void checkSubscription(Context context, String dspChannelId) {
        checkChannelSubscriptionStatusService(context, dspChannelId);
    }
    APIInterface apiInterface;
    public void checkChannelSubscriptionStatusService(Context context, String idToPass) {
        CheckChannelSubscriptionStatusService_V1 checkChannelSubscriptionStatusServiceV1 = new CheckChannelSubscriptionStatusService_V1(context);
        checkChannelSubscriptionStatusServiceV1.setCheckChannelSubscriptionStatusServiceListener(new CheckChannelSubscriptionStatusService_V1.ICheckChannelSubscriptionStatusService() {
            @Override
            public void checkChannelSubscriptionStatusServiceResponse(boolean unlockedFlag, boolean adsEnabledFlag) {
                Log.d(TAG, "checkChannelSubscriptionStatusServiceResponse");

                if(unlockedFlag) {
                    //afterGetVideoDetails(selectedVideoInfoDTOToLoad);
                    ispltSubscriptionUtilityHookInterface.spltSubscriptionUtilityHookCompleted(true);
                } else {
                    //showSubscriptionModal(selectedVideoInfoDTOToLoad, spotLightChannelDTOSentForSubscriptionCheck);
                    ispltSubscriptionUtilityHookInterface.spltSubscriptionUtilityHookCompleted(false);
                }
            }

            @Override
            public void checkChannelSubscriptionStatusServiceError(String ERROR) {
                if(ERROR != null) {
                    Log.d("checkChannelSubscript", "ERROR==>"+ERROR);
                }
                ispltSubscriptionUtilityHookInterface.spltSubscriptionUtilityHookCompleted(false);
            }

            @Override
            public void accessTokenExpired() {

            }

            @Override
            public void accessTokenRefreshed(String accessToken) {
                SPLTLoginPluginConstants.getInstance().strAccessToken = accessToken;
            }

            @Override
            public void clientTokenExpired() {

            }

            @Override
            public void clientTokenRefreshed(String clientToken) {
                SPLTLoginPluginConstants.getInstance().strClientToken = clientToken;
            }
        });
        //check status of the subscription
        apiInterface = APIClient.getClient(SPLTLoginPluginConstants.getInstance().strAccessToken, SPLTLoginPluginConstants.getInstance().strClientToken).create(APIInterface.class);

        if(idToPass != null && idToPass.length() > 0) {
            Call<Object> call1 = apiInterface.checkSubscriptions(idToPass);
            Log.d("MainActivity", "checkChannelSubscriptionStatusService idToPass==>"+idToPass);
            Log.d("MainActivity", "checkChannelSubscriptionStatusService strClientToken==>"+SPLTLoginPluginConstants.getInstance().strClientToken);
            Log.d("MainActivity", "checkChannelSubscriptionStatusService strAccessToken==>"+SPLTLoginPluginConstants.getInstance().strAccessToken);
            checkChannelSubscriptionStatusServiceV1.checkChannelSubscription(call1);
        } else {
            Log.d("MainActivity", "checkChannelSubscriptionStatusService ERROR idToPass==>"+idToPass);
            Log.d("MainActivity", "checkChannelSubscriptionStatusService strClientToken==>"+SPLTLoginPluginConstants.getInstance().strClientToken);
            Log.d("MainActivity", "checkChannelSubscriptionStatusService strAccessToken==>"+SPLTLoginPluginConstants.getInstance().strAccessToken);
            //checkChannelSubscriptionStatusServiceResponse(false, true);
        }
    }

    public boolean isAlreadyShowingSubscriptionAlertDialog = false;
    public void showSubscriptionAlertDialog(Context context, HookScreenListener hookScreenListener) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("SUBSCRIPTION")
                .setMessage(SPLTLoginPluginConstants.getInstance().visitWebsiteMessage)
                .setCancelable(true)
                .setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(hookScreenListener != null)
                                hookScreenListener.hookFailed(null);
                            isAlreadyShowingSubscriptionAlertDialog = false;
                            dialog.cancel();
                        }
                    });

        AlertDialog alert11 = builder1.create();
        if(!isAlreadyShowingSubscriptionAlertDialog) {
            isAlreadyShowingSubscriptionAlertDialog = true;
            alert11.show();
        }
    }
    public void showSubscriptionAlertDialog(Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("SUBSCRIPTION")
                .setMessage(SPLTLoginPluginConstants.getInstance().visitWebsiteMessage)
                .setCancelable(true)
                .setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isAlreadyShowingSubscriptionAlertDialog = false;
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        if(!isAlreadyShowingSubscriptionAlertDialog) {
            isAlreadyShowingSubscriptionAlertDialog = true;
            alert11.show();
        }
    }
}
