package com.theteamgo.fancywatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.MobvoiApiClient.ConnectionCallbacks;
import com.mobvoi.android.common.api.MobvoiApiClient.OnConnectionFailedListener;
//import com.mobvoi.android.common.data.FreezableUtils;
//import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.gesture.GestureType;
import com.mobvoi.android.gesture.MobvoiGestureClient;
import com.mobvoi.android.wearable.DataApi;
//import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
//import com.mobvoi.android.wearable.DataMapItem;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

public class MainActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener{

    public static final String START_ACTIVITY_PATH = "/start/MainActivity";
    private TextView mTextView;
    private Handler mHandler;
    private String mNode;
    private static final String TAG = "WearMainActivity";
    private MobvoiApiClient mMobvoiApiClient;
    private MobvoiGestureClient mMobvoiGestureClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        mHandler = new Handler();

        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    private void sendGestureMessage(int type) {

        Collection<String> nodes = getNodes();
        for (String node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mMobvoiApiClient, node, type+"", new byte[0]).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mMobvoiApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "on resume");
        super.onResume();
        Wearable.DataApi.addListener(mMobvoiApiClient, this);
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
        mMobvoiApiClient.connect();
        mMobvoiGestureClient = MobvoiGestureClient.getInstance(GestureType.GROUP_TURN_WRIST);
        mMobvoiGestureClient.register(MainActivity.this, new MobvoiGestureClient.IGestureDetectedCallback() {
            @Override
            public void onGestureDetected(final int type) {
                Log.d("callback", "onGestureDetected type: " + type);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
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
                        sendGestureMessage(type);
                        Toast.makeText(getApplicationContext(), "onGestureDetected " + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mMobvoiApiClient, this);
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
        mMobvoiGestureClient.unregister(this);
    }

    // ticwatch API

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "connected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + connectionResult);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);
    }

    @Override
    public void onPeerConnected(Node node) {
        mNode = node.getId();
        Log.d(TAG, "Node Connected" + node.getId());
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "Node Disconnected" + node.getId());
    }

    private static void LOGD(final String tag, String message) {
        //if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        //}
    }
}
