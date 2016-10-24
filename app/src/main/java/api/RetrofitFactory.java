package api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.Constant;

/**
 * description： Retrofit
 * author：pz
 * data：2016/10/24
 */
public class RetrofitFactory {
    private static RetrofitFactory retrofitFactory = null;
    private Retrofit retrofit = null;

    private RetrofitFactory() {
        retrofit = new Retrofit.Builder()
                //设置OKHttpClient
                .client(OKHttpFactory.getInstance().getOkHttpClient())
                //gson转化器
                .addConverterFactory(GsonConverterFactory.create())
                //rx转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //baseUrl
                .baseUrl(Constant.BASE_URL)
                .build();
    }

    /**
     * 得到retorfitFactory实例
     *
     * @return retorfitFactory
     */
    public static RetrofitFactory getInstance() {
        if (retrofitFactory == null) {
            synchronized (RetrofitFactory.class) {
                if (retrofitFactory == null) {
                    retrofitFactory = new RetrofitFactory();
                }
            }
        }
        return retrofitFactory;
    }

    /**
     * 得到retorfit实例
     *
     * @return retorfit
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
