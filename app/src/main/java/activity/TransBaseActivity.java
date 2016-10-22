package activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Email  : bigbigpeng3@gmail.com
 * Author : peng zhang
 */
public abstract class TransBaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //把设置布局文件的操作交给继承的子类
        setContentView(getLayoutResId());
    }

    abstract protected int getLayoutResId();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
