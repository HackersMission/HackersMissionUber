package com.theteamgo.fancywatch.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by houfang on 15/8/16.
 */
public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> mCache;

    public BitmapCache() {
        mCache = new LruCache<String, Bitmap>((int)(Runtime.getRuntime().maxMemory() / 1024 / 8)) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }

        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }
}
