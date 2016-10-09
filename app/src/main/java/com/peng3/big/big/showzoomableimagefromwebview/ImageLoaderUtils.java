package com.peng3.big.big.showzoomableimagefromwebview;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Description : 图片加载工具类
 */
public class ImageLoaderUtils {

    public static void display(Context context, ImageView imageView, String url, int placeholder, int error) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url).thumbnail(0.8f).placeholder(placeholder)
                .error(error).crossFade().fitCenter().into(imageView);
    }

    public static void display(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }

        Glide.with(context).load(url).thumbnail(0.8f).into(imageView);
    }

    public static void displayWhole(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Log.d("ImageBrowserAdapter",url);

        Glide.with(context).
                load(url)
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.image_default_rect)
                 .into(imageView);

    }


}
