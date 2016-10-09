package com.peng3.big.big.showzoomableimagefromwebview;


import okhttp3.OkHttpClient;

/**
 * Email  : bigbigpeng3@gmail.com
 * Author : peng zhang
 */
public class OkHttpUtil {

    private static OkHttpUtil mInstance;

    private OkHttpClient mOkHttpClient;

    private OkHttpUtil() {
        mOkHttpClient = new OkHttpClient();
    }

    public static OkHttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtil();
                }
            }
        }
        return mInstance;
    }

    public OkHttpClient getOkHttpClient(){
        if (mOkHttpClient == null){
            return mOkHttpClient;
        }
        return new OkHttpClient();
    }

    /**
     * 封装的方法放在下面
     */



}