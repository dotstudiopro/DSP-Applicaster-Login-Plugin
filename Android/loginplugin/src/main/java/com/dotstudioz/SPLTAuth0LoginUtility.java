package com.dotstudioz;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.ParameterBuilder;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import com.dotstudioz.dotstudioPRO.services.constants.ApplicationConstantURL;
import com.dotstudioz.dotstudioPRO.services.constants.ApplicationConstants;
import com.dotstudioz.dotstudioPRO.services.services.CompanyTokenService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

public class SPLTAuth0LoginUtility {
    private static String TAG = "SPLTAuth0LoginUtility";
    private static final SPLTAuth0LoginUtility ourInstance = new SPLTAuth0LoginUtility();

    public static SPLTAuth0LoginUtility getInstance() {
        return ourInstance;
    }

    public String TOKEN_RESPONSE_SHARED_PREFERENCE = "com.dotstudioz.tokenResponse";
    public String IS_FB_USER_RESPONSE_SHARED_PREFERENCE = "com.dotstudioz.isFBUserResponse";
    public String FACEBOOK_RESPONSE_SHARED_PREFERENCE = "com.dotstudioz.facebookResponse";
    public String USER_DETAILS_RESPONSE_SHARED_PREFERENCE = "com.dotstudioz.userDetails";
    public String USER_EMAIL_DETAILS_RESPONSE_SHARED_PREFERENCE = "com.dotstudioz.userEmailDetails";
    public String AUTH0_ID_TOKEN_RESPONSE_SHARED_PREFERENCE = "com.dotstudioz.auth0idtoken";
    public String AUTH0_REFRESH_TOKEN_RESPONSE_SHARED_PREFERENCE = "com.dotstudioz.auth0refreshtoken";
    private SPLTAuth0LoginUtility() { }

    private Lock mLock;
    public Context mContext;

