////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package com.amazon.identity.auth.device;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import com.amazon.identity.auth.device.AuthError.ERROR_TYPE;
//import com.amazon.identity.auth.map.device.utils.MAPLog;
//
//public class ExternalBrowserManager {
//    private static final String LOG_TAG = ExternalBrowserManager.class.getName();
//    private static final String BROWSER_ID_SUFFIX = ".amazon.auth";
//
//    public ExternalBrowserManager() {
//    }
//
//    public void openUrl(AbstractRequest request, String url, Context context) throws AuthError {
//        CompatibilityUtil.assertCorrectManifestIntegration(context);
//        Intent intent = this.getIntent(url, context);
//        MAPLog.i(LOG_TAG, "Starting webView");
//
//        try {
//            request.onStart();
//            context.startActivity(intent);
//        } catch (Exception var6) {
//            MAPLog.e(LOG_TAG, "Unable to Launch webView: " + var6.getMessage());
//            throw new AuthError("Unable to Launch webView.", var6, ERROR_TYPE.ERROR_UNKNOWN);
//        }
//    }
//
//    private Intent getIntent(String url, Context context) {
//        Log.e(LOG_TAG, "getIntent");
//        Intent intent = new Intent();
//        intent.addFlags(268435456);
//        intent.addFlags(1073741824);
//        Log.e(LOG_TAG, "sqliu:"+context.getPackageName());
//        intent.setComponent(new ComponentName(context.getPackageName(), "com.willblaschko.android.alexa.AmazonAuthorizationActivity"));
//        intent.putExtra("URL", url);
//        intent.putExtra("com.android.browser.application_id", context.getPackageName() + ".amazon.auth");
//        return intent;
//    }
//}
