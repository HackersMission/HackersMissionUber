package com.theteamgo.fancywatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.theteamgo.fancywatch.utils.CustomRequest;
import com.theteamgo.fancywatch.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by houfang on 16/1/16.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText name_text, password_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        name_text = (EditText) findViewById(R.id.nickNameEditText);
        password_text = (EditText) findViewById(R.id.password_EditText);
        VolleyUtil volleyUtil = new VolleyUtil(getApplication());

    }

    public void click_to_login(View v) {
        String name = name_text.getText().toString();
        String password = password_text.getText().toString();
        //name = "test1";
        //password = "12345";
        final String finalName = name;
        CustomRequest customRequest = new CustomRequest(Constant.REGISTER + "?username=" + name + "&password=" + password, null, this,
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
                                intent.setClass(RegisterActivity.this, LoginUberActivity.class);
                                startActivity(intent);
                                finish();
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
}
