package utils;

import android.app.Application;
import android.content.Context;

/**
 * Description:
 * Author：pz
 * Date：2016/10/24:15:14
 */
public class SystemApplication extends Application{
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
         context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

}
