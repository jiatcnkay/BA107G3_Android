package idv.wei.ba107g3.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import java.util.ArrayList;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.friends.FriendsListFragment;
import idv.wei.ba107g3.main.MainActivity;
import idv.wei.ba107g3.talk.TalkFragment;

public class Talk extends AppCompatActivity {
    private TabLayout tablayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        tablayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MySearchAdapter(getSupportFragmentManager()));
        tablayout.setupWithViewPager(viewPager);
    }

    class MySearchAdapter extends FragmentPagerAdapter {

        public MySearchAdapter(FragmentManager fm) {
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
                    return new FriendsListFragment();
                case 1 :
                    return new TalkFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return "好友名單";
                case 1 :
                    return "即時聊天";
            }
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FriendsListFragment.onlineList = new ArrayList<>();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

