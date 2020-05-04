package com.dotstudioz.services;

import android.content.Context;
import android.util.Log;

import com.dotstudioz.dotstudioPRO.models.dto.ParameterItem;
import com.dotstudioz.dotstudioPRO.models.dto.SubscriptionDTO;
import com.dotstudioz.dotstudioPRO.services.accesstoken.AccessTokenHandler;
import com.dotstudioz.dotstudioPRO.services.accesstoken.ClientTokenRefreshClass;
import com.dotstudioz.dotstudioPRO.services.constants.ApplicationConstantURL;
import com.dotstudioz.dotstudioPRO.services.constants.ApplicationConstants;
import com.dotstudioz.dotstudioPRO.services.services.CommonAsyncHttpClient_V1;
import com.dotstudioz.dotstudioPRO.services.services.CompanyTokenService;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by mohsin on 08-10-2016.
 */

public class CheckChannelSubscriptionStatusServiceInternal_V1 /*implements CommonAsyncHttpClient_V1.ICommonAsyncHttpClient_V1*/ {

    public ICheckChannelSubscriptionStatusServiceInternal iCheckChannelSubscriptionStatusServiceInternal;
    public interface ICheckChannelSubscriptionStatusServiceInternal {
        void CheckChannelSubscriptionStatusServiceInternalResponse(boolean unlockedFlag, boolean adsEnabledFlag);
        void CheckChannelSubscriptionStatusServiceInternalError(String ERROR);
        void accessTokenExpired();
        void accessTokenRefreshed(String accessToken);
        void clientTokenExpired();
        void clientTokenRefreshed(String clientToken);
    }

    Context context;
    public CheckChannelSubscriptionStatusServiceInternal_V1(Context ctx) {
        context = ctx;
        if (ctx instanceof ICheckChannelSubscriptionStatusServiceInternal)
            iCheckChannelSubscriptionStatusServiceInternal = (ICheckChannelSubscriptionStatusServiceInternal) ctx;
        /*else
            throw new RuntimeException(ctx.toString()+ " must implement ICheckChannelSubscriptionStatusServiceInternal");*/
    }

    // Assign the listener implementing events interface that will receive the events
    public void setCheckChannelSubscriptionStatusServiceInternalListener(ICheckChannelSubscriptionStatusServiceInternal callback) {
        this.iCheckChannelSubscriptionStatusServiceInternal = callback;
    }

    private String xAccessToken;
    private String xClientToken;
    private String api;
    public void CheckChannelSubscriptionStatusServiceInternal(String xAccessToken, String xClientToken, String API_URL) {
        this.xAccessToken = xAccessToken;
        this.xClientToken = xClientToken;
        this.api = API_URL;
        if (iCheckChannelSubscriptionStatusServiceInternal == null) {
            if (context != null && context instanceof CheckChannelSubscriptionStatusServiceInternal_V1.ICheckChannelSubscriptionStatusServiceInternal) {
                iCheckChannelSubscriptionStatusServiceInternal = (CheckChannelSubscriptionStatusServiceInternal_V1.ICheckChannelSubscriptionStatusServiceInternal) context;
            }
            if (iCheckChannelSubscriptionStatusServiceInternal == null) {
                throw new RuntimeException(context.toString()+ " must implement ICheckChannelSubscriptionStatusServiceInternal or setCheckChannelSubscriptionStatusServiceInternalListener");
            }
        }

        ArrayList<ParameterItem> headerItemsArrayList = new ArrayList<>();
        headerItemsArrayList.add(new ParameterItem("x-access-token", xAccessToken));
        headerItemsArrayList.add(new ParameterItem("x-client-token", xAccessToken));

        getCommonAsyncHttpClientV1().setCommonAsyncHttpClient_V1Listener(new CommonAsyncHttpClient_V1.ICommonAsyncHttpClient_V1() {
            @Override
            public void onResultHandler(JSONObject response) {
                onResultHandler1(response);
            }

            @Override
            public void onErrorHandler(String ERROR) {
                onErrorHandler1(ERROR);
            }

            @Override
            public void accessTokenExpired() {
                accessTokenExpired1();
            }

            @Override
            public void clientTokenExpired() {
                clientTokenExpired1();
            }
        });

        getCommonAsyncHttpClientV1().getAsyncHttpsClient(headerItemsArrayList, null,
                API_URL, AccessTokenHandler.getInstance().fetchTokenCalledInCategoriesPageString);
    }

    private CommonAsyncHttpClient_V1 commonAsyncHttpClientV1;
    private CommonAsyncHttpClient_V1 getCommonAsyncHttpClientV1() {
        if(commonAsyncHttpClientV1 == null) {
            commonAsyncHttpClientV1 = new CommonAsyncHttpClient_V1();
        }
        return commonAsyncHttpClientV1;
    }

