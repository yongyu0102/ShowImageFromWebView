package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.peng.zhang.activity.R;

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
import utils.ActionSheetDialog;
import utils.OkHttpUtil;

/**
 * description: 加载 WebView 主界面
 * author：pz
 * 时间：2016/10/18 :23:11
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    // 获取img标签正则
    private static final String IMAGE_URL_TAG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMAGE_URL_CONTENT = "http:\"?(.*?)(\"|>|\\s+)";
    public static final String URL_ALL = "urls";

    private WebView mWebView;

    private List<String> listImgSrc = new ArrayList<>();
//        private String url= "http://www.jianshu.com/p/d614bb028588";
    private String url = "http://news.sina.com.cn/china/xlxw/2016-10-23/doc-ifxwztru6946123.shtml";
    private String longClickUrl;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.web_view);
        //长按点击事件
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                responseWebLongClick(v);
                return false;
            }
        });


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF -8");
        //载入js
        mWebView.addJavascriptInterface(new MyJavascriptInterface(this), "imageListener");
        //获取 html
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        mWebView.setWebViewClient(new WebViewClient() {
            // 网页跳转
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // web 页面加载完成，添加监听图片的点击 js 函数
                addImageClickListener();
                //解析 HTML
                parseHTML(view);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "请检查您的网络设置", Toast.LENGTH_SHORT).show();
            }
        });

        mWebView.loadUrl(url);
        mHandler = new Handler();

    }

    /**
     * java 调取 js 代码
     *
     * @param view WebView
     */
    private void parseHTML(WebView view) {
        //这段 js 代码是解析获取到了 Html 文本文件，然后调用本地定义的 Java 代码返回
        //解析出来的 Html 文本文件
        view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    }

    /**
     * 响应 WebView 长按图片的点击事件
     *
     * @param v
     */
    private void responseWebLongClick(View v) {
        if (v instanceof WebView) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (result != null) {
                int type = result.getType();
                //判断点击类型如果是图片
                if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    longClickUrl = result.getExtra();
                    //弹出对话框
                    showDialog(longClickUrl);
                }
            }
        }
    }

    /**
     * 注入 js 函数监听，这段 js 函数的功能就是，遍历所有的图片，并添加 onclick 函数，实现点击事件，
     * 函数的功能是在图片点击的时候调用本地java接口并传递 url 过去
     */
    private void addImageClickListener() {
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imageListener.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    // js 通信接口，定义供 JavaScript 调用的交互接口
    private class MyJavascriptInterface {
        private Context context;

        public MyJavascriptInterface(Context context) {
            this.context = context;
        }

        /**
         * 点击图片启动新的 ShowImageFromWebActivity，并传入点击图片对应的 url 和页面所有图片
         * 对应的 url
         *
         * @param url 点击图片对应的 url
         */
        @android.webkit.JavascriptInterface
        public void openImage(String url) {
            Intent intent = new Intent();
            intent.putExtra("image", url);
            intent.putStringArrayListExtra(URL_ALL, (ArrayList<String>) listImgSrc);
            intent.setClass(context, ShowImageFromWebActivity.class);
            context.startActivity(intent);
        }
    }

    private class InJavaScriptLocalObj {
        /**
         * 获取要解析 WebView 加载对应的 Html 文本
         *
         * @param html WebView 加载对应的 Html 文本
         */
        @android.webkit.JavascriptInterface
        public void showSource(String html) {
            //从 Html 文件中提取页面所有图片对应的地址对象
            getAllImageUrlFromHtml(html);
        }
    }

    /***
     * 获取页面所有图片对应的地址对象，
     * 例如 <img src="http://sc1.hao123img.com/data/f44d0aab7bc35b8767de3c48706d429e" />
     *
     * @param html WebView 加载的 html 文本
     * @return
     */
    private List<String> getAllImageUrlFromHtml(String html) {
        Matcher matcher = Pattern.compile(IMAGE_URL_TAG).matcher(html);
        List<String> listImgUrl = new ArrayList<String>();
        while (matcher.find()) {
            listImgUrl.add(matcher.group());
        }
        //从图片对应的地址对象中解析出 src 标签对应的内容
        getAllImageUrlFormSrcObject(listImgUrl);
        return listImgUrl;
    }

    /***
     * 从图片对应的地址对象中解析出 src 标签对应的内容
     * 即 url，例如 "http://sc1.hao123img.com/data/f44d0aab7bc35b8767de3c48706d429e"
     *
     * @param listImageUrl 图片地址对象，
     *                     例如 <img src="http://sc1.hao123img.com/data/f44d0aab7bc35b8767de3c48706d429e" />
     */
    private List<String> getAllImageUrlFormSrcObject(List<String> listImageUrl) {
        for (String image : listImageUrl) {
            Matcher matcher = Pattern.compile(IMAGE_URL_CONTENT).matcher(image);
            while (matcher.find()) {
                listImgSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        return listImgSrc;
    }

    /**
     * 长按 WebView 中图片弹出对话框，可以选择保存图片
     *
     * @param url 点击图片对应的 url
     */
    private void showDialog(final String url) {
        new ActionSheetDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(
                        "保存到相册",
                        ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                //下载图片
                                downloadImage(url);
                            }
                        }).show();
    }

    /**
     * 开始下载图片
     */
    private void downloadImage(String url) {
        downloadAsync(url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImagesFromWebView");
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

                    final File imageFile = new File(destFileDir, getFileName(url) + ".jpg");
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
                            sendBroadcast(imageFile);
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
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length()) + new Date().getTime();
    }

    /**
     * description：更新相册
     * author：pz
     * data：2016/10/24
     */
    private void sendBroadcast(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
    }
}
