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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import com.theteamgo.fancywatch.common.Constant;
import com.theteamgo.fancywatch.utils.CustomRequest;
import com.theteamgo.fancywatch.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.mobiwise.playerview.MusicPlayerView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener, MobvoiApiClient.ConnectionCallbacks,
        OnConnectionFailedListener{

    private static final String TAG = "MainActivity";
    private MobvoiApiClient mMobvoiApiClient;
    private boolean mResolvingError = false;
    private Context context;
    public MediaPlayer mediaPlayer;
    private TextView status;
    public String audioTitle = "test";


    private MusicPlayerView mpv;
    private ImageView playNextBtn;

    private TextView title;
    private TextView subTitle;
    private List<Song> songList = new ArrayList<>();
    private int playIndex = 0;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        context = this;

        VolleyUtil volleyUtil = new VolleyUtil(this);

        ((MyApplication)getApplication()).setMainActivity(this);
        GetPlayList();
        status = (TextView)findViewById(R.id.status);
        playNextBtn = (ImageView)findViewById(R.id.next);
        mHandler = new Handler();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        title = (TextView) findViewById(R.id.textViewSong);
        subTitle = (TextView) findViewById(R.id.textViewSinger);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.reset();
        mpv = (MusicPlayerView) findViewById(R.id.mpv);
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    mediaPlayer.pause();
                } else {
                    mpv.start();
                    mediaPlayer.start();
                }
            }
        });

        mpv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                togglePlayer();
            }
        });
    }

    public void GetPlayList() {
        CustomRequest customRequest = new CustomRequest(Constant.PLAYLIST, null, this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response.getString("data"));
                            Log.i("test", jsonArray.toString());
                            songList.clear();
                            for (int i = 0 ; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Song song = new Song();
                                song.mediaUrl = jsonObject.getString("mediaUrl");
                                song.mediaImageUrl = jsonObject.getString("mediaImageUrl");
                                song.mediaTitle = jsonObject.getString("mediaTitle");
                                song.mediaSubtitle = jsonObject.getString("mediaSubtitle");
                                song.mediaLength = jsonObject.getInt("mediaLength");
                                if (song.mediaLength < 300)
                                    songList.add(song);
                            }

                            playIndex = -1;
                            playAll();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        customRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyUtil.getmQueue().add(customRequest);
    }

    private void startMusic(int index) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(songList.get(index).mediaUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();// 停止
        }
        if(mpv.isRotating())
            mpv.stop();
    }

    private void nextMusic() {
        stopMusic();
        playIndex++;
        if (playIndex >= songList.size()) {
            return;
        }

        startMusic(playIndex);
        mpv.setCoverURL(songList.get(playIndex).mediaImageUrl);
        mpv.setMax(songList.get(playIndex).mediaLength);
        mpv.setProgress(0);
        title.setText(songList.get(playIndex).mediaTitle);
        subTitle.setText(songList.get(playIndex).mediaSubtitle);
        mpv.start();
    }

    private void playAll() {
        nextMusic();
        if (playIndex >= songList.size()) {
            return;
        }
        /* 当MediaPlayer.OnCompletionLister会运行的Listener */
        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    // @Override
                    public void onCompletion(MediaPlayer arg0) {
                        try {
                            nextMusic();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void togglePlayer() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    try {
                        mpv.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.pause();
                } else {
                    try {
                        mpv.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }
            }
        });
    }

    public void changeStatus(final String text) {
//        status.setText(text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText(text);
            }
        });
    }

    public void playNext() {
    }

    public void sendAudioInfo() {
        new StartSendingAudioInfoTask().execute();
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
        sendAudioInfo();
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
        //int type = Integer.valueOf(messageEvent.getPath());
        //if(type == Constant.CONTROL_TYPE_TOGGLE) {
        //    togglePlayer();
        //} else if(type == Constant.CONTROL_TYEP_REQUEST_INFO) {
        //    new StartSendingAudioInfoTask().execute();
        //}
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

    private void sendAudioInfoMessage(String node, String msg) {
        try {
            Wearable.MessageApi.sendMessage(
                    mMobvoiApiClient, node, "" + Constant.CONTROL_TYPE_INFO, msg.getBytes("UTF-8")).setResultCallback(
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

    private class StartSendingAudioInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendAudioInfoMessage(node, audioTitle);
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

    public void click_play_next(View v) {
        nextMusic();
    }
}
