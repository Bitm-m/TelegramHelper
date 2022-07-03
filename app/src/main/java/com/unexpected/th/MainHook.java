package com.unexpected.th;

import android.view.WindowManager;

import com.unexpected.th.XC_MethodAfterReplacement;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
            if(lpparam.packageName.equals("org.telegram.messenger") || lpparam.packageName.equals("org.telegram.plus")) {
                    XposedHelpers.findAndHookMethod("android.view.Window", lpparam.classLoader, "setFlags", int.class, int.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                                Integer flags = (Integer) param.args[0];
                                flags &= ~ WindowManager.LayoutParams.FLAG_SECURE;
                                param.args[0] = flags;
                            }
                        });

                    XposedHelpers.findAndHookMethod("android.view.SurfaceView", lpparam.classLoader, "setSecure", boolean.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                                param.args[0] = false;
                            }
                        });

                    XposedHelpers.findAndHookMethod("org.telegram.ui.ChatActivity", lpparam.classLoader, "hasSelectedNoforwardsMessage", XC_MethodAfterReplacement.returnConstant(false));
                    XposedHelpers.findAndHookMethod("org.telegram.ui.Components.SharedMediaLayout", lpparam.classLoader, "hasNoforwardsMessage", XC_MethodAfterReplacement.returnConstant(false));
                    XposedBridge.hookAllMethods(XposedHelpers.findClass("org.telegram.messenger.MessagesController", lpparam.classLoader), "isChatNoForwards", XC_MethodAfterReplacement.returnConstant(false));
                    XposedHelpers.findAndHookMethod("org.telegram.messenger.UserConfig", lpparam.classLoader, "isPremium", XC_MethodAfterReplacement.returnConstant(true));
            }
    }
}
