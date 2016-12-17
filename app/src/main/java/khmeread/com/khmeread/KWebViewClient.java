package khmeread.com.khmeread;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by chamnapchhorn on 12/17/16.
 */

public class KWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        String APP_URL = view.getContext().getString(R.string.app_url);

        if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:") || url
                .startsWith("mms:") || url.startsWith("mmsto:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
            return true;
        }
        else if (url.startsWith("mailto:")) {
            MailTo mt = MailTo.parse(url);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { mt.getTo() });
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
            emailIntent.putExtra(Intent.EXTRA_CC, mt.getCc());
            emailIntent.putExtra(Intent.EXTRA_TEXT, mt.getBody());

            view.getContext().startActivity(Intent.createChooser(emailIntent, "Open with"));

            return true;
        }
        else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request.getUrl().toString());
    }
}
