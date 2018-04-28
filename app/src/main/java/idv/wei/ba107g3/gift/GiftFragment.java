package idv.wei.ba107g3.gift;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.activity.Gift;
import idv.wei.ba107g3.gift_discount.GiftDiscountVO;
import idv.wei.ba107g3.main.Util;

import static android.content.Context.MODE_PRIVATE;
import static idv.wei.ba107g3.main.Util.CART;

public class GiftFragment extends Fragment {
    private EditText keyword;
    private Button btnsearch;
    public static RecyclerView recyclerView_gift;
    private TextView count_gift;
    private List<GiftVO> giftVO = new ArrayList<>();
   // private List<GiftDiscountVO> Util.GIFTDLIST = new ArrayList<>();
    private LocalBroadcastManager broadcastManager;
    private List<GiftDiscountVO> giftDList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_gift, null);
        keyword = view.findViewById(R.id.keyword);
        count_gift = view.findViewById(R.id.count_gift);
        btnsearch = view.findViewById(R.id.btnsearch);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyWord = keyword.getText().toString();
                if (keyWord.isEmpty()) {
                    Util.showMessage(getActivity(), getString(R.string.keyinkeyword));
                } else {
                    GetALLGift getALLGift = new GetALLGift();
                    getALLGift.execute(keyWord);
                }
            }
        });
        recyclerView_gift = view.findViewById(R.id.recyclerview_gift);
        recyclerView_gift.setHasFixedSize(true);
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        registerUpdateGiftReceiver();
        GetALLGift getALLGift = new GetALLGift();
        getALLGift.execute();
        return view;
    }

    private void registerUpdateGiftReceiver() {
        IntentFilter deleteFilter = new IntentFilter("deleteGift");
        IntentFilter insertFilter = new IntentFilter("insertGift");
        IntentFilter giftDFilter = new IntentFilter("giftD");
        GiftDReceiver giftDReceiver = new GiftDReceiver();
        UpdateGiftReceiver updateGiftReceiver = new UpdateGiftReceiver();
        broadcastManager.registerReceiver(updateGiftReceiver, deleteFilter);
        broadcastManager.registerReceiver(updateGiftReceiver, insertFilter);
        broadcastManager.registerReceiver(giftDReceiver , giftDFilter);
    }

    private class GiftDReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            giftDList = new ArrayList<>();
            String giftList = intent.getStringExtra("giftList");
            giftDList = new Gson().fromJson(giftList.toString(),new TypeToken<List<GiftDiscountVO>>(){}.getType());
        }
    }

    private class UpdateGiftReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            JsonArray jsonArray = new Gson().fromJson(message, JsonArray.class);
            JsonObject jsonAction = jsonArray.get(0).getAsJsonObject();
            String action = jsonAction.get("action").getAsString();
            switch (action) {
                case "deleteGift":
                    GetALLGift getALLGift = new GetALLGift();
                    getALLGift.execute();
                    break;
                case "insertGift":
                    GetALLGift getALLGift1 = new GetALLGift();
                    getALLGift1.execute();
                    break;
            }
        }
    }


    class GetALLGift extends AsyncTask<String, Void, List<GiftVO>> {

        @Override
        protected List<GiftVO> doInBackground(String... strings) {
            GiftDAO_interface dao = new GiftDAO();
            if (strings.length == 0) {
                giftVO = dao.getAll();
                SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                pref.edit().putString("allGift",new Gson().toJson(giftVO)).apply();
            } else {
                giftVO = dao.getByKeyWord(strings[0]);
            }
            return giftVO;
        }

        @Override
        protected void onPostExecute(List<GiftVO> list) {
            super.onPostExecute(list);
            count_gift.setText("共" + String.valueOf(list.size()) + "筆商品");
            recyclerView_gift.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            recyclerView_gift.setAdapter(new GiftAdapter());
        }
    }


    private class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView gift_pic;
            private TextView gift_name, gift_price, gift_oldprice, textamount, gift_amount;
            private Button btnadd, gift_percent;


            public ViewHolder(View itemView) {
                super(itemView);
                gift_pic = itemView.findViewById(R.id.gift_pic);
                gift_name = itemView.findViewById(R.id.gift_name);
                gift_price = itemView.findViewById(R.id.gift_price);
                gift_oldprice = itemView.findViewById(R.id.gift_oldprice);
                textamount = itemView.findViewById(R.id.textamount);
                gift_amount = itemView.findViewById(R.id.gift_amount);
                btnadd = itemView.findViewById(R.id.btnadd);
                gift_percent = itemView.findViewById(R.id.gift_percent);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(getContext()).inflate(R.layout.cardview_gift, parent, false);
            return new ViewHolder(itemview);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
//            SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, Context.MODE_PRIVATE);
//            String json = pref.getString("Util.GIFTDLIST", "");
//            Util.GIFTDLIST = new Gson().fromJson(json.toString(), new TypeToken<List<GiftDiscountVO>>() {
//            }.getType());
            Iterator<GiftDiscountVO> iterator = giftDList.iterator();
            while(iterator.hasNext()){
                if(iterator.next().getGiftd_amount()==0)
                    iterator.remove();
            }
            final GiftVO gift = giftVO.get(position);
            byte[] gift_pic = gift.getGift_pic();
            Bitmap bitmap = BitmapFactory.decodeByteArray(gift_pic, 0, gift_pic.length);
            viewHolder.gift_pic.setImageBitmap(bitmap);
            viewHolder.gift_name.setText(gift.getGift_name());
            viewHolder.gift_price.setTextColor(Color.BLACK);
            viewHolder.gift_price.setText("$" + gift.getGift_price().toString());
            viewHolder.gift_percent.setVisibility(View.INVISIBLE);
            viewHolder.gift_oldprice.setVisibility(View.INVISIBLE);
            viewHolder.textamount.setVisibility(View.INVISIBLE);
            viewHolder.gift_amount.setVisibility(View.INVISIBLE);
            for (int i = 0; i < giftDList.size(); i++) {
                if (gift.getGift_no().equals(giftDList.get(i).getGift_no())) {
                    //設定折價數
                    viewHolder.gift_percent.setVisibility(View.VISIBLE);
                    Double percent = giftDList.get(i).getGiftd_percent() * 100;
                    if (percent % 10 == 0) {
                        int intpercent = (int) (giftDList.get(i).getGiftd_percent() * 10);
                        viewHolder.gift_percent.setText(String.valueOf(intpercent) + "折");
                    } else {
                        percent /= 10;
                        viewHolder.gift_percent.setText(String.valueOf(percent) + "折");
                    }
                    //設定價格
                    viewHolder.gift_oldprice.setVisibility(View.VISIBLE);
                    int price = gift.getGift_price();
                    viewHolder.gift_oldprice.setText("$" + String.valueOf(price));
                    viewHolder.gift_oldprice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    price = (int) (price * (percent/100));
                    viewHolder.gift_price.setTextColor(Color.RED);
                    viewHolder.gift_price.setText("$" + String.valueOf(price));
                    //設定數量
                    viewHolder.textamount.setVisibility(View.VISIBLE);
                    viewHolder.gift_amount.setVisibility(View.VISIBLE);
                    viewHolder.gift_amount.setText(giftDList.get(i).getGiftd_amount().toString());
                }
            }

            //設定加入購物車動作
            viewHolder.btnadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = CART.indexOf(gift);
                    //找不到為-1表示第一次加入
                    if (index == -1) {
                        gift.setGift_buy_qty(1);
                        CART.add(gift);
                        Gift.count_cart.setVisibility(View.VISIBLE);
                        Gift.count_cart.setText(String.valueOf(++Util.count));
                    } else {
                        GiftVO orderGift = CART.get(index);
                        for (int i = 0; i < giftDList.size(); i++) {
                            if (orderGift.getGift_no().equals(giftDList.get(i).getGift_no())) {
                                if((orderGift.getGift_buy_qty() + 1)>giftDList.get(i).getGiftd_amount()){
                                    orderGift.setGift_buy_qty(orderGift.getGift_buy_qty() - 1);
                                    Util.showMessage(getContext(),getString(R.string.over_amount));
                                    break;
                                }
                            }
                        }
                        orderGift.setGift_buy_qty(orderGift.getGift_buy_qty() + 1);
                    }
                    String text = "";
                    for (GiftVO orderGift : CART) {
                        text += "\n- " + orderGift.getGift_name() + " x "
                                + orderGift.getGift_buy_qty();
                    }
                    String message = getString(R.string.current_buy) + text;
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.cart)
                            .setMessage(message)
                            .setPositiveButton(getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return giftVO.size();
        }
    }
}
