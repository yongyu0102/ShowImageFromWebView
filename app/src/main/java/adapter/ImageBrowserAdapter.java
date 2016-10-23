package adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.peng.zhang.activity.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageBrowserAdapter extends PagerAdapter {

	private Activity context;
	private List<String> picUrls;

	public ImageBrowserAdapter(Activity context, ArrayList<String> picUrls) {
		this.context = context;
		this.picUrls = picUrls;
	}


	@Override
	public int getCount() {

		return picUrls.size();
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		View view = View.inflate(context, R.layout.item_image_browser, null);
		ImageView pvShowImage = (ImageView) view.findViewById(R.id.pv_show_image);
		String picUrl = picUrls.get(position);
		final  PhotoViewAttacher photoViewAttacher=new PhotoViewAttacher(pvShowImage);
		photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
		photoViewAttacher.setZoomable(false);
		photoViewAttacher.setMinimumScale(1F);
		Glide.with(context).
				load(picUrl)
				.crossFade()
				.placeholder(R.drawable.avatar_default)
				.error(R.drawable.image_default_rect)
				.into(new GlideDrawableImageViewTarget(pvShowImage){
					@Override
					public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
						super.onResourceReady(resource, animation);
						photoViewAttacher.setZoomable(true);
						photoViewAttacher.update();
					}
				});

		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}


	

}