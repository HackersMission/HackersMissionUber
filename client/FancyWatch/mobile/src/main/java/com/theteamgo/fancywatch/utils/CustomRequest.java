package com.theteamgo.fancywatch.utils;



import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
//import com.theteamgo.teamgo.utils.sharedPreferenceHelper.Auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by houfang on 16/1/16.
 */

public class CustomRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    private Context context;

    public CustomRequest(String url, Map<String, String> params, Context context,
                         Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        this.params = params;
        this.context = context;
    }

    public CustomRequest(int method, String url, Map<String, String> params, Context context,
                         Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = responseListener;
        this.params = params;
        this.context = context;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }
    /*
    public Map<String, String> getHeaders() throws AuthFailureError {
        String token = Auth.getToken(context);
        HashMap<String, String> headers = new HashMap<>();
        if (token != null && token.length() != 0) {
            Log.i("Authorization", token);
            headers.put("Authorization", "Token " + token);
        }

        return headers;
    }*/

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, "UTF8");
            //HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}