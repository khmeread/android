package khmeread.com.khmeread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AdvancedWebView mWebView;
    private ProgressBar mProgressBar;
    private String mFailedUrl = "";
    private NetworkConnectionReceiver mReceiver = new NetworkConnectionReceiver();
    private boolean mFirstLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // views
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // webview
        KWebChromeClient webChromeClient = new KWebChromeClient();
        KWebViewClient webViewClient = new KWebViewClient();

        mWebView.setListener(this, this);
        mWebView.setMixedContentAllowed(true);
        mWebView.addPermittedHostname(getResources().getString(R.string.app_host));
        mWebView.setCookiesEnabled(true);
        mWebView.setThirdPartyCookiesEnabled(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setUserAgentString(webSettings.getUserAgentString() + "; KhmereadAndroid");
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        // connectivity
        if (!NetworkUtil.isConnected(this)) {
            mFailedUrl = getAppUrl();
            showNetworkError();
        } else {

            // load
            Intent intent = getIntent();
            if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                String url = intent.getDataString();
                Log.d(TAG, "INTENT_OPEN_URL: " + url);
                mWebView.loadUrl(url);
            } else {
                mWebView.loadUrl(getAppUrl());
            }
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Log.d(TAG, url + " :onPageStarted ....");

        showProgressBar();
    }

    @Override
    public void onPageFinished(String url) {
        Log.d(TAG, url + " :onPageFinished ....");

        hideProgressBar();
        mFirstLoading = false;
        mFailedUrl = "";
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Log.d(TAG, failingUrl + " :onPageError .... " + errorCode);

        mFailedUrl = failingUrl;
        hideProgressBar();
        showNetworkError();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {
        Intent intent = new Intent(this, ExternalActivity.class);
        intent.putExtra(ExternalActivity.EXTERNAL_URL, url);
        this.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();

        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filter);

        IntentFilter customFilter = new IntentFilter(NetworkConnectionReceiver.NOTIFY_NETWORK_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, customFilter);
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();

        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) { return; }
        super.onBackPressed();
    }

    private String getAppUrl() {
        return getResources().getString(R.string.app_url);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void showNetworkError() {
        NetworkConnectionDialogFragment mDialog = new NetworkConnectionDialogFragment();
        mDialog.show(getFragmentManager(), "error_dialog");
    }

    private BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(NetworkConnectionReceiver.EXTRA_IS_CONNECTED, false);
            Log.d(TAG, "==============================" + isConnected + ",=========" + mFailedUrl);
            if (isConnected) {
                if (mFailedUrl != "") {
                    mWebView.loadUrl(mFailedUrl);
                }
            }
        }
    };
}
