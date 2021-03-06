package com.theteamgo.fancywatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.theteamgo.fancywatch.common.Constant;
import com.theteamgo.fancywatch.utils.CustomRequest;
import com.theteamgo.fancywatch.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by houfang on 16/1/16.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText name_text, password_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name_text = (EditText) findViewById(R.id.nickNameEditText);
        password_text = (EditText) findViewById(R.id.password_EditText);
        VolleyUtil volleyUtil = new VolleyUtil(getApplication());
        Log.i("name", ((MyApplication)getApplication()).getSharedPreference("username"));
        Log.i("password", ((MyApplication)getApplication()).getSharedPreference("password"));
        if (!((MyApplication)getApplication()).getSharedPreference("username").equals("") && !((MyApplication)getApplication()).getSharedPreference("password").equals("")) {
            login(((MyApplication)getApplication()).getSharedPreference("username"), ((MyApplication)getApplication()).getSharedPreference("password"));
        }

    }

    public void login(String name, String password) {
        Log.i("test", "login");
        final String finalName = name;
        CustomRequest customRequest = new CustomRequest(Constant.LOGIN + "?username=" + name + "&password=" + password, null, this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("data", response.toString());
                            if (response.getInt("status") == 0) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("username", finalName);
                                intent.putExtras(bundle);
                                intent.setClass(LoginActivity.this, LoginUberActivity.class);
                                startActivity(intent);
                                Log.i("success", response.toString());
                            }
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
    }

    public void click_to_login(View v) {
        String name = name_text.getText().toString();
        String password = password_text.getText().toString();
        name = "test1";
        password = "12345";
        ((MyApplication)getApplication()).saveSharedPreferences(name, password);
        login(name, password);
    }

    public void click_to_register(View v) {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
    }
}
