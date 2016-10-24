package api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * description：OkHttpClient
 * author：pz
 * data：2016/10/24
 */
public class OKHttpFactory {
private static final int TIMEOUT_READ = 50;
    private static final int TIMEOUT_CONNECTION = 50;

    private static OKHttpFactory okHttpFactory = null;
    private OkHttpClient okHttpClient = null;

    private OKHttpFactory() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                //失败重连
                .retryOnConnectionFailure(true)
                //time out
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 得到okHttpFactory实例
     *
     * @return okHttpFactory
     */
    public static OKHttpFactory getInstance() {
        if (okHttpFactory == null) {
            synchronized (OKHttpFactory.class) {
                if (okHttpFactory == null) {
                    okHttpFactory = new OKHttpFactory();
                }
            }
        }
        return okHttpFactory;
    }

    /**
     * 得到okhttpClient实例
     *
     * @return okhttpClient
     */
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }





}
