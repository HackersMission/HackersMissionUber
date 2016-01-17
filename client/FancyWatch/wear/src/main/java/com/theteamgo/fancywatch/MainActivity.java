package com.theteamgo.fancywatch;

import static com.theteamgo.fancywatch.DataLayerListenerService.LOGD;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.MobvoiApiClient.ConnectionCallbacks;
import com.mobvoi.android.common.api.MobvoiApiClient.OnConnectionFailedListener;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.gesture.GestureType;
import com.mobvoi.android.gesture.MobvoiGestureClient;
import com.mobvoi.android.speech.SpeechRecognitionApi;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;
import java.io.UnsupportedEncodingException;
import com.theteamgo.fancywatch.common.Constant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import co.mobiwise.playerview.MusicPlayerView;

public class MainActivity extends SpeechRecognitionApi.SpeechRecogActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener{

    private static final String TAG = "MainActivity";

    private MobvoiApiClient mMobvoiApiClient;
    private MobvoiGestureClient mMobvoiGestureClient;
    private View mLayout;
    private TextView audioTitle;
    private Handler mHandler;
    MusicPlayerView mpv;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mHandler = new Handler();
        LOGD(TAG, "onCreate");
        setContentView(R.layout.main_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //mDataItemList = (ListView) findViewById(R.id.dataItem_list);
        //mIntroText = (TextView) findViewById(R.id.intro);
        mLayout = findViewById(R.id.layout);
        findViewById(R.id.speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecognition();
            }
        });

        audioTitle = (TextView)findViewById(R.id.title);

        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mpv = (MusicPlayerView) findViewById(R.id.mpv);

        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StartGestureMessageTask().execute(Constant.CONTROL_TYPE_TOGGLE);

                if (mpv.isRotating())
                    mpv.stop();
                else
                    mpv.start();
            }
        });

        ((MyApplication)getApplication()).setMainActivity(this);
        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                new StartGestureMessageTask().execute(Constant.CONTROL_TYEP_REQUEST_INFO);
            }
        };
        //mTimer.schedule(mTimerTask, 2000, 2000);
    }


    @Override
    public void onRecognitionSuccess(String text) {
        TextView txtRslt = (TextView) findViewById(R.id.title);
        txtRslt.setText(text);
        new StartWordMessageTask().execute(text);
    }

    @Override
    public void onRecognitionFailed() {
//        TextView txtRslt = (TextView) findViewById(R.id.title);
//        txtRslt.setText("onRecognitionFailed");
    }
    public void setAudioTitle(final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                audioTitle.setText(title);
            }
        });
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

    private void sendStartActivityMessage(String node) {
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, node, "test", new byte[0]).setResultCallback(
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

    private void sendGestureMessage(String node, int type) {
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, node, ""+type, new byte[0]).setResultCallback(
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

    private void sendWordMessage(String node, String wd) {
        try {
            Log.i("FUCK", wd);
            Wearable.MessageApi.sendMessage(
                    mMobvoiApiClient, node, ""+Constant.CONTROL_WORD_COMMAND, wd.getBytes("utf-8")).setResultCallback(
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    private class StartGestureMessageTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendGestureMessage(node, args[0]);
            }
            return null;
        }
    }

    private class StartWordMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            Collection<String> nodes = getNodes();
            Log.i("FUCK2", args[0]);
            Log.i("FUCK2", nodes.size()+"");


            for (String node : nodes) {
                Log.i("FUCK3", args[0]);
                sendWordMessage(node, args[0]);
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            new StartGestureMessageTask().execute(Constant.CONTROL_TYPE_NEXT);
                        } else if (type == GestureType.TYPE_TURN_WRIST_UP) {
                            s = "turn wrist up";
                        } else if (type == GestureType.TYPE_TURN_WRIST_DOWN) {
                            s = "turn wrist down";
                        } else {
                            s = "unknown gesture";
                        }
                        //new StartGestureMessageTask().execute(type);
                        Toast.makeText(getApplicationContext(), "onGestureDetected " + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        new StartGestureMessageTask().execute(Constant.CONTROL_TYEP_REQUEST_INFO);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mMobvoiApiClient, this);
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.DataApi.addListener(mMobvoiApiClient, this);
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LOGD(TAG, "onDataChanged(): " + dataEvents);
    }


    @Override
    public void onMessageReceived(MessageEvent event) {
        LOGD(TAG, "onMessageReceived: " + event);
        Toast.makeText(getApplicationContext(), "message", Toast.LENGTH_SHORT);
    }

    @Override
    public void onPeerConnected(Node node) {
        //generateEvent("Node Connected", node.getId());
    }

    @Override
    public void onPeerDisconnected(Node node) {
        //generateEvent("Node Disconnected", node.getId());
    }

    public boolean onLongPressSidePanel(MotionEvent e) {
        Log.d(TAG, "onLongPressSidePanel");
        return true;
    }

    public boolean onScrollSidePanel(MotionEvent e1, MotionEvent e2, float distanceX,
                                     float distanceY) {
        Log.d(TAG, "onScrollSidePanel " + distanceY);
        return true;
    }

    public boolean onFlingSidePanel(MotionEvent e1, MotionEvent e2, float velocityX,
                                    float velocityY) {
        Log.d(TAG, "onFlingSidePanel " + velocityY);
        return true;
    }

    public boolean onDoubleTapSidePanel(MotionEvent e) {
        Log.d(TAG, "onDoubleTapSidePanel");
        return true;
    }

    public boolean onSingleTapSidePanel(MotionEvent e) {
        Log.d(TAG, "onSingleTapSidePanel");
        //new StartGestureMessageTask().execute(CONTROL_TYPE_TOGGLE);
        return true;
    }



    public void test_send(View v) {
        new StartWearableActivityTask().execute();
    }

}
