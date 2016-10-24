package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.peng.zhang.activity.R;

import java.util.ArrayList;

import adapter.ImageBrowserAdapter;
import utils.Constant;
import utils.ImageLoaderUtils;

public class ShowImageFromWebActivity extends Activity implements View.OnClickListener {
    private ViewPager vpImageBrowser;
    private TextView tvImageIndex;
    private Button btnSave;

    private ImageBrowserAdapter adapter;
    private ArrayList<String> imgUrls;
    private String url;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_from_web);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        vpImageBrowser = (ViewPager) findViewById(R.id.vp_image_browser);
        tvImageIndex = (TextView) findViewById(R.id.tv_image_index);
        btnSave = (Button) findViewById(R.id.btn_save);
    }

    private void initData() {
        imgUrls = getIntent().getStringArrayListExtra(Constant.IMAGE_URL_ALL);
        url = getIntent().getStringExtra(Constant.IMAGE_URL);
        int position = imgUrls.indexOf(url);
        adapter = new ImageBrowserAdapter(this, imgUrls);
        vpImageBrowser.setAdapter(adapter);
        final int size = imgUrls.size();

        if (size > 1) {
            tvImageIndex.setVisibility(View.VISIBLE);
            tvImageIndex.setText((position + 1) + "/" + size);
        } else {
            tvImageIndex.setVisibility(View.GONE);
        }

        vpImageBrowser.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                currentIndex = arg0;
                int index = arg0 % size;
                tvImageIndex.setText((index + 1) + "/" + size);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        vpImageBrowser.setCurrentItem(position);
    }

    private void initListener() {
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                downloadImage();
                break;
        }
    }

    /**
     * 开始下载图片
     */
    private void downloadImage() {
        ImageLoaderUtils.downLoadImage(imgUrls.get(currentIndex), Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImagesFromWebView", this);
    }

}
