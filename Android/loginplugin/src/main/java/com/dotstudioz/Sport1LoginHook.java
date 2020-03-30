package com.dotstudioz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.applicaster.hook_screen.HookScreen;
import com.applicaster.hook_screen.HookScreenListener;

import java.util.HashMap;
import java.util.Map;

/*import static com.applicaster.sport1loginhook.Sport1LoginHookActivity.ACTIVITY_HOOK_COMPLETED;
import static com.applicaster.sport1loginhook.Sport1LoginHookActivity.ACTIVITY_HOOK_RESULT_CODE;*/

public class Sport1LoginHook extends AppCompatActivity implements HookScreen {
    HookScreenListener hookListener;
    HashMap<String, String> hookScreen = new HashMap<>();
    public Sport1LoginHook() { }
    @Override
    public boolean isFlowBlocker() {
        return true;
    }
    @Override
    public boolean shouldPresent() {
        return true;
    }
    @Override
    public boolean isRecurringHook() {
        return false;
    }
    @Override
    public void hookDismissed() { }
    @Override
    public void executeHook(final Context context, HookScreenListener hookListener, Map<String, ?> hookProps) {
        this.hookListener = hookListener;
        /*Intent intent = new Intent(context, Sport1LoginHookActivity.class);
        ((Activity) context).startActivityForResult(intent, ACTIVITY_HOOK_RESULT_CODE);*/
    }
    @Override
    public HookScreenListener getListener() {
        return hookListener;
    }
    @Override
    public HashMap<String, String> getHook() {
        return hookScreen;
    }
    @Override
    public void setHook(HashMap<String, String> hookScreen) {
        this.hookScreen = hookScreen;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == ACTIVITY_HOOK_RESULT_CODE) {
            if (resultCode == ACTIVITY_HOOK_COMPLETED) {
                hookListener.hookCompleted(null);
            }else {
                hookListener.hookFailed(null);
            }
        }*/
    }
}