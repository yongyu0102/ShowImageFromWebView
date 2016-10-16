package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import utils.ActionSheetDialog;
import com.peng3.big.big.activity.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.OkHttpUtil;

/**
 * Email  : bigbigpeng3@gmail.com
 * Author : peng zhang
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String URL_ALL = "urls";
    private WebView mWebView;

    private List<String> listImgSrc = new ArrayList<>();

    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

    private String url;
    private String longClickUrl;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //就让我更无耻一点吧!
        url = "http://www.jianshu.com/p/d614bb028588";

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v instanceof WebView) {
                    WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                    if (result != null) {
                        int type = result.getType();
                        if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                            longClickUrl = result.getExtra();
                           showDialog(longClickUrl);
                        }
                    }
                }
                return false;
            }
        });

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF -8");

        //载入js
        mWebView.addJavascriptInterface(new MyJavascriptInterface(this), "imagelistner");
        //获取 html
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");

        mWebView.setWebViewClient(new WebViewClient() {
            // 网页开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.getSettings().setJavaScriptEnabled(true);

                super.onPageStarted(view, url, favicon);
            }

            // 网页跳转
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);

            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);

                super.onPageFinished(view, url);

                // html加载完成之后，添加监听图片的点击js函数
                addImageClickListner();

                view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                        + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //出现页面错误的时候，不让webView显示了。同时跳出一个错误Toast
                mWebView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            // 网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }
        });

        mWebView.loadUrl(url);
        mHandler=new Handler();

    }

    private void showDialog(final String url) {
        ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(
                        "请选择地图",
                        ActionSheetDialog.SheetItemColor.Grey,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                            }
                        });
        actionSheetDialog.addSheetItem(
                "保存到相册",
                ActionSheetDialog.SheetItemColor.Blue,
                new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        downloadImage(url);
                    }
                }).show();
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    // js通信接口
    public class MyJavascriptInterface {

        private Context context;


        public MyJavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            System.out.println(img);
            Intent intent = new Intent();
            intent.putExtra("image", img);
            intent.putStringArrayListExtra(URL_ALL, (ArrayList<String>) listImgSrc);
//            intent.setClass(context, ShowWebImageActivity.class);
            intent.setClass(context, ShowImageFromWebActivity.class);
//            intent.setClass(context,PicViewerActivity.class);
            context.startActivity(intent);
            System.out.println(img);
        }
    }

    /**
     * 打印 Html 内容
     */
    final class InJavaScriptLocalObj {
        @android.webkit.JavascriptInterface
        public void showSource(String html) {
//            System.out.println("====>html="+html);
            getImageUrl(html);
        }
    }

    /***
     * 获取ImageUrl地址标签
     *
     * @param HTML
     * @return
     */
    private List<String> getImageUrl(String HTML) {
        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);
        List<String> listImgUrl = new ArrayList<String>();
        while (matcher.find()) {
            listImgUrl.add(matcher.group());
        }
        getImageSrc(listImgUrl);
        return listImgUrl;
    }

    /***
     * 获取ImageSrc地址
     *
     * @param listImageUrl
     * @return
     */
    private List<String> getImageSrc(List<String> listImageUrl) {
        for (String image : listImageUrl) {
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
            while (matcher.find()) {
                listImgSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
//                Log.d(TAG,matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        return listImgSrc;
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getTitle() == "保存到手机") {
//                    new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
//                } else {
//                    return false;
//                }
//                return true;
//            }
//        };
//
//        if (v instanceof WebView) {
//            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
//            if (result != null) {
//                int type = result.getType();
//                if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
//                    longClickUrl = result.getExtra();
//                    menu.setHeaderTitle("提示");
//                    menu.add(0, v.getId(), 0, "保存到手机").setOnMenuItemClickListener(handler);
//                }
//            }
//        }
//    }

//    /***
//     * 功能：用线程保存图片
//     *
//     * @author wangyp
//     */
//    private class SaveImage extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            String result = "";
//            try {
//                String sdcard = Environment.getExternalStorageDirectory().toString();
//                File file = new File(sdcard + "/Download");
//                if (!file.exists()) {
//                    file.mkdirs();
//                }
//                int idx = longClickUrl.lastIndexOf(".");
//                String ext = longClickUrl.substring(idx);
//                file = new File(sdcard + "/Download/" + new Date().getTime() + ext);
//                InputStream inputStream = null;
//                URL url = new URL(longClickUrl);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//                conn.setConnectTimeout(20000);
//                if (conn.getResponseCode() == 200) {
//                    inputStream = conn.getInputStream();
//                }
//                byte[] buffer = new byte[4096];
//                int len = 0;
//                FileOutputStream outStream = new FileOutputStream(file);
//                while ((len = inputStream.read(buffer)) != -1) {
//                    outStream.write(buffer, 0, len);
//                }
//                outStream.close();
//                result = "图片已保存至：" + file.getAbsolutePath();
//            } catch (Exception e) {
//                result = "保存失败！" + e.getLocalizedMessage();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//        }
//    }


    /**
     * 开始下载图片
     */
    private void downloadImage(String url) {
        downloadAsyn(url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImagesFromWebView");
    }


    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     */
    private void downloadAsyn(final String url, final String destFileDir) {

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
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length())+ new Date().getTime();
    }
}
