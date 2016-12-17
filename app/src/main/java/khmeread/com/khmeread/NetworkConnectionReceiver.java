package khmeread.com.khmeread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by chamnapchhorn on 12/17/16.
 */

public class NetworkConnectionReceiver extends BroadcastReceiver {
    public static final String NOTIFY_NETWORK_CHANGE = "NOTIFY_NETWORK_CHANGE";
    public static final String EXTRA_IS_CONNECTED = "EXTRA_IS_CONNECTED";

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        Intent localIntent = new Intent(NOTIFY_NETWORK_CHANGE);
        localIntent.putExtra(EXTRA_IS_CONNECTED, NetworkUtil.isConnected(context));
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }
}
