package com.willblaschko.android.alexa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AmazonAuthorizationActivity extends Activity {
    public static final String NOT_FOUND_PAGE = "file:///android_asset/LoadError.html";//网页加载出错时候的默认显示布局
    private static final String LOG_TAG = AmazonAuthorizationActivity.class.getName();
    private static final String DEFAULT_ULR = "https://na.account.amazon.com/ap/oa?response_type=code&redirect_uri=amzn%3A%2F%2Fcom.amazon.identity.auth.device.lwaapp&client_id=amzn1.application-oa2-client.7abbb7a0717346eca06c2081c7ce92e2&amzn_respectRmrMeAuthState=1&amzn_showRmrMe=1&amzn_rmrMeDefaultSelected=1&state=clientId%3Damzn1.application-oa2-client.7abbb7a0717346eca06c2081c7ce92e2%26redirectUri%3Damzn%3A%2F%2Fcom.amazon.identity.auth.device.lwaapp%26clientRequestId%3D0b1fb0ae-1ef1-4051-9402-71fe4e1fe4d4%26InteractiveRequestType%3Dcom.amazon.identity.auth.device.authorization.request.authorize%26com.amazon.identity.auth.device.authorization.return_auth_code%3Dfalse&scope=profile+postal_code&appIdentifier=eyJwYWNrYWdlIjoiY29tLmFtYXpvbi5pZGVudGl0eS5hdXRoLmRldmljZS5sd2FhcHAiLCJNRDUi%0AOlsiNjg4MmQzYzk1MzRiNjVjY2ExOTUxNDJmMmI3NTFkODIiXSwiU0hBLTI1NiI6WyI0NjRkZGZm%0ANzY1ZDkwYTI3YWE3YjBlODZlOWI5NTIyOWJjMzdmZDEwZjg2NmMyOTYxNTUyYmIyYTU0YWU1Yjg5%0AIl19%0A&sw_ver=LWAAndroidSDK3.0.0&code_challenge_method=S256&code_challenge=XCFhdn-qLS2zEjja_ZfQ-wI361bGMfQlxEtNdSTSOio&language=zh_CN";
    private static final String AMAZON_ACCOUNT = "";//亚马逊开发者账号
    private static final String AMAZON_PSW = "";//亚马逊开发者密码
    private static String URL = "NOT_FOUND_PAGE";
    private WebView webView;
    private ProgressBar progressBar;
    private ProgressBar pb_auth;
    private TextView tv_auth;
    private Button btn_manual_authorization;
    private RelativeLayout rl_auto_auth;
    private Handler mHandler;
    private boolean manualAuthorize = false;//手动授权

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_showweb);

        mHandler = new Handler(getMainLooper());
        clearCookies(this);

        initWebView();

        progressBar = (ProgressBar) findViewById(R.id.pb);
        progressBar.setVisibility(View.VISIBLE);
        pb_auth = (ProgressBar) findViewById(R.id.pb_auth);
        tv_auth = (TextView) findViewById(R.id.tv_auth);
        rl_auto_auth = (RelativeLayout) findViewById(R.id.rl_auto_auth);
        btn_manual_authorization = (Button) findViewById(R.id.btn_manual_authorization);
        btn_manual_authorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualAuthorize = true;
                mHandler.removeCallbacksAndMessages(null);
                clearCookies(AmazonAuthorizationActivity.this);
                initWebView();
                rl_auto_auth.setVisibility(View.GONE);
            }
        });
    }

    public static void clearCookies(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookies(null);
        CookieManager.getInstance().flush();
        WebStorage.getInstance().deleteAllData();
    }

    protected void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webView.setBackgroundColor(0);

        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        // 缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);
        //设置可以支持缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(false);

        settings.setDomStorageEnabled(true);

        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//允许js弹出窗口
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受所有网站的证书
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //这里获取到是亚马逊的schema就跳转,此时已经授权成功
                try {
                    if (url.startsWith("amzn://")) {
                        authSuccess(url);
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
                webView.loadUrl(url);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(LOG_TAG, "onReceivedError:" + error.toString());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //解决重定向导致的onPageFinished多次调用的问题
                if (webView.getProgress() != 100) {
                    return;
                }
                Log.d(LOG_TAG, "onPageFinished\n" + "url=" + url);

                //默认填表
                final String strAccount = String.format("javascript:document.getElementById('ap_email').value='%s';", AMAZON_ACCOUNT);
                final String strPsw = String.format("javascript:document.getElementById('ap_password').value='%s';", AMAZON_PSW);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.evaluateJavascript(strAccount, null);
                        webView.evaluateJavascript(strPsw, null);
                        if (manualAuthorize) {
                            Log.d(LOG_TAG, "manual Authorize");
                            return;
                        }
                        //模拟点击提交按钮
                        final String submit = "javascript:document.getElementById('signInSubmit').click();";
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(LOG_TAG, "submit");
                                webView.evaluateJavascript(submit, null);
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(LOG_TAG, "第一次进入 确认授权应用");
                                        final String firstCommit = "javascript:document.getElementsByName('consentApproved')[0].click();";
                                        webView.evaluateJavascript(firstCommit, null);
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d(LOG_TAG, "国家选择");
                                                final String nextButton = "javascript:document.getElementsByName('nextButton')[0].click();";
                                                webView.evaluateJavascript(nextButton, null);

                                            }
                                        }, 1000);
                                    }
                                }, 1000);
                            }
                        }, 1000);
                    }
                }, 500);


            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setProgress(newProgress);
                }
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title.contains("404") || title.contains("500") || title.contains("Error") || title.contains("找不到网页")) {
                    view.stopLoading();
                    view.loadUrl(NOT_FOUND_PAGE);
                    authErr();
                }

            }
        });

        Intent in = getIntent();
        if (in == null) {
            webView.loadUrl(DEFAULT_ULR);
        } else {
            URL = in.getExtras().getString("URL", DEFAULT_ULR);
            webView.loadUrl(URL);
        }
    }

    //授权成功
    private void authSuccess(final String url) {
        Log.d(LOG_TAG, "authSuccess:" + url);
        pb_auth.setVisibility(View.INVISIBLE);
        tv_auth.setText("Authorization success..");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                authEnd();
            }
        }, 1500);

    }

    //授权失败
    private void authErr() {
        Log.d(LOG_TAG, "authErr");
        pb_auth.setVisibility(View.INVISIBLE);
        tv_auth.setText("Authorization failed, please try again.. ");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                authEnd();
            }
        }, 1500);

    }

    //授权结束
    private void authEnd() {
        Log.d(LOG_TAG, "authEnd");
        webView.destroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
