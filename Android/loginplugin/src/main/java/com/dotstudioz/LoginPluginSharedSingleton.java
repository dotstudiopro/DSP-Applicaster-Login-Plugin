package com.dotstudioz;

public class LoginPluginSharedSingleton {
    private static final LoginPluginSharedSingleton ourInstance = new LoginPluginSharedSingleton();

    public static LoginPluginSharedSingleton getInstance() {
        return ourInstance;
    }

    private LoginPluginSharedSingleton() {
    }

    public boolean requestForSubscription = false;

    public boolean executeHookFlagCalled = false;
    public boolean isRequestForSubscriptionFlagCalled = false;
    public String lastSubscriptionChannelId;
    public boolean lastSubscriptionResult = false;
}
