package com.theteamgo.fancywatch;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.theteamgo.fancywatch.utils.STAPI.STAPI;
import com.theteamgo.fancywatch.utils.STAPI.STAPIException;
import com.theteamgo.fancywatch.utils.STAPI.STAPIParameters4Post;
import com.theteamgo.fancywatch.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.mobiwise.playerview.MusicPlayerView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener, MobvoiApiClient.ConnectionCallbacks,
        OnConnectionFailedListener{

    private static final String TAG = "MainActivity";
    private MobvoiApiClient mMobvoiApiClient;
    private boolean mResolvingError = false;
    private Context context;
    public MediaPlayer mediaPlayer;
    private TextView status;
    public String audioTitle = "";


    private MusicPlayerView mpv;
    private ImageView playNextBtn;

    private TextView title;
    private TextView subTitle;
    private List<Song> songList = new ArrayList<>();
    private int song_length;
    private int playIndex = 0;
    public int si = 0;
    private Handler mHandler;

    public int tPickUp;
    public int tArrive;
    public String[] status_list = new String[3];
    public TextView[] tv_status = new TextView[3];
    private String picture;
    private int eyeglass;
    private int gender = -1;
    private int smile;
    private int sunglass;
    private int attractive = -1;
    private int age = -1;


    private STAPI mSTAPI = new STAPI(Constant.STAPI_ID, Constant.STAPI_SECRET);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        context = this;

        getUberProfile();
        getPlayList("");


        ((MyApplication)getApplication()).setMainActivity(this);
        status = (TextView)findViewById(R.id.status);
//        tv_status[0] = (TextView)findViewById(R.id.status1);
//        tv_status[1] = (TextView)findViewById(R.id.status2);
//        tv_status[2] = (TextView)findViewById(R.id.status3);

        playNextBtn = (ImageView)findViewById(R.id.next);
        mHandler = new Handler();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        title = (TextView) findViewById(R.id.textViewSong);
        subTitle = (TextView) findViewById(R.id.textViewSinger);

        //mediaPlayer = new MediaPlayer();

        //mediaPlayer.reset();
        mpv = (MusicPlayerView) findViewById(R.id.mpv);
        /*
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
        */

        mpv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                togglePlayer();
            }
        });
        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                CustomRequest customRequest = new CustomRequest(Constant.UBER_REQUEST, null, context,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("FUCK", response.toString());
                                    String s = response.getString("status");
                                    if (s.equals("processing")) {
                                        status_list[0]=("等待接单中");
                                    }
                                    else if (s.equals("accepted")) {
                                        status_list[0]=("Uber正向您驶来");
                                        int t = response.getJSONObject("pickup").getInt("eta");
                                        tPickUp = t;
                                        status_list[1]=("预计" + t + "分钟到达");
                                        CustomRequest customRequest = new CustomRequest(Constant.ESTIMATE_TIME, null, context,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            tArrive = response.getInt("data");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                    }
                                                }) {
                                        };
                                        customRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        VolleyUtil.getmQueue().add(customRequest);
                                    } else if (s.equals("arriving")) {
                                        status_list[0]=("司机即将到达");
                                        status_list[1]=("预计1分钟后到达");
                                    } else if (s.equals("in_progress")) {
                                        status_list[0]=("乘车中");
                                        status_list[1]=("预计"+tArrive/60+"分钟后到达目的地");
                                    } else if (s.equals("completed")) {
                                        status_list[0]=("祝您旅途愉快");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
//                                Log.i("FUCKERROR", error.toString());
//                                error.printStackTrace();
                                status_list[0]=("欢迎乘坐Uber");
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer "+ ((MyApplication)getApplication()).getSharedPreference("ubertoken"));
                        return headers;
                    }
                };
                customRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleyUtil.getmQueue().add(customRequest);
            }
        };
        mTimer.schedule(mTimerTask, 0, 2000);
        Timer mTimer2 = new Timer();
        TimerTask mTimerTask2 = new TimerTask() {
            @Override
            public void run() {
//                YoYo.with(Techniques.FadeOut).duration(2000).playOn(tv_status[si]);

                si ++;
                if (si == 3)
                    si = 0;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText(status_list[si]);
                    }
                });
            }
        };
        mTimer2.schedule(mTimerTask2, 0, 2000);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    // @Override
                    public void onCompletion(MediaPlayer arg0) {
                        Log.i("FUCKKKKK","FUCK");
                        try {
                            nextMusic();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getUberProfile() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.UBER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject profile = new JSONObject(response);
                            picture = profile.getString("picture");
                            new Thread(test_face).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("uber profile", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + ((MyApplication)getApplication()).getSharedPreference("ubertoken"));
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyUtil.getmQueue().add(request);
    }

    /**
     * 网络操作相关的子线程
     */
    Runnable test_face = new Runnable() {
        @Override
        public void run() {
            test_face_thread();
        }
    };

    public void test_face_thread()  {
        try {
            STAPIParameters4Post params = new STAPIParameters4Post();
            params.setAttributes(true);
            for (int i = 0; i < 1; i++) {
                JSONObject jsonObject = mSTAPI.faceDetection(picture, params);
                //Log.i("detection", jsonObject.toString());
                JSONObject attributes = jsonObject.getJSONArray("faces").getJSONObject(0).getJSONObject("attributes");

                age = attributes.getInt("age");
                gender = attributes.getInt("gender");
                attractive = attributes.getInt("attractive");
                getPlayList("");


                Log.i("detection", jsonObject.getJSONArray("faces").getJSONObject(0).getJSONObject("attributes").toString());
            }
        } catch (STAPIException e) {
            Log.d("detection", e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPlayList(String command) {
        String url;

        if (command.equals(""))
            url = Constant.PLAYLIST + "?";
        else {
            url = Constant.PLAYLIST + "?command=" + command + "&";
//            try {
//                url = URLEncoder.encode(url, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
        }

        if (age != -1) {
            url += ("age=" + age);
        }
        if (gender != -1) {
            url += ("&gender=" + gender);
        }
        if (attractive != -1) {
            url += ("&attractive=" + attractive);
        }

        try {
                url = URLEncoder.encode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        Log.d("command", url);

        CustomRequest customRequest = new CustomRequest(url, null, this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response.getString("data"));
                            Log.i("test", jsonArray.toString());
                            song_length = jsonArray.length();
                            songList.clear();
                            for (int i = 0 ; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Song song = new Song();
                                song.mediaUrl = jsonObject.getString("mediaUrl");
                                song.mediaImageUrl = jsonObject.getString("mediaImageUrl");
                                song.mediaTitle = jsonObject.getString("mediaTitle");
                                song.mediaSubtitle = jsonObject.getString("mediaSubtitle");
                                song.mediaLength = jsonObject.getInt("mediaLength");
                                status_list[2]=(jsonObject.getString("msg"));
                                if (song.mediaLength < 300)
                                    songList.add(song);
                            }

                            playIndex = 0;
                            nextMusic();
//                            playNext();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    playAll();
//                                }
//                            });
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

    private void startMusic() {
        try {
            if (playIndex>= songList.size())
                playIndex=0;
            mpv.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songList.get(playIndex).mediaUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mpv.start();
                }
            });

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void stopMusic() {
//            mediaPlayer.pause();// 停止
//            mpv.stop();
//    }

    private void nextMusic() {
        startMusic();
        mpv.setCoverURL(songList.get(playIndex).mediaImageUrl);
        mpv.setMax(songList.get(playIndex).mediaLength);
        mpv.setProgress(0);
        title.setText(songList.get(playIndex).mediaTitle);
        audioTitle = songList.get(playIndex).mediaTitle;
        subTitle.setText(songList.get(playIndex).mediaSubtitle);
        playIndex++;
    }

    private void changeMusic() {
        CustomRequest customRequest = new CustomRequest(Constant.PLAYACTION + "?operation=1&username="
                + ((MyApplication)getApplication()).getSharedPreference("username") + "&url=" + songList.get(playIndex).mediaUrl, null, this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("change", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        VolleyUtil.getmQueue().add(customRequest);
        nextMusic();
    }

    private void praiseMusic() {
        CustomRequest customRequest = new CustomRequest(Constant.PLAYACTION + "?operation=2&username="
                + ((MyApplication)getApplication()).getSharedPreference("username") + "&url=" + songList.get(playIndex).mediaUrl, null, this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("praise", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        VolleyUtil.getmQueue().add(customRequest);
    }

    private void finishMusic() {
        CustomRequest customRequest = new CustomRequest(Constant.PLAYACTION + "?operation=0&username="
                + ((MyApplication)getApplication()).getSharedPreference("username") + "&url=" + songList.get(playIndex).mediaUrl, null, this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("finish", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        VolleyUtil.getmQueue().add(customRequest);
        nextMusic();
    }

    private void playAll() {
        nextMusic();
        /* 当MediaPlayer.OnCompletionLister会运行的Listener */
        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    // @Override
                    public void onCompletion(MediaPlayer arg0) {
                        try {
                            finishMusic();
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
                if (mpv.isRotating()) {
                    try {
                        mpv.stop();
                        mediaPlayer.pause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.pause();
                } else {
                    try {
                        mpv.start();
                        mediaPlayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                getPlayList(text);
            }
        });
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

        try {
            int type = Integer.valueOf(messageEvent.getPath());
            String txt2= new String(messageEvent.getData(), "utf-8");
            Log.d("FUCK2", txt2);
            if (type == Constant.CONTROL_TYPE_TOGGLE) {
                if(((MyApplication) getApplication()).getMainActivity() != null)
                    ((MyApplication) getApplication()).getMainActivity().togglePlayer();
            } else if(type == Constant.CONTROL_TYEP_REQUEST_INFO){
                if(((MyApplication) getApplication()).getMainActivity() != null)
                    ((MyApplication) getApplication()).getMainActivity().sendAudioInfo();
                Log.d(TAG, "requset info");
            }
            //Toast.makeText(getApplicationContext(), "onGestureDetected " + s, Toast.LENGTH_SHORT).show();
            else if (type == Constant.CONTROL_WORD_COMMAND) {
                String txt = new String(messageEvent.getData(), "utf-8");
                //Log.d("FUCK", txt);
                if(txt.indexOf("换台") != -1)
//                    ((MyApplication) getApplication()).getMainActivity().playNext();
                    nextMusic();
                else
                    ((MyApplication) getApplication()).getMainActivity().changeStatus(txt);
            } else if (type == Constant.CONTROL_TYPE_NEXT) {
                    nextMusic();
//                ((MyApplication) getApplication()).getMainActivity().playNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if(mediaPlayer == null)
                return null;
            Collection<String> nodes = getNodes();
            JSONObject jobject = new JSONObject();
            try {
                jobject.put("title",audioTitle);
                jobject.put("isPlaying",mediaPlayer.isPlaying());
                jobject.put("position",mediaPlayer.getCurrentPosition());
                jobject.put("duration",mediaPlayer.getDuration());
                String jstr = jobject.toString();
                for (String node : nodes) {
                    sendAudioInfoMessage(node, jstr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
        changeMusic();
    }

    public void click_praise(View v) {
        praiseMusic();
    }
}
