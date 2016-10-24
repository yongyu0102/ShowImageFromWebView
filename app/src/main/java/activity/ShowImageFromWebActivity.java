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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import adapter.ImageBrowserAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.OkHttpUtil;

public class ShowImageFromWebActivity extends Activity implements View.OnClickListener {
    private ViewPager vpImageBrowser;
    private TextView tvImageIndex;
    private Button btnSave;

    private ImageBrowserAdapter adapter;
    private ArrayList<String> imgUrls;
    private String url;
    private int currentIndex;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_from_web);
        initView();
        initListener();
        initData();
    }

    private void initView(){
        vpImageBrowser = (ViewPager) findViewById(R.id.vp_image_browser);
        tvImageIndex = (TextView) findViewById(R.id.tv_image_index);
        btnSave = (Button) findViewById(R.id.btn_save);
    }


    private void initData(){
        mHandler = new Handler();
        imgUrls=getIntent().getStringArrayListExtra(MainActivity.URL_ALL);
        url=getIntent().getStringExtra("image");
        int position=imgUrls.indexOf(url);
        adapter=new ImageBrowserAdapter(this,imgUrls);
        vpImageBrowser.setAdapter(adapter);
        final int size=imgUrls.size();

        if(size > 1) {
            tvImageIndex.setVisibility(View.VISIBLE);
            tvImageIndex.setText((position+1) + "/" + size);
        } else {
            tvImageIndex.setVisibility(View.GONE);
        }


        vpImageBrowser.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                currentIndex=arg0;
                int index = arg0 % size;
                tvImageIndex.setText((index+1) + "/" + size);

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

    private void initListener(){
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save :
                Toast.makeText(getApplicationContext(), "开始下载图片", Toast.LENGTH_SHORT).show();
                downloadImage();
                break;
        }
    }


    /**
     * 开始下载图片
     */
    private void downloadImage() {
        downloadAsync(imgUrls.get(currentIndex), Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImagesFromWebView");
    }


    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     */
    private void downloadAsync(final String url, final String destFileDir) {

        OkHttpUtil mOkHttpUtil = OkHttpUtil.getInstance();
        OkHttpClient mOkHttpClient = mOkHttpUtil.getOkHttpClient();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        final Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                Toast.makeText(getApplicationContext(), "下载失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir);
                    //如果file不存在,就创建这个file
                    if (!file.exists()) {
                        file.mkdir();
                    }

                    File imageFile = new File(destFileDir, getFileName(url) + ".jpg");
                    fos = new FileOutputStream(imageFile);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    //sendSuccessResultCallback(file.getAbsolutePath(), callback);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {

                    e.printStackTrace();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "下载失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                    });

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

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

}
