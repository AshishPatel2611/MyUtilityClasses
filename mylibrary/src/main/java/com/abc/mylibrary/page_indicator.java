package com.abc.mylibrary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by hexagon on 4/7/17.
 */

public class page_indicator extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    protected View view;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
    private int[] mImageResources = {
            R.drawable.example_picture,
            R.drawable.example_picture,
            R.drawable.example_picture,
            R.drawable.example_picture,
            R.drawable.example_picture
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.activity_how_it_works_info, null);
        setContentView(view);

        intro_images = (ViewPager) findViewById(R.id.viewpager_how_it_works);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots_in_viewpager_dialog);

        mAdapter = new ViewPagerAdapter(this, mImageResources);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewController();


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.deselected_item_drawable));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_drawable));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.deselected_item_drawable));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_drawable));

    }

    public class ViewPagerAdapter extends PagerAdapter {
        private Activity mContext;
        private int[] mResources;
        FragmentManager fm;
        private static final String TAG = "ViewPagerAdapter";

        public ViewPagerAdapter(Activity mContext, int[] mResources) {
            this.mContext = mContext;
            this.mResources = mResources;
        }

        public ViewPagerAdapter(Activity mContext, int[] mResources, FragmentManager fm) {
            this.mContext = mContext;
            this.mResources = mResources;
            this.fm = fm;
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.raw_item_viewpager, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
            Log.i(TAG, "instantiateItem: " + mContext);
       /* if (!((mContext instanceof HomeDetailsActivity))) {
            Log.d(TAG, "instantiateItem: ");
            imageView.setAdjustViewBounds(true);
        }*/
            imageView.setImageResource(mResources[position]);
            container.addView(itemView);


            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }


    }
}