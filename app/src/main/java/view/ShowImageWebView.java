package view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import activity.ShowImageFromWebActivity;
import utils.Constant;

/**
 * 可以实现点击图片进行保存的 WebView
 * Created by FZT on 2016/10/10.
 */
public class ShowImageWebView extends WebView {

    private List<String> listImgSrc = new ArrayList<>();

    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

    private String url;
    private String longClickUrl;
    private Context context;

    public ShowImageWebView(Context context) {
        super(context);
        init(context);
    }

    public ShowImageWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShowImageWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context=context;
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                responseWebLongClick(v);
                return false;
            }
        });

        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setDefaultTextEncodingName("UTF -8");

        //载入js
        this.addJavascriptInterface(new MyJavascriptInterface(context), "imagelistner");
        //获取 html
        this.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
    }

    /**
     * 响应长按点击事件
     * @param v
     */
    private void responseWebLongClick(View v) {
        if (v instanceof WebView) {
            HitTestResult result = ((WebView) v).getHitTestResult();
            if (result != null) {
                int type = result.getType();
                if (type == HitTestResult.IMAGE_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    longClickUrl = result.getExtra();
                    showDialog(longClickUrl);
                }
            }
        }
    }

    /**
     * 解析 HTML 该方法在 setWebViewClient 的 onPageFinished 方法中进行调用
     * @param view
     */
    public void parseHTML(WebView view) {
        view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    }

    /**
     * 长按 WebView 图片弹出 Dialog
     * @param url
     */
    private void showDialog(final String url) {
        new ActionSheetDialog(context)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(
                        "保存到相册",
                        ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                downloadImage(url);
                            }
                        }).show();
    }

    //下载图片
    private File bitmap=null;
    private void downloadImage(final String url) {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                      bitmap= ImageUtils.loadPictureForResult(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cab",url,100,100);
//                    ShowImageWebView.this.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(bitmap!=null){
//                                ToastManager.show("保存成功");
//                                sendBroadcast(bitmap);
//                            }else {
//                                ToastManager.show("保存失败，请检查网络连接");
//                            }
//                        }
//                    });

                }
            }).start();
    }

    /**
     * 发送广播更新相册
     * @param file
     */
    private void sendBroadcast(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     *   注入js函数监听
     *   该方法在 setWebViewClient 的 onPageFinished 方法中进行调用
     */
    public void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        this.loadUrl("javascript:(function(){" +
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
            //点击图片 url
            intent.putExtra(Constant.IMAGE_URL, img);
            //页面所以图片 url
            intent.putStringArrayListExtra(Constant.IMAGE_URL_ALL, (ArrayList<String>) listImgSrc);
            intent.setClass(context, ShowImageFromWebActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * 打印 Html 内容
     */
    final class InJavaScriptLocalObj {
        @android.webkit.JavascriptInterface
        public void showSource(String html) {
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
            listImgSrc.clear();
            while (matcher.find()) {
                listImgSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        return listImgSrc;
    }

}
