package com.theteamgo.fancywatch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
//import com.mobvoi.android.common.api.MobvoiApiClient.ConnectionCallbacks;
import com.mobvoi.android.common.api.MobvoiApiClient.OnConnectionFailedListener;
//import com.mobvoi.android.common.api.ResultCallback;
//import com.mobvoi.android.common.data.FreezableUtils;
//import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.wearable.DataApi;
//import com.mobvoi.android.wearable.DataApi.DataItemResult;
//import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
//import com.mobvoi.android.wearable.MessageApi.SendMessageResult;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
//import com.mobvoi.android.wearable.PutDataMapRequest;
//import com.mobvoi.android.wearable.PutDataRequest;
import com.mobvoi.android.wearable.Wearable;
import com.theteamgo.fancywatch.utils.VolleyUtil;

public class MainActivity extends AppCompatActivity implements DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener, MobvoiApiClient.ConnectionCallbacks,
        OnConnectionFailedListener{

    private static final String TAG = "MainActivity";
    private MobvoiApiClient mMobvoiApiClient;
    private boolean mResolvingError = false;
    private boolean mCameraSupported = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        VolleyUtil volleyUtil = new VolleyUtil(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //LOGD(TAG, "created");
        Log.d(TAG, "created");
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "started");
        if (!mResolvingError) {
            mMobvoiApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mMobvoiApiClient, this);
            Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
            Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
            mMobvoiApiClient.disconnect();
        }
        super.onStop();
    }


    // Ticwatch API
    @Override
    public void onConnected(Bundle bundle) {
        mResolvingError = false;
        Log.d(TAG, "connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived() A message from watch was received:" + messageEvent
                .getRequestId() + " " + messageEvent.getPath());
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConnected: " + node);
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected: " + node);
    }


    private static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }
}
