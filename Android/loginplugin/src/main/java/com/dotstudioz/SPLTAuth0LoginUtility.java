package com.dotstudioz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.auth0.android.lock.internal.configuration.Theme;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import com.dotstudioz.dotstudioPRO.services.constants.ApplicationConstantURL;
import com.dotstudioz.dotstudioPRO.services.constants.ApplicationConstants;
import com.dotstudioz.dotstudioPRO.services.services.CompanyTokenService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
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

        setClientTokenFromSharedPreference(context);
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

    /**
     * extract company key from the access token
     * @param context
     * @return Company Key as a String
     */
    public String getCompanyKeyFromAccessToken(Context context) {
        String accessToken;
        //Check if accessToken is present, if not then fetch it
        try {
            accessToken = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);
            if(accessToken != null && accessToken.length() > 0) {
                SPLTLoginPluginConstants.getInstance().strAccessToken = accessToken;
                try {
                    Base64 decoder = new Base64(true);
                    byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.getInstance().strAccessToken.split("\\.")[1]);
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

    /**
     * extract the company name from the access token
     * @param context
     * @return Comapany Name as a string
     */
    private String getCompanyNameFromAccessToken(Context context) {
        String accessToken;
        //Check if accessToken is present, if not then fetch it
        try {
            accessToken = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);
            if(accessToken != null && accessToken.length() > 0) {
                SPLTLoginPluginConstants.getInstance().strAccessToken = accessToken;
                try {
                    Base64 decoder = new Base64(true);
                    byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.getInstance().strAccessToken.split("\\.")[1]);
                    String s = new String(secret);
                    JSONObject jsonObject = new JSONObject(s);

                    if (jsonObject.has("iss")) {
                        if(jsonObject.getJSONObject("iss").has("context")) {
                            if(jsonObject.getJSONObject("iss").getJSONObject("context").has("name")) {
                                String companyName = jsonObject.getJSONObject("iss").getJSONObject("context").getString("name");
                                if (companyName != null && companyName.length() > 0) {
                                    return companyName;
                                } else {
                                    return "";
                                }
                            }
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

    /**
     * extract the data from the access token and confirm if it is within the expiry date
     * @param context
     * @return true/false based on the expiry date
     */
    private boolean isAccessTokenValid(Context context) {
        String accessToken;
        //Check if accessToken is present, if not then fetch it
        try {
            accessToken = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);
            if(accessToken != null && accessToken.length() > 0) {
                SPLTLoginPluginConstants.getInstance().strAccessToken = accessToken;
                try {
                    Base64 decoder = new Base64(true);
                    byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.getInstance().strAccessToken.split("\\.")[1]);
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

    public interface ILoginPlugin {
        public void loginResponse(boolean result, String token);
    }
    public void login(Context context) {
        showLoginController(context);
    }
    public ILoginPlugin iLoginPlugin;
    public void login(Context context, ILoginPlugin iLP) {
        if(iLP != null)
            iLoginPlugin = iLP;
            showLoginController(context);
    }
    private void showLoginController(Context context) {
        Log.d(TAG, "showLoginController: CALLED");
        mContext = context;

        if(SPLTLoginPluginConstants.getInstance().apiKey != null && SPLTLoginPluginConstants.getInstance().apiKey.length() > 0 &&
                SPLTLoginPluginConstants.getInstance().auth0ClientId != null && SPLTLoginPluginConstants.getInstance().auth0ClientId.length() > 0) {
            Map<String, Object> parametersMap = ParameterBuilder
                    .newAuthenticationBuilder()
                    .setScope(ParameterBuilder.SCOPE_OFFLINE_ACCESS)
                    .set("c", getCompanyKeyFromAccessToken(context))
                    .asDictionary();

            validateLogoURLAndShowLogin(SPLTLoginPluginConstants.getInstance().logo);
        } else {
            return;
        }
    }

    /**
     * check if the logo url passed as a parameter is valid and if so then download the image
     * once the download is complete call startLockActivity or if the logoURL is invalid then
     * call the startLockActivity without downloading the image
     * @param logoURL
     */
    private void validateLogoURLAndShowLogin(String logoURL) {
        //validate if a logo was assigned or else load the login screen without downloading the logo
        if(logoURL != null && logoURL.length() > 0) {
            new DownloadImageTask().execute(logoURL);
        } else {
            startLockActivity(mContext);
        }
    }
    public Bitmap sharedBitmap;

    /**
     * Download the image using a URL and convert it to bitmap
     * also on download complete call startLockActivity
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        public DownloadImageTask() {

        }

        protected Bitmap doInBackground(String... urls) {
            Log.d(TAG, "doInBackground: CALLED");
            if(urls != null && urls.length > 0) {
                String url = urls[0];
                Bitmap bmp = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    bmp = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return bmp;
            } else {
                return null;
            }
        }
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                sharedBitmap = result;
            }

            startLockActivity(mContext);
        }
    }

    /**
     * Helper method to validate string for data and return either the
     * actual value or the default value in case of null string
     * @param value - the actual value of the string
     * @param defaultValue - default value in case of null value
     * @return
     */
    private String validateValueOrSetDefault(String value, String defaultValue) {
        String returnValue = defaultValue;
        if(value != null && value.length() > 0) {
            returnValue = value;
        }
        return returnValue;
    }
    public void startLockActivity(Context context) {

        Drawable d = null;
        //check if a logo is present or else use the default logo
        if(sharedBitmap != null) {
            d = new BitmapDrawable(context.getResources(), sharedBitmap);
        } else {
            d = context.getResources().getDrawable(R.drawable.dotstudiopro_logo_black);
        }

        String title = validateValueOrSetDefault(SPLTLoginPluginConstants.getInstance().title, SPLTLoginPluginConstants.getInstance().defaultTitle);
        String titleColor = validateValueOrSetDefault(SPLTLoginPluginConstants.getInstance().titleColor, SPLTLoginPluginConstants.getInstance().defaultTitleColor);;
        String headerColor = validateValueOrSetDefault(SPLTLoginPluginConstants.getInstance().headerColor, SPLTLoginPluginConstants.getInstance().defaultHeaderColor);
        String backgroundColor = validateValueOrSetDefault(SPLTLoginPluginConstants.getInstance().headerColor, SPLTLoginPluginConstants.getInstance().defaultHeaderColor);

        int initialScreenToUse = 0;

        Log.d(TAG, "startLockActivity:line number 331 ");
        Auth0 auth0 = new Auth0(SPLTLoginPluginConstants.getInstance().auth0ClientId, SPLTLoginPluginConstants.getInstance().auth0Domain);
        Map<String, Object> parametersMap = ParameterBuilder
                .newAuthenticationBuilder()
                .setScope(ParameterBuilder.SCOPE_OFFLINE_ACCESS)
                .set("c", getCompanyKeyFromAccessToken(context))
                .asDictionary();
        Log.d(TAG, "startLockActivity: line number 338");
        mLock = Lock.newBuilder(auth0, mCallback).withAuthenticationParameters(parametersMap)
                .withTheme(getCustomizedLockTheme(context, d, title, titleColor, headerColor, backgroundColor))
                //Add parameters to the build
                .withScheme("demo")
                .initialScreen(initialScreenToUse)
                .closable(true)
                //.build();
                .build(context);
        Log.d(TAG, "startLockActivity: line number 347");

        context.startActivity(mLock.newIntent(context));
    }
    private Theme getCustomizedLockTheme(Context context, Drawable d, String title, String titleColor, String headerColor, String backgroundColor) {
        Theme customizedLockTheme = Theme.newBuilder()
                .withHeaderLogoDrawable(d)//logo
                .withHeaderTitleString(title)//Header title text
                .withHeaderTitleColorColor(Color.parseColor(titleColor))//Header title text color
                .withHeaderColorColor(Color.parseColor(headerColor))//Header and logo background color
                .withDarkPrimaryColorColor(Color.parseColor(backgroundColor))//LOG IN button color
                .withPrimaryColorColor(Color.parseColor(backgroundColor))//LOG IN button pressed color
                .buildWithActualValues();
        return customizedLockTheme;
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
            sendLoginResponse(false);
        }

        @Override
        public void onError(LockException error){
            Toast.makeText(mContext, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
            sendLoginResponse(false);
        }
    };
    public void sendLoginResponse(boolean flag) {
        if(iLoginPlugin != null) {
            if(!flag) {
                iLoginPlugin.loginResponse(false, null);
            } else {
                if(SPLTLoginPluginConstants.getInstance().strClientToken != null && SPLTLoginPluginConstants.getInstance().strClientToken.length() > 0)
                    iLoginPlugin.loginResponse(true, SPLTLoginPluginConstants.getInstance().strClientToken);
                else
                    iLoginPlugin.loginResponse(false, null);
            }
        }
    }
    private UserProfile mUserProfile;
    private Auth0 mAuth0;
    private String mUserEmailId;
    private String mUserIdString;
    private String mUserFirstName;
    private String mUserLastName;
    private String mUserAvatarPath;

    /**
     * using the AuthenticationAPIClient to get the client token received after the user had logged in
     */
    private void getUserProfileFromAuth0() {
        mAuth0 = new Auth0(SPLTLoginPluginConstants.getInstance().auth0ClientId, SPLTLoginPluginConstants.getInstance().auth0Domain);
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

    /**
     * setting the client token if passed from external screens / JavaScript / React to set the token.
     * @param context
     * @param clientToken
     */
    public void setClientTokenFromExternalInterface(Context context, String clientToken) {
        if(context != null) {
            mContext = context;
            setClientTokenAndUserDetails(clientToken);
        }
    }
    /**
     * extract the user details from the client token
     * @param clientToken
     */
    private void setClientTokenAndUserDetails(String clientToken) {
        SPLTLoginPluginConstants.getInstance().strClientToken = clientToken;
        Log.d(TAG, "scrapeOutInformationFromClientToken: SPLTLoginPluginConstants.getInstance().strClientToken==>"+SPLTLoginPluginConstants.getInstance().strClientToken);

        Base64 decoder = new Base64(true);
        byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.getInstance().strClientToken.split("\\.")[1]);
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
                SPLTLoginPluginConstants.getInstance().strClientToken,
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);

        if(iLoginPlugin != null)
            sendLoginResponse(true);
    }

    /**
     * validate the time SPLTLoginPluginConstants.getInstance().strClientToken's expire variable
     * return false if client token is expired
     * return true if the client token is still valid
     * @return
     */
    public boolean isClientTokenValid() {
        if(SPLTLoginPluginConstants.getInstance().strClientToken != null && SPLTLoginPluginConstants.getInstance().strClientToken.length() > 0) {
            try {
                Base64 decoder = new Base64(true);
                byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.getInstance().strClientToken.split("\\.")[1]);
                String s = new String(secret);
                JSONObject jsonObject = new JSONObject(s);
                Log.d(TAG, "isClientTokenValid: CALLED");

                if (jsonObject.has("expires")) {
                    Date dt = new Date(jsonObject.getLong("expires"));
                    Log.d(TAG, "isClientTokenValid: Expiry Date From token==>" + dt.toString());
                    Date currentDate = new Date();
                    Log.d(TAG, "isClientTokenValid: Current Date==>" + currentDate.toString());

                    if (dt.compareTo(currentDate) > 0) {
                        Log.d(TAG, "isClientTokenValid: token is not expired");
                        return true;
                    } else {
                        Log.d(TAG, "isClientTokenValid: token is expired");
                        return false;
                    }
                } else {
                    Log.d(TAG, "isClientTokenValid: token is expired");
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean isUserLoggedIn(Context context) {
        Log.d(TAG, "isUserLoggedIn: CALLED CALLED");
        return !checkIfUserAlreadyAuthenticated(context);
    }

    /**
     * check the shared preference if the client token is already present and valid
     * @param context
     * @return
     */
    private boolean checkIfUserAlreadyAuthenticated(Context context) {
        String sp = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);

        if(sp == null || sp.length() == 0) {
            SPLTLoginPluginConstants.getInstance().strClientToken = "";
            Log.d(TAG, "checkIfUserAlreadyAuthenticated: FALSE==>"+SPLTLoginPluginConstants.getInstance().strClientToken);
            return false;
        } else {
            setClientTokenFromSharedPreference(context);
            return true;
        }
    }

    /**
     * read client token from shared preference and save it in SPLTLoginPluginConstants.getInstance().strClientToken
     * @param context
     */
    private void setClientTokenFromSharedPreference(Context context) {
        String sp = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);

        if(sp == null || sp.length() == 0) {
            SPLTLoginPluginConstants.getInstance().strClientToken = "";
        } else {
            SPLTLoginPluginConstants.getInstance().strClientToken = sp;
            Log.d(TAG, "checkIfUserAlreadyAuthenticated: TRUE==>"+SPLTLoginPluginConstants.getInstance().strClientToken);
        }

        if(SPLTLoginPluginConstants.getInstance().strClientToken != null && SPLTLoginPluginConstants.getInstance().strClientToken.length() > 0) {
            try {
                Base64 decoder = new Base64(true);
                byte[] secret = decoder.decodeBase64(SPLTLoginPluginConstants.getInstance().strClientToken.split("\\.")[1]);
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

    /**
     * logout method to clear the shared preference &
     * assign SPLTLoginPluginConstants.getInstance().strClientToken
     * @param context
     */
    public void logout(Context context) {
        Log.d(TAG, "logout: CALLED");
        SPLTLoginPluginConstants.getInstance().strClientToken = "";

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


    private boolean requestFromRefreshClientTokenFlag = false;
    /**
     * get access token, based on the api key received from the plugin
     * and save the access token in SPLTLoginPluginConstants.getInstance().strAccessToken
     * @param context
     */
    private void requestAccessToken(final Context context) {
        CompanyTokenService companyTokenService = new CompanyTokenService(context);
        companyTokenService.setCompanyTokenServiceListener(new CompanyTokenService.ICompanyTokenService() {
            @Override
            public void companyTokenServiceResponse(JSONObject responseBody) {
                try {
                    if(responseBody != null && responseBody.has("token")) {
                        SPLTLoginPluginConstants.getInstance().strAccessToken = responseBody.getString("token");

                        SharedPreferencesUtil.getInstance(context).addToSharedPreference(
                                ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                                SPLTLoginPluginConstants.getInstance().strAccessToken,
                                ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);

                        Log.d(TAG, "companyTokenServiceResponse: SPLTLoginPluginConstants.getInstance().strAccessToken==>"+SPLTLoginPluginConstants.getInstance().strAccessToken);
                        if(requestFromRefreshClientTokenFlag) {
                            requestFromRefreshClientTokenFlag = false;
                            // call back the refreshClientToken now
                            refreshClientToken(context);
                        }
                    } else {
                        SPLTLoginPluginConstants.getInstance().strAccessToken = "";
                    }
                } catch(Exception e) {
                    SPLTLoginPluginConstants.getInstance().strAccessToken = "";
                    e.printStackTrace();
                }
            }

            @Override
            public void companyTokenServiceError(String responseBody) {
                SPLTLoginPluginConstants.getInstance().strAccessToken = "";
            }
        });
        companyTokenService.requestForToken(SPLTLoginPluginConstants.apiKey, ApplicationConstantURL.getInstance().TOKEN_URL);
    }

    /**
     * refresh the existing client token
     * and save the client token in SPLTLoginPluginConstants.getInstance().strClientToken
     * @param context
     */
    private void refreshClientToken(final Context context) {
        String accessToken = SharedPreferencesUtil.getInstance(context).getSharedPreference(
                ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);
        ApplicationConstants.xAccessToken = accessToken;
        ApplicationConstants.CLIENT_TOKEN = SPLTLoginPluginConstants.getInstance().strClientToken;
        if(accessToken != null && accessToken.length() > 0) {
            SPLTLoginPluginConstants.getInstance().strAccessToken = accessToken;
            Log.d(TAG, "refreshClientToken: From SharedPreference SPLTLoginPluginConstants.getInstance().strAccessToken==>"+SPLTLoginPluginConstants.getInstance().strAccessToken);
        }

        if(SPLTLoginPluginConstants.getInstance().strAccessToken == null || SPLTLoginPluginConstants.getInstance().strAccessToken.length() == 0) {
            requestFromRefreshClientTokenFlag = true;
            requestAccessToken(context);
            return;
        }

        if(SPLTLoginPluginConstants.getInstance().strAccessToken != null && SPLTLoginPluginConstants.getInstance().strAccessToken.length() > 0 &&
                SPLTLoginPluginConstants.getInstance().strClientToken != null && SPLTLoginPluginConstants.getInstance().strClientToken.length() > 0) {
            com.dotstudioz.dotstudioPRO.services.accesstoken.ClientTokenRefreshClass clientTokenRefreshClass = new com.dotstudioz.dotstudioPRO.services.accesstoken.ClientTokenRefreshClass(context);
            clientTokenRefreshClass.setClientTokenRefreshListener(new com.dotstudioz.dotstudioPRO.services.accesstoken.ClientTokenRefreshClass.IClientTokenRefresh() {
                @Override
                public void clientTokenResponse(String ACTUAL_RESPONSE) {
                    try {
                        String idToken = ACTUAL_RESPONSE;
                        SPLTLoginPluginConstants.getInstance().strClientToken = idToken;

                        Log.d(TAG, "clientTokenResponse: SPLTLoginPluginConstants.getInstance().strClientToken==>"+SPLTLoginPluginConstants.getInstance().strClientToken);

                        SharedPreferencesUtil.getInstance(context).addToSharedPreference(
                                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE,
                                SPLTLoginPluginConstants.getInstance().strClientToken,
                                ApplicationConstants.USER_DETAILS_RESPONSE_SHARED_PREFERENCE_KEY);

                        if(refreshClientTokenInterfaceFlag)
                            refreshClientTokenInterfaceResult(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void clientTokenError(String ERROR) {
                    if(refreshClientTokenInterfaceFlag)
                        refreshClientTokenInterfaceResult(false);
                }
            });
            refreshClientTokenInterfaceFlag = true;
            clientTokenRefreshClass.refreshExistingClientToken(SPLTLoginPluginConstants.getInstance().strAccessToken, SPLTLoginPluginConstants.getInstance().strClientToken);
        }
    }

    /**
     * returns the stored client token if present in the shared preference
     * @param context
     * @return
     */
    public String getClientToken(Context context) {
        if(checkIfUserAlreadyAuthenticated(context))
            return SPLTLoginPluginConstants.getInstance().strClientToken;
        else
            return null;
    }

    public interface IRefreshClientToken {
        void clientTokenRefreshSuccess(String clientToken);
        void clientTokenRefreshFailed(String error);
    }
    private IRefreshClientToken iRefreshClientToken;
    private boolean refreshClientTokenInterfaceFlag = false;
    public void setRefreshClientTokenInterface(IRefreshClientToken irct) {
        if(irct instanceof IRefreshClientToken) {
            this.iRefreshClientToken = (IRefreshClientToken) irct;
        } else {
            this.iRefreshClientToken = null;
        }
    }
    public void refreshClientTokenInterface(Context context) {
        if(this.iRefreshClientToken != null) {
            refreshClientToken(context);
        }
    }
    private void refreshClientTokenInterfaceResult(boolean flag) {
        if(this.iRefreshClientToken != null) {
            if(flag)
                this.iRefreshClientToken.clientTokenRefreshSuccess(ApplicationConstants.CLIENT_TOKEN);
            else
                this.iRefreshClientToken.clientTokenRefreshFailed("Failed refreshing the client token, need to relogin");
        }
    }
}