    //@Override
    public void onResultHandler1(JSONObject response) {
        if(response != null)
            Log.d("onResultHandler1", "onResultHandler1==>"+response.toString());
        try {
            if (response != null)
                resultProcessingForSubscriptions(response);
            else
                iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(false, true);
        } catch(Exception e) {
            e.printStackTrace();
            iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(false, true);
        }
    }
    //@Override
    public void onErrorHandler1(String ERROR) {
        Log.d("onErrorHandler1", "onErrorHandler1==>"+ERROR);
        iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalError(ERROR);
    }
    //@Override
    public void accessTokenExpired1() {
        if(!refreshAccessToken)
            refreshAccessToken();
        else
            iCheckChannelSubscriptionStatusServiceInternal.accessTokenExpired();
    }
    //@Override
    public void clientTokenExpired1() {
        if(refreshClientToken)
            refreshClientToken();
        else
            iCheckChannelSubscriptionStatusServiceInternal.clientTokenExpired();
    }

    private ArrayList<SubscriptionDTO> userSubscriptionDTOArrayList = new ArrayList<SubscriptionDTO>();
    public boolean resultProcessingForSubscriptions(JSONObject response) {
        try {
            if (response != null) {
                if (response.has("unlocked")) {
                    return response.getBoolean("unlocked");
                } else {
                    return false;
                }
            }
        } catch(Exception e) {
            return false;
        }

        return false;
    }

    /**
     * expecting 3 parameters in the response
     * success=true/false
     * unlocked=true/false
     * ads_enabled=true/false
     * @param response boolean flag
     * @param paramName it could be either unlocked or adsenabled
     * @return
     */
    public boolean resultProcessingForSubscriptions(JSONObject response, String paramName) {
        try {
            if (response != null) {
                if (response.has(paramName)) {
                    return response.getBoolean(paramName);
                } else {
                    return false;
                }
            }
        } catch(Exception e) {
            return false;
        }

        return false;
    }

