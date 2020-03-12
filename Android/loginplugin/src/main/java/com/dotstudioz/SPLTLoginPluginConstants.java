package com.dotstudioz;

public class SPLTLoginPluginConstants {
    private static final SPLTLoginPluginConstants ourInstance = new SPLTLoginPluginConstants();

    public static SPLTLoginPluginConstants getInstance() {
        return ourInstance;
    }

    private SPLTLoginPluginConstants() {
    }

    //variable to store the company access token
    public static String strAccessToken;

    //variable to store the client token
    public static String strClientToken;

    //static variables
    public static String auth0Domain = "dotstudiopro.auth0.com";

    //readable parameters from the plugin manifest
    public static String backgroundColor;
    public static String BACKGROUND_COLOR_KEY = "backgroundColor";
    public static String headerColor;
    public static String HEADER_COLOR_KEY = "headerColor";
    public static String titleColor;
    public static String TITLE_COLOR_KEY = "titleColor";
    public static String logo;// = "https://images.dotstudiopro.com/5aafe37f99f815241c357891/1080/1920";
    public static String LOGO_KEY = "logo";
    public static boolean show_on_startup = true;
    public static String SHOW_ON_STARTUP_KEY = "show_on_startup";
    public static String title;// = "Dotstudioz";
    public static String TITLE_KEY = "title";

    /*public static String apiKey;
    public static String auth0ClientId;*/
    public static String apiKey = "a12878949f4ea52703ab6a07c662b31895886cea";
    public static String API_KEY_STRING = "apiKey";
    public static String auth0ClientId;// = "12u8cBNQt66Zq5ypkZBF5P1q33sXd7Iq"; //"fRI7uheX6IzdEKa4GXpQAAWBsIGX67oR"
    public static String AUTH0_CLIENT_ID_STRING = "auth0ClientId";
}
