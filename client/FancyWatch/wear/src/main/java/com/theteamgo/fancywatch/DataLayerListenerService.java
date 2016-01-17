package com.theteamgo.fancywatch;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;
import com.theteamgo.fancywatch.common.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "WearService";
    MobvoiApiClient mMobvoiApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mMobvoiApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LOGD(TAG, "onDataChanged: " + dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        LOGD(TAG, "onMessageReceived: " + messageEvent);

        // Check to see if the message is to start an activity
        //if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
        //    Intent startIntent = new Intent(this, MainActivity.class);
        //    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //    startActivity(startIntent);
        //}
        try {
            int type = Integer.valueOf(messageEvent.getPath());
            if (type == Constant.CONTROL_TYPE_INFO) {
                if(((MyApplication) getApplication()).getMainActivity() != null) {
                    String str = new String(messageEvent.getData(), "UTF-8");
                    JSONObject jobject = new JSONObject(str);
                    ((MyApplication) getApplication()).getMainActivity().setAudioStatus(jobject);
                    Log.d(TAG, "get audio info: " + str);
                }
            }
            //Toast.makeText(getApplicationContext(), "onGestureDetected " + s, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        LOGD(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        LOGD(TAG, "onPeerDisconnected: " + peer);
    }

    public static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }
}
