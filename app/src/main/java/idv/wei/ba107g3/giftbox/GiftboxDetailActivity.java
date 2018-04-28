package idv.wei.ba107g3.giftbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.gift.GiftVO;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberProfileActivity;
import idv.wei.ba107g3.member.MemberVO;

public class GiftboxDetailActivity extends AppCompatActivity {
    private RecyclerView giftboxdetail_recyclerView;
    private List<GiftboxVO> giftboxDetailList = new ArrayList<>();
    private List<GiftboxVO> showgiftboxList = new ArrayList<>();
    private List<MemberVO> aLLMem = new ArrayList<>();
    private GiftVO gift;
    private int giftr_amount;
    private TextView gift_name_gift_amount;
    private ImageView gift_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftboxdetail);
        gift_name_gift_amount = findViewById(R.id.gift_name_gift_amount);
        gift_pic = findViewById(R.id.gift_pic);

        Bundle bundle = getIntent().getExtras();
        gift = (GiftVO) bundle.getSerializable("gift");
        giftr_amount = (int) bundle.getInt("giftr_moumnt");
        gift_name_gift_amount.setText("收到" + String.valueOf(giftr_amount) + "個" + gift.getGift_name());
        byte[] photo = gift.getGift_pic();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        gift_pic.setImageBitmap(bitmap);

        String jsonList = (String) bundle.getString("giftboxList");
        giftboxDetailList = new Gson().fromJson(jsonList.toString(), new TypeToken<List<GiftboxVO>>() {
        }.getType());

        for (GiftboxVO giftboxVO : giftboxDetailList) {
            if (giftboxVO.getGift_no().equals(gift.getGift_no())) {
                GiftboxVO giftboxVO1 = new GiftboxVO();
                giftboxVO1.setMem_no_self(giftboxVO.getMem_no_self());
                giftboxVO1.setGiftr_amount(giftboxVO.getGiftr_amount());
                showgiftboxList.add(giftboxVO1);
            }
        }

        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        aLLMem = new Gson().fromJson(pref.getString("allMem", "").toString(), new TypeToken<List<MemberVO>>() {
        }.getType());
        giftboxdetail_recyclerView = findViewById(R.id.giftboxdetail_recyclerView);
        giftboxdetail_recyclerView.setHasFixedSize(true);
        giftboxdetail_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        giftboxdetail_recyclerView.setAdapter(new GiftboxDetailAdapter(GiftboxDetailActivity.this));
    }

    private class GiftboxDetailAdapter extends RecyclerView.Adapter<GiftboxDetailAdapter.ViewHolder> {
        private Context context;

        public GiftboxDetailAdapter(Context context) {
            this.context = context;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mem_photo;
            private TextView giftboxdetail_amount, mem_name;
            private CardView cardview_giftboxdetail;

            public ViewHolder(View itemView) {
                super(itemView);
                mem_photo = itemView.findViewById(R.id.mem_photo);
                giftboxdetail_amount = itemView.findViewById(R.id.giftboxdetail_amount);
                mem_name = itemView.findViewById(R.id.mem_name);
                cardview_giftboxdetail = itemView.findViewById(R.id.cardview_giftboxdetail);
            }
        }

        @Override
        public int getItemCount() {
            return showgiftboxList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(context).inflate(R.layout.cardview_giftboxdetail, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewholder, int position) {
            GiftboxVO giftboxVO = showgiftboxList.get(position);
            viewholder.giftboxdetail_amount.setText(String.valueOf(giftboxVO.getGiftr_amount()));
            for (final MemberVO memberVO : aLLMem) {
                if (giftboxVO.getMem_no_self().equals(memberVO.getMem_no())) {
                    viewholder.mem_name.setText(memberVO.getMem_name());
                    byte[] photo = memberVO.getMem_photo();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                    viewholder.mem_photo.setImageBitmap(bitmap);
                    viewholder.cardview_giftboxdetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(GiftboxDetailActivity.this, MemberProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("member", memberVO);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }
}
