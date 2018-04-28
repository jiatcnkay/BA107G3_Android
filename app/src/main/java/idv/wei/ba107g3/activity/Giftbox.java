package idv.wei.ba107g3.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import idv.wei.ba107g3.giftbox.GiftboxDAO;
import idv.wei.ba107g3.giftbox.GiftboxDAO_interface;
import idv.wei.ba107g3.giftbox.GiftboxDetailActivity;
import idv.wei.ba107g3.giftbox.GiftboxVO;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberVO;


public class Giftbox extends AppCompatActivity {
    private RecyclerView giftbox_recyclerView;
    private ProgressDialog progressDialog;
    private List<GiftboxVO> giftboxList = new ArrayList<>();
    private List<GiftboxVO> giftboxDetailList = new ArrayList<>();
    private List<GiftVO> allGiftList = new ArrayList<>();
    private TextView noGift;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftbox);
        noGift = findViewById(R.id.noGift);
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        allGiftList = new Gson().fromJson(pref.getString("allGift", "").toString(), new TypeToken<List<GiftVO>>() {
        }.getType());
        MemberVO memberVO = new Gson().fromJson(pref.getString("loginMem",""), MemberVO.class);
        GetMemGift getMemGift = new GetMemGift();
        getMemGift.execute(memberVO.getMem_no());
    }

    private class GetMemGift extends AsyncTask<String, Void, List<GiftboxVO>> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Giftbox.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<GiftboxVO> doInBackground(String... strings) {
            GiftboxDAO_interface dao = new GiftboxDAO();
            return dao.getByMemGift(strings[0]);
        }

        @Override
        protected void onPostExecute(List<GiftboxVO> giftboxes) {
            super.onPostExecute(giftboxes);
            progressDialog.cancel();

            for(GiftboxVO giftboxVO : giftboxes){
                GiftboxVO giftboxVO1 = new GiftboxVO();
                giftboxVO1.setGift_no(giftboxVO.getGift_no());
                giftboxVO1.setGiftr_amount(giftboxVO.getGiftr_amount());
                giftboxVO1.setMem_no_self(giftboxVO.getMem_no_self());
                giftboxDetailList.add(giftboxVO1);
            }

            giftbox_recyclerView = findViewById(R.id.giftbox_recyclerView);
            giftbox_recyclerView.setHasFixedSize(true);
            if(giftboxes.size()==0 || giftboxes==null){
                noGift.setVisibility(View.VISIBLE);
                giftbox_recyclerView.setVisibility(View.INVISIBLE);
                return;
            }
            for (int i = 0; i < giftboxes.size(); i++) {
                int index = giftboxList.indexOf(giftboxes.get(i));
                if (index == -1) {
                    giftboxList.add(giftboxes.get(i));
                } else {
                    GiftboxVO giftboxVO = giftboxList.get(index);
                    giftboxVO.setGiftr_amount(giftboxVO.getGiftr_amount() + giftboxes.get(i).getGiftr_amount());
                }
            }
            giftbox_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            giftbox_recyclerView.setAdapter(new GiftboxAdapter(Giftbox.this));
        }
    }

    private class GiftboxAdapter extends RecyclerView.Adapter<GiftboxAdapter.ViewHolder> {
        private Context context;

        public GiftboxAdapter(Context context) {
            this.context = context;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView gift_pic;
            private TextView giftr_amount,gift_name;
            private CardView cardview_giftbox;

            public ViewHolder(View itemView) {
                super(itemView);
                gift_pic = itemView.findViewById(R.id.gift_pic);
                giftr_amount = itemView.findViewById(R.id.giftr_amount);
                gift_name= itemView.findViewById(R.id.gift_name);
                cardview_giftbox = itemView.findViewById(R.id.cardview_giftbox);
            }
        }

        @Override
        public int getItemCount() {
            return giftboxList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(context).inflate(R.layout.cardview_giftbox, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewholder, int position) {
            final Bundle bundle = new Bundle();
            final GiftboxVO giftboxVO = giftboxList.get(position);
            for(GiftVO giftVO : allGiftList){
                if(giftboxVO.getGift_no().equals(giftVO.getGift_no())){
                    byte[] photo = giftVO.getGift_pic();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photo,0,photo.length);
                    viewholder.gift_pic.setImageBitmap(bitmap);
                    viewholder.gift_name.setText(giftVO.getGift_name());
                    bundle.putSerializable("gift",giftVO);
                }
            }
            viewholder.giftr_amount.setText(String.valueOf(giftboxVO.getGiftr_amount()));
            viewholder.cardview_giftbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.putInt("giftr_moumnt",giftboxVO.getGiftr_amount());
                    bundle.putString("giftboxList",new Gson().toJson(giftboxDetailList));
                    Intent intent = new Intent(Giftbox.this, GiftboxDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }
}
