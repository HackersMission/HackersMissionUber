package com.theteamgo.fancywatch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.theteamgo.fancywatch.utils.CustomRequest;
import com.theteamgo.fancywatch.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by houfang on 16/1/16.
 */
public class LoginUberActivity extends AppCompatActivity {
    private String TAG = "test";
    private WebView webView;
    private Activity context;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uber_login);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                LoginUberActivity.this.setProgress(progress * 1000);
            }
        });

        webView.setWebViewClient(new UberWebViewClient());
        webView.loadUrl(buildUrl());
        context = this;
    }

    private String buildUrl() {
        Log.i("url", Constant.UBER_AUTH + "?response_type=" + Constant.UBER_RESPONSE_TYPE +
                "&client_id=" + Constant.UBER_CLIENT_ID +
                "&scope=" + Constant.UBER_SCOPE +
                "&redirect_uri=" + Constant.UBER_REDIRECT_URL);
        return Constant.UBER_AUTH + "?response_type=" + Constant.UBER_RESPONSE_TYPE +
                "&client_id=" + Constant.UBER_CLIENT_ID +
                "&scope=" + Constant.UBER_SCOPE +
                "&redirect_uri=" + Constant.UBER_REDIRECT_URL;
        /*Uri.Builder uriBuilder = Uri.parse(Constant.UBER_AUTH).buildUpon();
        uriBuilder.appendQueryParameter("response_type", Constant.UBER_RESPONSE_TYPE);
        uriBuilder.appendQueryParameter("client_id", Constant.UBER_CLIENT_ID);
        uriBuilder.appendQueryParameter("scope", Constant.UBER_SCOPE);
        uriBuilder.appendQueryParameter("redirect_uri", Constant.UBER_REDIRECT_URL);
        Log.i("url", uriBuilder.build().toString().replace("%20", "+"));
        return uriBuilder.build().toString().replace("%20", "+");*/
    }

    private class UberWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("override", url);
            return checkRedirect(url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (checkRedirect(failingUrl)) {
                return;
            }
            Toast.makeText(LoginUberActivity.this, "Error " + description, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkRedirect(String url) {
        if (url.startsWith(Constant.UBER_REDIRECT_URL)) {
            Log.i("redirect", url);
            Uri uri = Uri.parse(url);
            String authorization_code = uri.getQueryParameter("code");
            Log.i("token", authorization_code);
            HashMap params = new HashMap<String, String>();
            params.put("client_secret", Constant.UBER_SECRET);
            params.put("client_id", Constant.UBER_CLIENT_ID);
            params.put("grant_type", Constant.UBER_GRANT_TYPE);
            params.put("redirect_uri", Constant.UBER_REDIRECT_URL);
            params.put("code", authorization_code);
            CustomRequest customRequest = new CustomRequest(Request.Method.POST, Constant.UBER_TOKEN, params, this,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.i("token", response.toString());
                                String token_type = response.getString("token_type");
                                String access_token = response.getString("access_token");
                                String expires_in = response.getString("expires_in");
                                String last_authenticated = response.getString("last_authenticated");
                                String refresh_token = response.getString("refresh_token");

                                CustomRequest customRequest1 = new CustomRequest(Constant.ADD_TOKEN +
                                        "?token_type=" + token_type +
                                        "&access_token=" + access_token +
                                        "&last_authenticated=" + last_authenticated +
                                        "&expires_in=" + expires_in +
                                        "&refresh_token=" + refresh_token +
                                        "&username=" + username, null, context,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.i("success", response.toString());
                                                Intent intent = new Intent();
                                                intent.setClass(context, MainActivity.class);
                                                startActivity(intent);
                                                context.finish();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                VolleyUtil.getmQueue().add(customRequest1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("fail", error.toString());
                        }
                    });

            VolleyUtil.getmQueue().add(customRequest);
            return true;
        }
        else {
            return false;
        }
    }
}