    public boolean resultProcessingForPurchase(JSONObject response) {
         try {
                if (response != null) {
                    // if let bSuccess = infoDict["success"] as? Bool, bSuccess == true {
                    if (response.has("success")) {
                        if(response.getBoolean("success"))
                        {
                            if (response.has("unlocked")) {
                                return response.getBoolean("unlocked");
                            } else {
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }
            } catch(Exception e) {
                return false;
            }

            return false;
    }


    /*private void resultProcessingForSubscriptions(JSONObject response) {
        try {
            if (response != null) {
                if (response.has("unlocked")) {
                    iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(response.getBoolean("unlocked"));
                }
            }
        } catch(Exception e) {
            iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(false);
        }
    }*/


    /**
     *
     *
     {
     "success": true,
     "unlocked": false,
     "ads_enabled": false
     }
     */


    /**
     * call to check subscription status of a channel
     * Usage in MainActivity in Revry:-
     * public int noOfChannelSubscriptionStatusCallsMade = 0;
     *     public ArrayList<ChannelSubscriptionStatusDTO> checkChannelSubscriptionStatusCallArrayList = new ArrayList();
     *     public void checkChannelSubscriptionStatusCall() {
     *         if(noOfChannelSubscriptionStatusCallsMade < checkChannelSubscriptionStatusCallArrayList.size()) {
     *             CheckChannelSubscriptionStatusServiceInternal(checkChannelSubscriptionStatusCallArrayList.get(noOfChannelSubscriptionStatusCallsMade).dsproChannelId);
     *
     *             noOfChannelSubscriptionStatusCallsMade++;
     *         } else {
     *             //check if everything is ok, and mark the flags appropriately
     *             //selectedChannelIDUnLocked = true or false;
     *             if(calledFromPostProcessingMissingChannelDataServiceResponse) {
     *                 postProcessingMissingChannelDataServiceResponseFromSubscriptionStatusCheck(fetchMissingChannelSelectedChannelID, fetchMissingChannelSpotLightChannelDTO, fetchMissingChannelSpotLightCategoriesDTO, fetchMissingChannelResponseObject, fetchMissingChannelMissingVideoInfoDTOList);
     *             } else if(calledFromPostProcessingMissingChildChannelDataServiceResponse) {
     *                 postProcessingMissingChildChannelDataServiceResponseFromSubscriptionStatusCheck(fetchMissingChannelSelectedChannelID, fetchMissingChannelSpotLightChannelDTO, fetchMissingChannelSpotLightCategoriesDTO);
     *             }
     *             calledFromPostProcessingMissingChannelDataServiceResponse = false;
     *             calledFromPostProcessingMissingChildChannelDataServiceResponse = false;
     *         }
     *     }
     *     CheckChannelSubscriptionStatusServiceInternal_V1 CheckChannelSubscriptionStatusServiceInternalV1;
     *     public void CheckChannelSubscriptionStatusServiceInternal(String idToPass) {
     *         if(ApplicationConstants.CLIENT_TOKEN != null && ApplicationConstants.CLIENT_TOKEN.length() > 0) {
     *             CheckChannelSubscriptionStatusServiceInternalV1 = new CheckChannelSubscriptionStatusServiceInternal_V1(this);
     *
     *             //check status of the subscription
     *             apiInterface = APIClient.getClient(ApplicationConstants.xAccessToken, ApplicationConstants.CLIENT_TOKEN).create(APIInterface.class);
     *
     *             if(idToPass != null && idToPass.length() > 0) {
     *                 Call<Object> call1 = apiInterface.checkSubscriptions(idToPass);
     *                 CheckChannelSubscriptionStatusServiceInternalV1.checkChannelSubscription(call1);
     *             } else {
     *                 checkChannelSubscriptionStatusCallResponse(false);
     *             }
     *         } else {
     *             checkChannelSubscriptionStatusCallResponse(false);
     *         }
     *     }
     *     @Override
     *     public void CheckChannelSubscriptionStatusServiceInternalResponse(boolean flag) {
     *         checkChannelSubscriptionStatusCallResponse(flag);
     *     }
     *     @Override
     *     public void CheckChannelSubscriptionStatusServiceInternalError(String ERROR) {
     *         if(ERROR != null) {
     *             Log.d("CheckChannelSubscriptionStatusServiceInternalError", "ERROR==>"+ERROR);
     *         }
     *         checkChannelSubscriptionStatusCallResponse(false);
     *     }
     *     public void checkChannelSubscriptionStatusCallResponse(boolean flag) {
     *         checkChannelSubscriptionStatusCallArrayList.get(noOfChannelSubscriptionStatusCallsMade).unlocked = flag;
     *         checkChannelSubscriptionStatusCallArrayList.get(noOfChannelSubscriptionStatusCallsMade).assigned = true;
     *         noOfChannelSubscriptionStatusCallsMade++;
     *
     *         checkChannelSubscriptionStatusCall();
     *     }
     * @param call1
     */
    public void checkChannelSubscription(Call call1) {
        call1.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    if(response.body() != null) {
                        Log.d("checkChanSubs", "" + (new Gson().toJson(response.body())));
                        JSONObject obj = new JSONObject("" + (new Gson().toJson(response.body())));
                        try {
                            if (obj.has("success") && obj.getBoolean("success")) {
                                iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(resultProcessingForSubscriptions(obj, "unlocked"), resultProcessingForSubscriptions(obj, "ads_enabled"));
                            } else {
                                iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(false, true);
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                            iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(false, true);
                        }
                    } else {
                        iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(false, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(false, true);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                call.cancel();
                iCheckChannelSubscriptionStatusServiceInternal.CheckChannelSubscriptionStatusServiceInternalResponse(false, true);
            }
        });
    }



    boolean refreshAccessToken = false;
    private void refreshAccessToken() {
        CompanyTokenService companyTokenService = new CompanyTokenService(context);
        companyTokenService.setCompanyTokenServiceListener(new CompanyTokenService.ICompanyTokenService() {
            @Override
            public void companyTokenServiceResponse(JSONObject responseBody) {
                try {
                    ApplicationConstants.xAccessToken = responseBody.getString("token");
                    iCheckChannelSubscriptionStatusServiceInternal.accessTokenRefreshed(ApplicationConstants.xAccessToken);
                    CheckChannelSubscriptionStatusServiceInternal(ApplicationConstants.xAccessToken, xClientToken, api);
                } catch (Exception e) {
                    e.printStackTrace();
                    iCheckChannelSubscriptionStatusServiceInternal.accessTokenExpired();
                }
            }

            @Override
            public void companyTokenServiceError(String responseBody) {
                iCheckChannelSubscriptionStatusServiceInternal.accessTokenExpired();
            }
        });
        refreshAccessToken = true;
        companyTokenService.requestForToken(ApplicationConstants.COMPANY_KEY, ApplicationConstantURL.TOKEN_URL);
    }

    boolean refreshClientToken = false;
    private void refreshClientToken() {
        ClientTokenRefreshClass clientTokenRefreshClass = new ClientTokenRefreshClass(context);
        clientTokenRefreshClass.setClientTokenRefreshListener(new ClientTokenRefreshClass.IClientTokenRefresh() {
            @Override
            public void clientTokenResponse(String ACTUAL_RESPONSE) {
                try {
                    String idToken = ACTUAL_RESPONSE;
                    iCheckChannelSubscriptionStatusServiceInternal.clientTokenRefreshed(idToken);
                    ApplicationConstants.CLIENT_TOKEN = idToken;
                    CheckChannelSubscriptionStatusServiceInternal(ApplicationConstants.xAccessToken, idToken, api);
                } catch(Exception e) {
                    e.printStackTrace();
                    iCheckChannelSubscriptionStatusServiceInternal.clientTokenExpired();
                }
            }

            @Override
            public void clientTokenError(String ERROR) {
                iCheckChannelSubscriptionStatusServiceInternal.clientTokenExpired();
            }
        });
        clientTokenRefreshClass.refreshExistingClientToken(ApplicationConstants.xAccessToken, ApplicationConstants.CLIENT_TOKEN);
    }
}
