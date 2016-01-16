package com.theteamgo.fancywatch;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
//import com.mobvoi.android.common.api.MobvoiApiClient.ConnectionCallbacks;
import com.mobvoi.android.common.api.MobvoiApiClient.OnConnectionFailedListener;
//import com.mobvoi.android.common.api.ResultCallback;
//import com.mobvoi.android.common.data.FreezableUtils;
//import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.gesture.GestureType;
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
import com.theteamgo.fancywatch.utils.CustomRequest;
import com.theteamgo.fancywatch.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.mobiwise.playerview.MusicPlayerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener, MobvoiApiClient.ConnectionCallbacks,
        OnConnectionFailedListener{

    private static final String TAG = "MainActivity";
    public static final int CONTROL_TYPE_TOGGLE = 7001;
    public static final int CONTROL_TYEP_VOLUME_UP = 7002;
    public static final int CONTROL_TYEP_VOLUME_DOWN = 7003;

    private MobvoiApiClient mMobvoiApiClient;
    private boolean mResolvingError = false;
    private Context context;
    public MediaPlayer mediaPlayer;


    private MusicPlayerView mpv;

    private List<Song> songList = new ArrayList<>();
    private int playIndex = 0;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mpv = (MusicPlayerView) findViewById(R.id.mpv);
        setSupportActionBar(toolbar);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        context = this;

        VolleyUtil volleyUtil = new VolleyUtil(this);
        ((MyApplication)getApplication()).setMainActivity(this);

        context = this;

        mHandler = new Handler();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        GetPlayList();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


    }

    private void playAll() {
        if (playIndex >= songList.size()) {
            return;
        }

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(songList.get(playIndex).mediaUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mpv.setCoverURL(songList.get(playIndex).mediaImageUrl);
        mpv.setMax(songList.get(playIndex).mediaLength);
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    mediaPlayer.pause();
                } else {
                    mpv.start();
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.start();
        mpv.start();
        playIndex++;

        /* 当MediaPlayer.OnCompletionLister会运行的Listener */
        mediaPlayer.setOnCompletionListener(
        new MediaPlayer.OnCompletionListener()
        {
            // @Override
            public void onCompletion(MediaPlayer arg0)
            {
                try
                {
                    if (playIndex >= songList.size()) {
                        Log.i("playlist", "end");
                        return;
                    }

                    try {

                        mediaPlayer.setDataSource(songList.get(playIndex).mediaUrl);
                        mpv.setCoverURL(songList.get(playIndex).mediaImageUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    playIndex++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void playOneSong(Song song) {

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(song.mediaUrl);
            mediaPlayer.prepare();//prepare之后自动播放
        } catch (IOException e) {
            e.printStackTrace();
        }

        mpv = (MusicPlayerView) findViewById(R.id.mpv);
        mpv.setCoverURL(song.mediaImageUrl);
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    mediaPlayer.pause();
                } else {
                    mpv.start();
//                    player = ExoPlayer.Factory.newInstance(4);
//                    Uri uri = Uri.parse("http://m.qingting.fm/vod/00/00/0000000000000000000026530084_24.m4a");
//                    Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
//                    DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);
//                    ExtractorSampleSource sampleSource = new ExtractorSampleSource(
//                            uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
//                    MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
//                    player.prepare(null, audioRenderer);
//                    player.setPlayWhenReady(true);
//                    player.release(); // Don’t forget to release when done!
                    mediaPlayer.start();
                }
            }
        });

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
        Wearable.DataApi.addListener(mMobvoiApiClient, this);
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
        Log.d(TAG, "connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, 1000);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mMobvoiApiClient.connect();
            }
        } else {
            mResolvingError = false;
            Wearable.DataApi.removeListener(mMobvoiApiClient, this);
            Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
            Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived() A message from watch was received:" + messageEvent
                .getRequestId() + " " + messageEvent.getPath());
        int type = Integer.valueOf(messageEvent.getPath());
        if(type == CONTROL_TYPE_TOGGLE) {
            togglePlayer();
        }
        /*
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
        final String toastStr = s;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "onGestureDetected " + toastStr, Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConnected: " + node);
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected: " + node);
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

    private static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public void test_send(View v) {

        new StartWearableActivityTask().execute();

        Log.v(TAG, "test send");
    }
}
