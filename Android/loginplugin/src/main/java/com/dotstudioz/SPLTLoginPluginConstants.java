package com.dotstudioz;

public class SPLTLoginPluginConstants {
    private static final SPLTLoginPluginConstants ourInstance = new SPLTLoginPluginConstants();

    public static SPLTLoginPluginConstants getInstance() {
        return ourInstance;
    }

    private SPLTLoginPluginConstants() {
    }

    public static String strAccessToken;
    public static String strClientToken;

    public static boolean show_on_startup;

    public static String API_KEY_STRING = "apiKey";
    public static String AUTH0_CLIENT_ID_STRING = "auth0ClientId";

    /*public static String apiKey = "a12878949f4ea52703ab6a07c662b31895886cea";
    public static String auth0ClientId = "12u8cBNQt66Zq5ypkZBF5P1q33sXd7Iq"; //"fRI7uheX6IzdEKa4GXpQAAWBsIGX67oR"*/
    public static String apiKey;
    public static String auth0ClientId;
    public static String auth0Domain = "dotstudiopro.auth0.com";

    public static String backgroundColor;
    public static String headerColor;
    public static String titleColor;
}
