package com.fourmob.colorpicker.sample;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;

/**
 * Created by bailey on 15. 9. 9..
 */
public class FirstStartActivity extends Activity {

    private ViewPager mPager;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_start);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ActionBar.LayoutParams.WRAP_CONTENT;


        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);

    }

    private View.OnClickListener mCloseButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int infoFirst = 1;
            SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
            SharedPreferences.Editor editor = a.edit();
            editor.putInt("First", infoFirst);
            editor.commit();
            finish();
        }
    };

    /**
     * PagerAdapter
     */
    class PagerAdapterClass extends PagerAdapter {

        private LayoutInflater mInflater;

        public PagerAdapterClass(Context c) {
            super();
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            View v = null;

            if (position == 0) {
                v = mInflater.inflate(R.layout.firststartview1, null);
                v.findViewById(R.id.fsv_one);
            } else if (position == 1) {
                v = mInflater.inflate(R.layout.firststartview2, null);
                v.findViewById(R.id.fsv_two);
            } else if (position == 2) {
                v = mInflater.inflate(R.layout.firststartview3, null);
                v.findViewById(R.id.fsv_three);
                v.findViewById(R.id.close).setOnClickListener(mCloseButtonClick);
            }
            ((ViewPager) pager).addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager) pager).removeView((View) view);
        }


        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

//        public void startUpdate(View arg0) {
//        }
//
//        public void finishUpdate(View arg0) {
//        }


    }

}

