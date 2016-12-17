package khmeread.com.khmeread;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by chamnapchhorn on 12/17/16.
 */

public class KWebChromeClient extends WebChromeClient {
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        WebView.HitTestResult result = view.getHitTestResult();
        String data = result.getExtra();
        Context context = view.getContext();
        Uri uri = Uri.parse(data);

        Intent intent = new Intent(context, ExternalActivity.class);
        intent.putExtra(ExternalActivity.EXTERNAL_URL, uri.toString());
        context.startActivity(intent);
        return false;
    }
}
