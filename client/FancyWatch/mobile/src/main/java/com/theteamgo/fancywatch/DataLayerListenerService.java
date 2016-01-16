package com.theteamgo.fancywatch;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.gesture.GestureType;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "ServiceMoblie";
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
        //    Intent startIntent = new Intent(this, TestMainActivity.class);
        //    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //    startActivity(startIntent);
        //}

        try {
            int type = Integer.valueOf(messageEvent.getPath());
            String s = "";
            if (type == GestureType.TYPE_TWICE_TURN_WRIST) {
                s = "turn wrist twice";
            } else if (type == GestureType.TYPE_TURN_WRIST_UP) {
                s = "turn wrist up";
            } else if (type == GestureType.TYPE_TURN_WRIST_DOWN) {
                s = "turn wrist down";
            } else {
                s = "unknown gesture";
            }
            Toast.makeText(getApplicationContext(), "onGestureDetected " + s, Toast.LENGTH_SHORT).show();
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
