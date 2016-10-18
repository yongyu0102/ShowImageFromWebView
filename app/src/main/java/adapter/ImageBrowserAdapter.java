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
import com.peng3.big.big.activity.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageBrowserAdapter extends PagerAdapter {

	private static final String TAG =ImageBrowserAdapter.class.getSimpleName() ;
	private Activity context;
	private List<String> picUrls;
	private ArrayList<View> picViews;
	private PhotoViewAttacher photoViewAttacher;

	public ImageBrowserAdapter(Activity context, ArrayList<String> picUrls) {
		this.context = context;
		this.picUrls = picUrls;
//		this.picUrls= Arrays.asList(images);
//		initImgs();
	}

	private void initImgs() {
		picViews = new ArrayList<View>();
		
		for(int i=0; i<picUrls.size(); i++) {
			// 填充显示图片的页面布局
			View view = View.inflate(context, R.layout.item_image_browser, null);
			picViews.add(view);
		}
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
//		View view = picViews.get(position);
		View view = View.inflate(context, R.layout.item_image_browser, null);
//		View view = View.inflate(context, R.layout.fragment_picture_slide, null);
		ImageView iv_image_browser = (ImageView) view.findViewById(R.id.show_webimage_imageview);
//		ImageView iv_image_browser = (ImageView) view.findViewById(R.id.iv_main_pic);
		String picUrl = picUrls.get(position);
		photoViewAttacher=new PhotoViewAttacher(iv_image_browser);
//		photoViewAttacher.canZoom();
		photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
//		photoViewAttacher.setZoomable(true);
		// 启用图片缩放功能
//		iv_image_browser.setEnabled(true);
		//显示图片
//		ImageLoaderUtils.displayWhole(context, iv_image_browser, picUrl,photoViewAttacher);
		Glide.with(context).
				load(picUrl)
				.crossFade()
				.placeholder(R.drawable.avatar_default)
				.error(R.drawable.image_default_rect)
				.into(new GlideDrawableImageViewTarget(iv_image_browser){
					@Override
					public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
						super.onResourceReady(resource, animation);
						if (photoViewAttacher!=null){
							photoViewAttacher.update();
						}
					}
				});

		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	private String[] images= images= new String[] {
			"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg",
	};

	

}