package idv.wei.ba107g3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.cart.CartActivity;
import idv.wei.ba107g3.gift.GiftFragment;
import idv.wei.ba107g3.gift_discount.GiftDiscountFragment;
import idv.wei.ba107g3.main.Util;

public class Gift extends AppCompatActivity {
    private TabLayout tablayout;
    private ViewPager viewPager;
    public static TextView count_cart;
    private ImageView cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gift_tab_layout);
        tablayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
        count_cart = findViewById(R.id.count_cart);
        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Util.CART.size()!=0) {
                    Intent intent = new Intent(Gift.this, CartActivity.class);
                    startActivity(intent);
                }else
                    Util.showMessage(Gift.this,"購物車目前是空的，請先購物");
            }
        });
        viewPager.setAdapter(new MySearchAdapter(getSupportFragmentManager()));
        tablayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Util.count==0)
            count_cart.setVisibility(View.INVISIBLE);
        else {
            count_cart.setVisibility(View.VISIBLE);
            count_cart.setText(String.valueOf(Util.count));
        }
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
                    return new GiftDiscountFragment();
                case 1 :
                    return new GiftFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return "限時優惠";
                case 1 :
                    return "全部禮物";
            }
            return null;
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
