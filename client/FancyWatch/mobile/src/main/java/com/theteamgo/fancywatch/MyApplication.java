package com.theteamgo.fancywatch;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by jesse on 16/1/16.
 */
public class MyApplication extends Application {
    MainActivity mainActivity = null;
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public MainActivity getMainActivity() {
        return this.mainActivity;
    }

    public void saveSharedPreferences(String username, String password) {
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences= getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
        editor.putString("username", username);
        editor.putString("password", password);
        //提交当前数据
        editor.commit();
        //使用toast信息提示框提示成功写入数据
//        Toast.makeText(this, "数据成功写入SharedPreferences！", Toast.LENGTH_LONG).show();
    }


    public void saveUberToken(String token) {
        SharedPreferences mySharedPreferences= getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
        editor.putString("ubertoken", token);
        //提交当前数据
        editor.commit();
        //使用toast信息提示框提示成功写入数据
//        Toast.makeText(this, "数据成功写入SharedPreferences！", Toast.LENGTH_LONG).show();
    }
    public String getSharedPreference(String name) {
        //同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
        SharedPreferences sharedPreferences= getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        String value =sharedPreferences.getString(name, "");
        return value;
        //使用toast信息提示框显示信息
        //Toast.makeText(this, "读取数据如下："+"\n"+"name：" + name + "\n" + "habit：" + habit,
        //        Toast.LENGTH_LONG).show();
    }
}
