package view;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import activity.ShowImageFromWebActivity;
import utils.Constant;
import utils.ImageLoaderUtils;

/**
 * 可以实现点击图片进行保存的 WebView
 * Created by FZT on 2016/10/10.
 */
public class ShowImageWebView extends WebView {

    private List<String> listImgSrc = new ArrayList<>();
    // 获取img标签正则
    private static final String IMAGE_URL_TAG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMAGE_URL_CONTENT = "http:\"?(.*?)(\"|>|\\s+)";

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
                 setWebImageLongClickListener(v);
                return false;
            }
        });

        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setDefaultTextEncodingName("UTF -8");

        //载入js
        this.addJavascriptInterface(new MyJavascriptInterface(context), "imageListener");
        //获取 html
        this.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
    }

    /**
     * 响应长按点击事件
     * @param v
     */
    private void setWebImageLongClickListener(View v) {
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
     * 注入 js 函数监听，这段 js 函数的功能就是，遍历所有的图片，并添加 onclick 函数，实现点击事件，
     * 函数的功能是在图片点击的时候调用本地java接口并传递 url 过去
     */
    public void setImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        this.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imageListener.startShowImageActivity(this.src);  " +
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
        public void startShowImageActivity(String url) {
            Intent intent = new Intent();
            intent.putExtra(Constant.IMAGE_URL, url);
            intent.putStringArrayListExtra(Constant.IMAGE_URL_ALL, (ArrayList<String>) listImgSrc);
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
     * 从图片对应的地址对象中解析出 src 标签对应的内容,即 url
     * 例如 "http://sc1.hao123img.com/data/f44d0aab7bc35b8767de3c48706d429e"
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

    /**
     * 开始下载图片
     */
    private void downloadImage(String url) {
        ImageLoaderUtils.downLoadImage(url,Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImagesFromWebView",context);
    }

}
