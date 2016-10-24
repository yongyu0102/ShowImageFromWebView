package utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.peng.zhang.activity.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * description：图片加载工具类
 * author：pz
 * data：2016/10/24
 */
public class ImageLoaderUtils {

    public static void displayScaleImage(Context context, final ImageView imageView, String url, final PhotoViewAttacher photoViewAttacher) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }

        Glide.with(context).
                load(url)
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.image_default_rect)
                 .into(new GlideDrawableImageViewTarget(imageView){
                     @Override
                     public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                         super.onResourceReady(resource, animation);
                         if (photoViewAttacher!=null){
                             photoViewAttacher.update();
                             photoViewAttacher.setZoomable(true);
                         }
                     }
                 });

    }


}
