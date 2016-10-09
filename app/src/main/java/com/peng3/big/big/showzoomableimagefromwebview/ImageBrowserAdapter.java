package com.peng3.big.big.showzoomableimagefromwebview;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bm.library.PhotoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageBrowserAdapter extends PagerAdapter {

	private static final String TAG =ImageBrowserAdapter.class.getSimpleName() ;
	private Activity context;
	private List<String> picUrls;
	private ArrayList<View> picViews;

	public ImageBrowserAdapter(Activity context, ArrayList<String> picUrls) {
		this.context = context;
		this.picUrls = picUrls;
//		this.picUrls= Arrays.asList(images);
		initImgs();
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
		View view = picViews.get(position);
		PhotoView iv_image_browser = (PhotoView) view.findViewById(R.id.show_webimage_imageview);
		String picUrl = picUrls.get(position);

		// 启用图片缩放功能
		iv_image_browser.enable();
		//显示图片
		ImageLoaderUtils.displayWhole(context, iv_image_browser, picUrl);
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