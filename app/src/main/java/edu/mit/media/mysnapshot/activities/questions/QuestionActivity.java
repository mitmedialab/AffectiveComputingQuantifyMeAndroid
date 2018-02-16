package edu.mit.media.mysnapshot.activities.questions;

import android.support.v4.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.mysnapshot.R;
import edu.mit.media.mysnapshot.view.ScrollPageIndicator;


public abstract class QuestionActivity extends AppCompatActivity {

    ViewPager viewPager;
    public ScreenSlidePagerAdapter pagerAdapter;
    public boolean isBuildingData;
    boolean isSetupPhase = false;


    protected abstract int getLayoutId();
    protected abstract void initFragments(List<Fragment> fragments, List<Drawable> icons);
    protected abstract boolean loadInitialData();
    public abstract void onFinish();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestFeatures();

        super.onCreate(savedInstanceState);

        isSetupPhase = true;

        setContentView(getLayoutId());

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        isBuildingData = ! loadInitialData();

        List<Fragment> fragments = new ArrayList<Fragment>();
        List<Drawable> icons = new ArrayList<Drawable>();

        initFragments(fragments, icons);

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(fragments.size()); // smoother scrolling, easier data access to each fragment, more memory usage
        viewPager.setAdapter(pagerAdapter);

        // we force the loading of the fragments so we can get smoother loading of more intense fragments, like ones that use the camera
        for (int i = 0; i < fragments.size(); i++) {
            pagerAdapter.instantiateItem(viewPager, i);
        }
        pagerAdapter.finishUpdate(viewPager);

        ScrollPageIndicator indicator = (ScrollPageIndicator) findViewById(R.id.pageIndicator);
        indicator.setViewPager(viewPager, pagerAdapter.getTotalCount());
        indicator.setIconDrawables(icons);

        pagerAdapter.setActiveCount(pagerAdapter.getTotalCount());

        View backButton = findViewById(R.id.backbutton);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        isSetupPhase = false;

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // this has to be delayed till after attachToWindow, so the viewPager populates all fragments
        if (isBuildingData) {
            pagerAdapter.setActiveCount(1);
        }
    }

    protected void requestFeatures() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onPause() {
        scrollHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    public void onPageComplete() {
        onPageComplete(false);
    }

    public void onPageComplete(boolean forceSlide) {
        if (isSetupPhase) {
            return;
        }
        if (isBuildingData) {
            ScreenSlidePagerAdapter adapter = ((ScreenSlidePagerAdapter)viewPager.getAdapter());

            if (adapter.getTotalCount() == viewPager.getCurrentItem() + 1) {
                onFinish();
            } else {
                if (adapter.getActiveCount() == viewPager.getCurrentItem() + 1) {
                    adapter.setActiveCount(adapter.getActiveCount() + 1);
                    waitThenSlidePage();
                } else if (forceSlide) {
                    waitThenSlidePage();
                }
            }
        }
    }

    Handler scrollHandler = new Handler();

    public void waitThenSlidePage() {
        scrollHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                viewPager.arrowScroll(View.FOCUS_RIGHT);
            }

        }, 150);
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public List<Fragment> fragments;


        public int getActiveCount() {
            return activeCount;
        }

        public void setActiveCount(int activeCount) {
            this.activeCount = activeCount;
            notifyDataSetChanged();
        }

        public int getTotalCount() {
            return fragments.size();
        }

        private int activeCount;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
            activeCount = fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return Math.min(activeCount, getTotalCount());
        }

        @Override
        public int getItemPosition(Object object) {
            return fragments.indexOf(object);
        }


    }


    public boolean isBuildingData() {
        return isBuildingData;
    }
}
