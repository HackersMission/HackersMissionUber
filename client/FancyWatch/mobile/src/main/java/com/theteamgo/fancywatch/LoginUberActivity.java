package com.theteamgo.fancywatch;

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

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by houfang on 16/1/16.
 */
public class LoginUberActivity extends AppCompatActivity {
    private String TAG = "test";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uber_login);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                LoginUberActivity.this.setProgress(progress * 1000);
            }
        });

        webView.setWebViewClient(new UberWebViewClient());
        webView.loadUrl(buildUrl());
    }

    private String buildUrl() {
        Uri.Builder uriBuilder = Uri.parse(Constant.UBER_AUTH).buildUpon();
        uriBuilder.appendQueryParameter("response_type", Constant.UBER_RESPONSE_TYPE);
        uriBuilder.appendQueryParameter("client_id", Constant.UBER_CLIENT_ID);
        uriBuilder.appendQueryParameter("scope", Constant.UBER_SCOPE);
        uriBuilder.appendQueryParameter("redirect_uri", null);
        Log.i("url", uriBuilder.build().toString().replace("%20", "+"));
        return uriBuilder.build().toString().replace("%20", "+");
    }

    private class UberWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, url);
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
        if (url.startsWith("REDIRECT_URL")) {
            Uri uri = Uri.parse(url);
            String authorization_code = uri.getQueryParameter("code");
            HashMap params = new HashMap<String, String>();
            params.put("client_secret", Constant.UBER_SECRET);
            params.put("client_id", Constant.UBER_CLIENT_ID);
            params.put("grant_type", Constant.UBER_GRANT_TYPE);
            params.put("redirect_uri", "YOUR_REDIRECT_URI");
            params.put("code", authorization_code);
            CustomRequest customRequest = new CustomRequest(Request.Method.POST, Constant.UBER_AUTH, params, this,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, error.toString());
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