    public void initialize(Context context) {
        mContext = context;

        initializeAPIDomain();
        if(isAccessTokenValid(context)) {
            initializeSharedPreferenceVariables(getCompanyKeyFromAccessToken(context));
        } else {
            requestAccessToken(context);
        }
    }
    private void initializeAPIDomain() {
        ApplicationConstantURL.API_DOMAIN = "http://api.myspotlight.tv"; //PRODUCTION SERVER
        ApplicationConstantURL.API_DOMAIN_S = "https://api.myspotlight.tv"; //PRODUCTION SERVER
        ApplicationConstantURL.getInstance().setAPIDomain();
    }
    private void initializeSharedPreferenceVariables(String uniqueCompanyIdentifier) {
        ApplicationConstants.setSharedPreferenceNames(
                TOKEN_RESPONSE_SHARED_PREFERENCE + uniqueCompanyIdentifier,
                IS_FB_USER_RESPONSE_SHARED_PREFERENCE + uniqueCompanyIdentifier,
                FACEBOOK_RESPONSE_SHARED_PREFERENCE + uniqueCompanyIdentifier,
                USER_DETAILS_RESPONSE_SHARED_PREFERENCE + uniqueCompanyIdentifier,
                USER_EMAIL_DETAILS_RESPONSE_SHARED_PREFERENCE + uniqueCompanyIdentifier,
                AUTH0_ID_TOKEN_RESPONSE_SHARED_PREFERENCE + uniqueCompanyIdentifier,
                AUTH0_REFRESH_TOKEN_RESPONSE_SHARED_PREFERENCE + uniqueCompanyIdentifier
        );
    }
    private String getCompanyKeyFromAccessToken(Context context) {
        String accessToken;
        //Check if accessToken is present, if not then fetch it
        try {
            accessToken = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);
            if(accessToken != null && accessToken.length() > 0) {
                SPLTLoginPluginConstants.strAccessToken = accessToken;
                try {
                    Base64 decoder = new Base64(true);
                    byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.strAccessToken.split("\\.")[1]);
                    String s = new String(secret);
                    JSONObject jsonObject = new JSONObject(s);

                    if (jsonObject.has("iss")) {
                        String companyKey = jsonObject.getString("iss");
                        if (companyKey != null && companyKey.length() > 0) {
                            return companyKey;
                        } else {
                            return "";
                        }
                    } else {
                        return "";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }
    private boolean isAccessTokenValid(Context context) {
        String accessToken;
        //Check if accessToken is present, if not then fetch it
        try {
            accessToken = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);
            if(accessToken != null && accessToken.length() > 0) {
                SPLTLoginPluginConstants.strAccessToken = accessToken;
                try {
                    Base64 decoder = new Base64(true);
                    byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.strAccessToken.split("\\.")[1]);
                    String s = new String(secret);
                    JSONObject jsonObject = new JSONObject(s);

                    if (jsonObject.has("expires")) {
                        Date dt = new Date(jsonObject.getLong("expires"));
                        Log.d(TAG, "isAccessTokenExpired: Expiry Date From token==>" + dt.toString());
                        Date currentDate = new Date();
                        Log.d(TAG, "isAccessTokenExpired: Current Date==>" + currentDate.toString());

                        if (dt.compareTo(currentDate) > 0) {
                            Log.d(TAG, "isAccessTokenExpired: token is not expired");
                            return true;
                        } else {
                            Log.d(TAG, "isAccessTokenExpired: token is expired");
                            return false;
                        }
                    } else {
                        Log.d(TAG, "isAccessTokenExpired: token is expired");
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void login(Context context) {
        showLoginController(context);
    }
    private void showLoginController(Context context) {
        Log.d(TAG, "showLoginController: CALLED");
        mContext = context;

        Map<String, Object> parametersMap = ParameterBuilder
                .newAuthenticationBuilder()
                .setScope(ParameterBuilder.SCOPE_OFFLINE_ACCESS)
                .set("c", getCompanyKeyFromAccessToken(context))
                .asDictionary();

            /*Theme customizedLockTheme = Theme.newBuilder()
                    //.withHeaderLogo(R.drawable.famil_league_logo)
                    .withHeaderLogo(R.drawable.com_auth0_lock_header_logo)
                    .withHeaderTitle("HEADER TITLE")
                    .withHeaderTitleColor(R.color.black)
                    .withPrimaryColor(R.color.black)
                    .withDarkPrimaryColor(R.color.black)
                    .withHeaderColor(R.color.white)
                    .build();*/
        int initialScreenToUse = 0;

        /*if(AppController.initialScreenToUse == 1)
            initialScreenToUse = 1;
        else
            initialScreenToUse = 0;*/

        Auth0 auth0 = new Auth0(SPLTLoginPluginConstants.auth0ClientId, SPLTLoginPluginConstants.auth0Domain);
        /*Options options = new Options();
        options.withTheme(customizedLockTheme);*/
        mLock = Lock.newBuilder(auth0, mCallback).withAuthenticationParameters(parametersMap)//.withTheme(customizedLockTheme)
                //Add parameters to the build
                .withScheme("demo")
                .initialScreen(initialScreenToUse)
                .closable(true)
                //.build();
                .build(context);
            /*mLock.onCreate(this);
            auth0.getAuthorizeUrl();*/
        context.startActivity(mLock.newIntent(context));
    }
    private Credentials mCredentials;
    private String mIdToken;
    private String mRefreshToken;
    private final LockCallback mCallback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            //Toast.makeText(getApplicationContext(), "Log In - Success", Toast.LENGTH_SHORT).show();
            mCredentials = credentials;
            mIdToken = credentials.getIdToken();
            mRefreshToken = credentials.getRefreshToken();

            SharedPreferencesUtil.getInstance(mContext).addToSharedPreference(
                    ApplicationConstants.AUTH0_ID_TOKEN_RESPONSE_SHARED_PREFERENCE,
                    mIdToken,
                    ApplicationConstants.AUTH0_ID_TOKEN_RESPONSE_SHARED_PREFERENCE_KEY
            );

            getUserProfileFromAuth0();
        }

        @Override
        public void onCanceled() {
            Log.d(TAG, "onCanceled: CALLED!!!");
        }

        @Override
        public void onError(LockException error){
            Toast.makeText(mContext, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
        }
    };
    private UserProfile mUserProfile;
    private Auth0 mAuth0;
    private String mUserEmailId;
    private String mUserIdString;
    private String mUserFirstName;
    private String mUserLastName;
    private String mUserAvatarPath;
    private void getUserProfileFromAuth0() {
        mAuth0 = new Auth0(SPLTLoginPluginConstants.auth0ClientId, SPLTLoginPluginConstants.auth0Domain);
        // The process to reclaim an UserProfile is preceded by an Authentication call.
        AuthenticationAPIClient aClient = new AuthenticationAPIClient(mAuth0);

        if (mIdToken != null && mIdToken.length() > 0) {
            aClient.tokenInfo(mIdToken)
                    .start(new BaseCallback<UserProfile, AuthenticationException>() {
                        @Override
                        public void onSuccess(final UserProfile payload) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                public void run() {
                                    mUserProfile = payload;
                                    if (mUserProfile != null && mUserProfile.getExtraInfo() != null && mUserProfile.getExtraInfo().size() > 0) {

                                        if (mUserProfile.getEmail() != null && mUserProfile.getEmail().length() > 0) {
                                            mUserEmailId = mUserProfile.getEmail();
                                            Log.d(TAG, "run: mUserEmailId==>"+mUserEmailId);

                                            SharedPreferencesUtil.getInstance(mContext).addToSharedPreference(
                                                    ApplicationConstants.USER_EMAIL_DETAILS_RESPONSE_SHARED_PREFERENCE,
                                                    mUserEmailId,
                                                    ApplicationConstants.USER_EMAIL_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);
                                        }


                                        if (mUserProfile.getExtraInfo().containsKey("spotlight")) {
                                            setClientTokenAndUserDetails(mUserProfile.getExtraInfo().get("spotlight").toString());
                                        }
                                    } else {
                                        Toast.makeText(mContext, "User details not available!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }

                        @Override
                        public void onFailure(AuthenticationException error) {
                            Log.d(TAG, "onFailure: ");
                        }
                    });
        }
    }
    private void setClientTokenAndUserDetails(String clientToken) {
        SPLTLoginPluginConstants.strClientToken = clientToken;
        Log.d(TAG, "scrapeOutInformationFromClientToken: SPLTLoginPluginConstants.strClientToken==>"+ SPLTLoginPluginConstants.strClientToken);

        Base64 decoder = new Base64(true);
        byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.strClientToken.split("\\.")[1]);
        String s = new String(secret);
        //Log.d("MainActivity", "String of secret:String of secret:" + s);
        try {
            JSONObject spotlightJSONObject = new JSONObject(s);

            if (spotlightJSONObject.has("context")) {
                if (spotlightJSONObject.getJSONObject("context").has("id")) {
                    mUserIdString = spotlightJSONObject.getJSONObject("context").getString("id");
                }

                if (spotlightJSONObject.getJSONObject("context").has("first_name")) {
                    mUserFirstName = spotlightJSONObject.getJSONObject("context").getString("first_name");
                }

                if (spotlightJSONObject.getJSONObject("context").has("last_name")) {
                    mUserLastName = spotlightJSONObject.getJSONObject("context").getString("last_name");
                }

                if (spotlightJSONObject.getJSONObject("context").has("avatar")) {
                    mUserAvatarPath = spotlightJSONObject.getJSONObject("context").getString("avatar");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferencesUtil.getInstance(mContext).addToSharedPreference(
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE,
                SPLTLoginPluginConstants.strClientToken,
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);
    }

    String token0252_27022020 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI1YWIxNjAwNTk3ZjgxNTAxNGIzNTc4OTEiLCJleHBpcmVzIjoxNTgyNzYyNjU3ODEyLCJjb250ZXh0Ijp7ImF2YXRhciI6Imh0dHBzOi8vczMtdXMtd2VzdC0xLmFtYXpvbmF3cy5jb20vbWVkaWEtYXdzLmRvdHN0dWRpb3Byby5jb20vYXZhdGFycy81NzAyMTI2NDk3ZjgxNTlkMjM4ODFiYmYucG5nIiwiaWQiOiI1NzAyMTI2NDk3ZjgxNTlkMjM4ODFiYmYiLCJmaXJzdF9uYW1lIjoiTW9oIiwibGFzdF9uYW1lIjoiU2hhIn19.ffXGwGvQGEvXM9hcTYGBPXwx4FUSwJZB147dxc31tVw";
    public boolean isClientTokenExpired() {
        if(SPLTLoginPluginConstants.strClientToken != null && SPLTLoginPluginConstants.strClientToken.length() > 0) {
            //String testClientToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI1N2ZlOGZlMzk5ZjgxNWUzMDlkYmMyZjQiLCJleHBpcmVzIjoxNDkxNDM4NDU5NDc3LCJjb250ZXh0Ijp7ImF2YXRhciI6Imh0dHA6Ly9jZG4uZG90c3R1ZGlvcHJvLmNvbS9hdmF0YXJzLzU4NWQ0MjA2ZTY2YzBiMjUzNTc5ZTc3YTU0MmM0ZjU0OTdmODE1N2M0NzdiMjNjNi5wbmciLCJpZCI6IjU0MmM0ZjU0OTdmODE1N2M0NzdiMjNjNiIsImZpcnN0X25hbWUiOiJNb2hzaW4iLCJsYXN0X25hbWUiOiJTaGFpa2gifX0.OFtX3rUPZVzLLiIhlI7vXO5O4Jh-cBk-MSjHUUD9d9M";

            try {
                Base64 decoder = new Base64(true);
                byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.strClientToken.split("\\.")[1]);
                //byte[] secret = decoder.decodeBase64(token0252_27022020.split("\\.")[1]);
                String s = new String(secret);
                JSONObject jsonObject = new JSONObject(s);
                Log.d(TAG, "isClientTokenExpired: asdas");

                if (jsonObject.has("expires")) {
                    Date dt = new Date(jsonObject.getLong("expires"));
                    Log.d(TAG, "isClientTokenExpired: Expiry Date From token==>" + dt.toString());
                    Date currentDate = new Date();
                    Log.d(TAG, "isClientTokenExpired: Current Date==>" + currentDate.toString());

                    if (dt.compareTo(currentDate) > 0) {
                        Log.d(TAG, "isClientTokenExpired: token is not expired");
                        return true;
                    } else {
                        Log.d(TAG, "isClientTokenExpired: token is expired");
                        return false;
                    }
                } else {
                    Log.d(TAG, "isClientTokenExpired: token is expired");
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean isUserLoggedIn(Context context) {
        Log.d(TAG, "isUserLoggedIn: CALLED");
        return checkIfUserAlreadyAuthenticated(context);
    }
    private boolean checkIfUserAlreadyAuthenticated(Context context) {
        String sp = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);

        if(sp == null || sp.length() == 0) {
            SPLTLoginPluginConstants.strClientToken = "";
            Log.d(TAG, "checkIfUserAlreadyAuthenticated: FALSE==>"+ SPLTLoginPluginConstants.strClientToken);
            return false;
        } else {
            setTokenFromSharedPreference(context);
            return true;
        }
    }
    private void setTokenFromSharedPreference(Context context) {
        String sp = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);

        if(sp == null || sp.length() == 0) {
            SPLTLoginPluginConstants.strClientToken = "";
        } else {
            SPLTLoginPluginConstants.strClientToken = sp;
            Log.d(TAG, "checkIfUserAlreadyAuthenticated: TRUE==>"+ SPLTLoginPluginConstants.strClientToken);
        }

        if(SPLTLoginPluginConstants.strClientToken != null && SPLTLoginPluginConstants.strClientToken.length() > 0) {
            try {
                Base64 decoder = new Base64(true);
                byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.strClientToken.split("\\.")[1]);
                String s = new String(secret);

                try {
                    JSONObject spotlightJSONObject = new JSONObject(s);

                    if (spotlightJSONObject.has("context")) {
                        if (spotlightJSONObject.getJSONObject("context").has("id")) {
                            mUserIdString = spotlightJSONObject.getJSONObject("context").getString("id");
                        }

                        if (spotlightJSONObject.getJSONObject("context").has("first_name")) {
                            mUserFirstName = spotlightJSONObject.getJSONObject("context").getString("first_name");
                        }

                        if (spotlightJSONObject.getJSONObject("context").has("last_name")) {
                            mUserLastName = spotlightJSONObject.getJSONObject("context").getString("last_name");
                        }

                        if (spotlightJSONObject.getJSONObject("context").has("avatar")) {
                            mUserAvatarPath = spotlightJSONObject.getJSONObject("context").getString("avatar");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
            }

            mUserEmailId = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                    ApplicationConstants.USER_EMAIL_DETAILS_RESPONSE_SHARED_PREFERENCE,
                    ApplicationConstants.USER_EMAIL_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);
        }
    }

    public void logout(Context context) {
        Log.d(TAG, "logout: CALLED");
        SPLTLoginPluginConstants.strClientToken = "";

        SharedPreferencesUtil.getInstance(context).removeFromSharedPreference(
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);

        SharedPreferencesUtil.getInstance(context).removeFromSharedPreference(
                ApplicationConstants.IS_FB_USER_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.IS_FB_USER_RESPONSE_SHARED_PREFERENCE_KEY);

        SharedPreferencesUtil.getInstance(context).removeFromSharedPreference(
                ApplicationConstants.FACEBOOK_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.FACEBOOK_RESPONSE_SHARED_PREFERENCE_KEY);

        SharedPreferencesUtil.getInstance(context).removeFromSharedPreference(
                ApplicationConstants.AUTH0_REFRESH_TOKEN_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.AUTH0_REFRESH_TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);

        SharedPreferencesUtil.getInstance(context).removeFromSharedPreference(
                ApplicationConstants.AUTH0_ID_TOKEN_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.AUTH0_ID_TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);

        SharedPreferencesUtil.getInstance(context).removeFromSharedPreference(
                ApplicationConstants.USER_EMAIL_DETAILS_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.USER_EMAIL_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);

        mCredentials = null;
        mIdToken = "";
        mUserIdString = "";
        mUserFirstName = "";
        mUserLastName = "";
        mUserAvatarPath = "";
    }

    /*public String getmUserEmailId() {
        return mUserEmailId;
    }
    public String getmUserIdString() {
        return mUserIdString;
    }
    public String getmUserFirstName() {
        return mUserFirstName;
    }
    public String getmUserLastName() {
        return mUserLastName;
    }
    public String getmUserAvatarPath() {
        return mUserAvatarPath;
    }*/









    private boolean requestFromRefreshClientTokenFlag = false;
    private void requestAccessToken(final Context context) {
        CompanyTokenService companyTokenService = new CompanyTokenService(context);
        companyTokenService.setCompanyTokenServiceListener(new CompanyTokenService.ICompanyTokenService() {
            @Override
            public void companyTokenServiceResponse(JSONObject responseBody) {
                try {
                    if(responseBody != null && responseBody.has("token")) {
                        SPLTLoginPluginConstants.strAccessToken = responseBody.getString("token");

                        SharedPreferencesUtil.getInstance(context).addToSharedPreference(
                                ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                                SPLTLoginPluginConstants.strAccessToken,
                                ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);

                        Log.d(TAG, "companyTokenServiceResponse: SPLTLoginPluginConstants.strAccessToken==>"+ SPLTLoginPluginConstants.strAccessToken);
                        if(requestFromRefreshClientTokenFlag) {
                            requestFromRefreshClientTokenFlag = false;
                            // call back the refreshClientToken now
                            refreshClientToken(context);
                        }
                    } else {
                        SPLTLoginPluginConstants.strAccessToken = "";
                    }
                } catch(Exception e) {
                    SPLTLoginPluginConstants.strAccessToken = "";
                    e.printStackTrace();
                }
            }

            @Override
            public void companyTokenServiceError(String responseBody) {
                SPLTLoginPluginConstants.strAccessToken = "";
            }
        });
        companyTokenService.requestForToken(SPLTLoginPluginConstants.apiKey, ApplicationConstantURL.getInstance().TOKEN_URL);
    }
    private void refreshClientToken(final Context context) {
        String accessToken = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);
        if(accessToken != null && accessToken.length() > 0) {
            SPLTLoginPluginConstants.strAccessToken = accessToken;
            Log.d(TAG, "refreshClientToken: From SharedPreference SPLTLoginPluginConstants.strAccessToken==>"+ SPLTLoginPluginConstants.strAccessToken);
        }

        if(SPLTLoginPluginConstants.strAccessToken == null || SPLTLoginPluginConstants.strAccessToken.length() == 0) {
            requestFromRefreshClientTokenFlag = true;
            requestAccessToken(context);
            return;
        }

        if(SPLTLoginPluginConstants.strAccessToken != null && SPLTLoginPluginConstants.strAccessToken.length() > 0 &&
                SPLTLoginPluginConstants.strClientToken != null && SPLTLoginPluginConstants.strClientToken.length() > 0) {
            com.dotstudioz.dotstudioPRO.services.accesstoken.ClientTokenRefreshClass clientTokenRefreshClass = new com.dotstudioz.dotstudioPRO.services.accesstoken.ClientTokenRefreshClass(context);
            clientTokenRefreshClass.setClientTokenRefreshListener(new com.dotstudioz.dotstudioPRO.services.accesstoken.ClientTokenRefreshClass.IClientTokenRefresh() {
                @Override
                public void clientTokenResponse(String ACTUAL_RESPONSE) {
                    try {
                        String idToken = ACTUAL_RESPONSE;
                        SPLTLoginPluginConstants.strClientToken = idToken;

                        Log.d(TAG, "clientTokenResponse: SPLTLoginPluginConstants.strClientToken==>"+ SPLTLoginPluginConstants.strClientToken);

                        SharedPreferencesUtil.getInstance(context).addToSharedPreference(
                                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE,
                                SPLTLoginPluginConstants.strClientToken,
                                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void clientTokenError(String ERROR) {
                }
            });
            clientTokenRefreshClass.refreshExistingClientToken(SPLTLoginPluginConstants.strAccessToken, SPLTLoginPluginConstants.strClientToken);
        }
    }
}
