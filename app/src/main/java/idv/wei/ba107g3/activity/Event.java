package idv.wei.ba107g3.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.event.EventFragment;
import idv.wei.ba107g3.event_participants.Event_participantsFragment;

public class Event extends AppCompatActivity {
    private TabLayout tablayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        tablayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyEventAdapter(getSupportFragmentManager()));
        tablayout.setupWithViewPager(viewPager);
    }

    class MyEventAdapter extends FragmentPagerAdapter {

        public MyEventAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    return new EventFragment();
                case 1 :
                    return new Event_participantsFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return "活動專區";
                case 1 :
                    return "我的活動";
            }
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

