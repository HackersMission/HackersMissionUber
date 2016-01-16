package com.theteamgo.fancywatch.utils;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by houfang on 15/7/20.
 */
public class VolleyUtil {
    public static RequestQueue mQueue = null;
    public static ImageLoader imageLoader = null;
    public static ImageLoader.ImageCache cache = new BitmapCache();

    public VolleyUtil(Context context) {
        mQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(mQueue, cache);
    }

    public static ImageLoader.ImageCache getImageCache() {return cache;}
    public static RequestQueue getmQueue() {
        return mQueue;
    }
}