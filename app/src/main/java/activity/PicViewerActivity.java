package activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.peng3.big.big.activity.R;

import java.util.ArrayList;
import java.util.Collections;

import fragment.PictureSlideFragment;
import view.PhotoViewViewPager;

/**Simple TouchGallery demo based on ViewPager and Photoview.
 * Created by Trojx on 2016/1/3.
 */
public class PicViewerActivity extends AppCompatActivity {

    private PhotoViewViewPager viewPager;
    private TextView tv_indicator;
//    private ArrayList<String> urlList;


    private ArrayList<String> imgUrls;
    private String url;
    private int currentIndex;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_viewer);

        String [] urls={"http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_0.jpg",
                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_1.jpg",
                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_2.jpg",
                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_3.jpg",
                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_4.jpg",
                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_5.jpg",
                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_6.jpg",};
        imgUrls=getIntent().getStringArrayListExtra(MainActivity.URL_ALL);
        url=getIntent().getStringExtra("image");
        int position=imgUrls.indexOf(url);
//        urlList = new ArrayList<>();
//        Collections.addAll(urlList, urls);

        viewPager = (PhotoViewViewPager) findViewById(R.id.viewpager);
        tv_indicator = (TextView) findViewById(R.id.tv_indicator);

        viewPager.setAdapter(new PictureSlidePagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tv_indicator.setText(String.valueOf(position+1)+"/"+imgUrls.size());
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(position);

    }

    private  class PictureSlidePagerAdapter extends FragmentStatePagerAdapter{

        public PictureSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PictureSlideFragment.newInstance(imgUrls.get(position));
        }

        @Override
        public int getCount() {
            return imgUrls.size();
        }
    }
}
