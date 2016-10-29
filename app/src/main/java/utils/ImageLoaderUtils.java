package utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.peng.zhang.activity.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import api.ApiManager;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
                .into(new GlideDrawableImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        if (photoViewAttacher != null) {
                            photoViewAttacher.update();
                        }
                    }
                });
    }

    public static void downLoadImage(final String url, final String destFileDir,final Context context) {
        ApiManager.getInstance().getApiService().downLoadImage(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastManager.show("保存失败");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        try {
                            is = responseBody.byteStream();
                            File file = new File(destFileDir);
                            //如果file不存在,就创建这个file
                            if (!file.exists()) {
                                file.mkdir();
                            }

                            final File imageFile = new File(destFileDir,  new Date().getTime() + ".jpg");
                            fos = new FileOutputStream(imageFile);
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                            }
                            fos.flush();
                            ToastManager.show("保存成功");
                            //更新相册
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(file);
                            intent.setData(uri);
                            context.sendBroadcast(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                            ToastManager.show("保存失败");
                        } finally {
                            try {
                                if (is != null) is.close();
                            } catch (IOException e) {
                            }
                            try {
                                if (fos != null) fos.close();
                            } catch (IOException e) {
                            }
                        }

                    }
                });
    }


}
