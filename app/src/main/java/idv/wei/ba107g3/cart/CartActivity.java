package idv.wei.ba107g3.cart;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.gift.GiftVO;
import idv.wei.ba107g3.gift_discount.GiftDiscountFragment;
import idv.wei.ba107g3.gift_discount.GiftDiscountVO;
import idv.wei.ba107g3.gift_order.GiftOrderDAO;
import idv.wei.ba107g3.gift_order.GiftOrderDAO_interface;
import idv.wei.ba107g3.gift_order.GiftOrderVO;
import idv.wei.ba107g3.gift_order_detail.GiftOrderDetailVO;
import idv.wei.ba107g3.gift_receive.GiftReceiveVO;
import idv.wei.ba107g3.main.MainActivity;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.LoginActivity;
import idv.wei.ba107g3.member.MemberDAO;
import idv.wei.ba107g3.member.MemberDAO_interface;
import idv.wei.ba107g3.member.MemberVO;

import static idv.wei.ba107g3.gift.GiftFragment.recyclerView_gift;
import static idv.wei.ba107g3.gift_discount.GiftDiscountFragment.recyclerView_giftdiscount;

//import static idv.wei.ba107g3.gift.GiftFragment.recyclerView_gift;
//import static idv.wei.ba107g3.gift_discount.GiftDiscountFragment.recyclerView_giftdiscount;

public class CartActivity extends AppCompatActivity {
    public RecyclerView recyclerview_cart;
    private TextView btn_coupons, total_amount, total_gift_first_money, coupons_money, total_gift_last_money;
    private Button btncheckout;
    private List<GiftDiscountVO> giftDlist;
    private List<GiftDiscountVO> checkoutGiftList;
    private ArrayList<GiftReceiveVO> receiveList = new ArrayList<>();
    private String mem_no_self;
    private String mem_account;
    private MemberSelect memberSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        find();
        recyclerview_cart.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerview_cart.setAdapter(new CartAdapter(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //取得有限時優惠的商品LIST
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, Context.MODE_PRIVATE);
        if (pref.getBoolean("login", false)) {
            MemberVO member = new Gson().fromJson(pref.getString("loginMem", "").toString(), MemberVO.class);
            mem_no_self = member.getMem_no();
            mem_account = member.getMem_account();
        }
        giftDlist = GiftDiscountFragment.giftDlist;
        Iterator<GiftDiscountVO> iterator = giftDlist.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getGiftd_amount() == 0)
                iterator.remove();
        }
        getPrice();
    }

    private void find() {
        btn_coupons = findViewById(R.id.btn_coupons);
        total_amount = findViewById(R.id.total_amount);
        total_amount.setText(String.valueOf(Util.count));
        total_gift_first_money = findViewById(R.id.total_gift_first_money);
        coupons_money = findViewById(R.id.coupons_money);
        total_gift_last_money = findViewById(R.id.total_gift_last_money);
        btncheckout = findViewById(R.id.btncheckout);
        recyclerview_cart = findViewById(R.id.recyclerview_gift);
        recyclerview_cart.setHasFixedSize(true);
    }


    public void getPrice() {
        int amount = 0, price = 0, coupons = 0, total = 0;
        for (int i = 0; i < Util.CART.size(); i++) {
            GiftVO giftVO = Util.CART.get(i);
            amount = giftVO.getGift_buy_qty();
            price = giftVO.getGift_price();
            coupons = Integer.parseInt(coupons_money.getText().toString());
            //是否為折價商品，如是的話價格折價
            for (int j = 0; j < giftDlist.size(); j++) {
                if (giftVO.getGift_no().equals(giftDlist.get(j).getGift_no())) {
                    price = (int) (price * (giftDlist.get(j).getGiftd_percent()));
                    break;
                }
            }
            total += (price * amount);
        }
        total_gift_first_money.setText("$" + String.valueOf(total));
        total_gift_last_money.setText("$" + String.valueOf(total + coupons));
    }

    public void oncheckout(View view) {
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        if (!pref.getBoolean("login", false)) {
            Toast.makeText(CartActivity.this, "請先登入", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(CartActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            return;
        }
        //結帳前先去確認優惠名單是否跟先前一樣
//        checkoutGiftList = new Gson().fromJson(pref.getString("giftDlist", "").toString(), new TypeToken<List<GiftDiscountVO>>() {
//        }.getType());
        checkoutGiftList = GiftDiscountFragment.giftDlist;
        Iterator<GiftDiscountVO> checkoutIterator = checkoutGiftList.iterator();
        while(checkoutIterator.hasNext()){
            if(checkoutIterator.next().getGiftd_amount()==0)
                checkoutIterator.remove();
        }

        if(giftDlist.size()!=checkoutGiftList.size()) {
            //如果名單有異動就會執行這個動作，而不會直接進行結帳動作
                giftDlist = checkoutGiftList;
                recyclerview_cart.getAdapter().notifyDataSetChanged();
                getPrice();
                showResult("部分商品優惠結束，請確認價格後再結帳", 0);
                return;
        }
            giftDlist = checkoutGiftList;

        //設定訂單
        GiftOrderVO giftOrderVO = new GiftOrderVO();
        giftOrderVO.setMem_no(mem_no_self);
        giftOrderVO.setCoup_no(null);
        giftOrderVO.setGifto_remark(null);

        //設定訂單明細及禮物收贈禮明細
        List<GiftOrderDetailVO> goDetailList = new ArrayList<>();
        for (int i = 0; i < Util.CART.size(); i++) {
            int price = Util.CART.get(i).getGift_price();
            GiftOrderDetailVO giftOrderDetailVO = new GiftOrderDetailVO();
            giftOrderDetailVO.setGift_no(Util.CART.get(i).getGift_no());
            giftOrderDetailVO.setGiftod_unit(price);
            Log.e("price",String.valueOf(giftOrderDetailVO.getGiftod_unit()));
            giftOrderDetailVO.setGiftod_amount(Util.CART.get(i).getGift_buy_qty());
            giftOrderDetailVO.setGiftod_money(price * (Util.CART.get(i).getGift_buy_qty()));
            Log.e("money",String.valueOf(giftOrderDetailVO.getGiftod_money()));
            giftOrderDetailVO.setGiftod_inventory(0);
            for (int j = 0; j < giftDlist.size(); j++) {
                //找優惠商品
                if (Util.CART.get(i).getGift_no().equals(giftDlist.get(j).getGift_no())) {
                    Log.e("in??","in??");
                    Double percent = giftDlist.get(j).getGiftd_percent() * 100;
                    price = (int) (price * (percent / 100));
                    giftOrderDetailVO.setGiftod_unit(price);
                    giftOrderDetailVO.setGiftd_no(giftDlist.get(j).getGiftd_no());
                    giftOrderDetailVO.setGiftod_amount(Util.CART.get(i).getGift_buy_qty());
                    giftOrderDetailVO.setGiftod_money(price * (Util.CART.get(i).getGift_buy_qty()));
                }
            }
            goDetailList.add(giftOrderDetailVO);
            Log.e("tag",String.valueOf(giftOrderDetailVO.getGiftod_money()));
        }

        for(GiftReceiveVO giftReceiveVO : receiveList){
            if(giftReceiveVO.getMem_no_self()==null)
                giftReceiveVO.setMem_no_self(mem_no_self);
            Log.e("tag", "ReceiveVO=" +  giftReceiveVO.getGift_no());
            Log.e("tag", "ReceiveVO=" +  giftReceiveVO.getGiftr_amount());
            Log.e("tag", "ReceiveVO=" +  giftReceiveVO.getMem_no_self());
            Log.e("tag", "ReceiveVO=" +  giftReceiveVO.getMem_no_other());
        }

        String jsonGiftOrderVO = new Gson().toJson(giftOrderVO);
        String jsonGiftOrderDetailVOList = new Gson().toJson(goDetailList);
        String jsonGiftReceiveList = new Gson().toJson(receiveList);
        GiftOrder giftOrder = new GiftOrder();
        giftOrder.execute(jsonGiftOrderVO, jsonGiftOrderDetailVOList, jsonGiftReceiveList);
    }

    private class GiftOrder extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            GiftOrderDAO_interface dao = new GiftOrderDAO();
            return dao.insert(strings[0], strings[1], strings[2]);
        }

        @Override
        protected void onPostExecute(String back) {
            super.onPostExecute(back);
            Gson gson = new Gson();
            String result = back;
            Log.e("tag","result="+result);
            if(result.equals("金額不足") || result.equals("結帳成功")) {
                switch (result) {
                    case "金額不足":
                        showResult("點數不足，請先加值",0);
                        break;
                    case "結帳成功":
                        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                        memberSelect = new MemberSelect();
                        try {
                            MemberVO member = memberSelect.execute(mem_account).get();
                            pref.edit().putString("loginMem",new Gson().toJson(member)).apply();
                        } catch (InterruptedException |ExecutionException e) {
                            e.printStackTrace();
                        }
                        showResult("結帳成功",1);
                        break;
                }
            }else {
                Type listType = new TypeToken<List<GiftDiscountVO>>() {
                }.getType();
                List<GiftDiscountVO> giftDiscountVOS = gson.fromJson(back,listType);
                Log.e("tag","list="+giftDiscountVOS);
                if (giftDiscountVOS != null) {
                    SharedPreferences pref = CartActivity.this.getSharedPreferences(Util.PREF_FILE, Context.MODE_PRIVATE);
                    pref.edit().putString("giftDlist", new Gson().toJson(giftDiscountVOS)).apply();
                    StringBuilder sb = new StringBuilder();
                    sb.append("因以下幾種原因:\n");
                    for (int i = 0; i < Util.CART.size(); i++) {
                        for (int j = 0; j < giftDiscountVOS.size(); j++) {
                            if (giftDiscountVOS.get(j).getGift_no().equals(Util.CART.get(i).getGift_no()) && giftDiscountVOS.get(j).getGiftd_amount() < Util.CART.get(i).getGift_buy_qty()) {
                                if (giftDiscountVOS.get(j).getGiftd_amount() != 0) {
                                    sb.append((i + 1) + ". " + Util.CART.get(i).getGift_name() + ":\n此優惠商品剩餘數量 : " + giftDiscountVOS.get(j).getGiftd_amount() + "\n\n");
                                    Util.CART.get(i).setGift_buy_qty(giftDiscountVOS.get(j).getGiftd_amount());

                                } else {
                                    sb.append((i + 1) + ". " + Util.CART.get(i).getGift_name() + ":\n此優惠商品已完售，故恢復成原價\n\n");
                                    giftDiscountVOS.remove(giftDiscountVOS.get(j));
                                }
                            }
                        }
                    }
                    sb.append("請麻煩確認數量及價格後，再進行購買，Thanks~");
                    recyclerView_giftdiscount.getAdapter().notifyDataSetChanged();
                    recyclerView_gift.getAdapter().notifyDataSetChanged();
                    showResult(sb.toString(), 0);
                }
            }
        }
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        private Context context;

        public CartAdapter(Context context) {
            this.context = context;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView gift_pic, less_amount, add_amount, remove_gift;
            private TextView gift_name, gift_amount, gift_price;
            private LinearLayout add_person_layout;
            private Button btn_add_person;


            public ViewHolder(View itemView) {
                super(itemView);
                gift_pic = itemView.findViewById(R.id.gift_pic);
                less_amount = itemView.findViewById(R.id.less_amount);
                add_amount = itemView.findViewById(R.id.add_amount);
                remove_gift = itemView.findViewById(R.id.remove_gift);
                gift_name = itemView.findViewById(R.id.gift_name);
                gift_amount = itemView.findViewById(R.id.gift_amount);
                gift_price = itemView.findViewById(R.id.gift_price);
                add_person_layout = itemView.findViewById(R.id.add_person_layout);
                btn_add_person = itemView.findViewById(R.id.btn_add_person);

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(context).inflate(R.layout.cardview_cart, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            int amount = 0, price = 0;
            final GiftVO giftVO = Util.CART.get(position);
            byte[] pic = giftVO.getGift_pic();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            viewHolder.gift_pic.setImageBitmap(bitmap);
            viewHolder.gift_name.setText(giftVO.getGift_name());
            viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());

            //設定價格
            price = giftVO.getGift_price();
            amount = giftVO.getGift_buy_qty();
            viewHolder.gift_price.setText("$" + String.valueOf(price * amount));
            //是否為折價商品，如是的話價格折價
            for (int i = 0; i < giftDlist.size(); i++) {
                if (giftVO.getGift_no().equals(giftDlist.get(i).getGift_no())) {
                    price = (int) (price * giftDlist.get(i).getGiftd_percent());
                    viewHolder.gift_price.setText("$" + String.valueOf(price * amount));
                }
            }

            //數量增加會改變價格
            viewHolder.add_amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.less_amount.setEnabled(true);
                    giftVO.setGift_buy_qty(giftVO.getGift_buy_qty() + 1);
                    viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());
                    //無限時優惠商品設定
                    int price = giftVO.getGift_price();
                    int amount = giftVO.getGift_buy_qty();
                    viewHolder.gift_price.setText("$" + String.valueOf(price * amount));
                    //有限時優惠商品設定
                    for (int i = 0; i < giftDlist.size(); i++) {
                        if (giftVO.getGift_no().equals(giftDlist.get(i).getGift_no())) {
                            //假如購買數量大於優惠商品限購數量時
                            if (giftVO.getGift_buy_qty() > giftDlist.get(i).getGiftd_amount()) {
                                Util.showMessage(CartActivity.this, getString(R.string.over_amount));
                                viewHolder.add_amount.setEnabled(false);
                                giftVO.setGift_buy_qty(giftVO.getGift_buy_qty() - 1);
                                viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());
                            }
                            amount = giftVO.getGift_buy_qty();
                            Double percent = giftDlist.get(i).getGiftd_percent();
                            price = (int) (price * percent);
                            int backprice = (price * amount);
                            viewHolder.gift_price.setText("$" + String.valueOf(backprice));
                        }
                    }
                    getPrice();
                }
            });

            //數量減少會改變價格
            viewHolder.less_amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.add_amount.setEnabled(true);
                    int amount = 0;
                    if (receiveList.size() != 0) {
                        for (int i = 0; i < receiveList.size(); i++) {
                            if (giftVO.getGift_no().equals(receiveList.get(i).getGift_no()))
                                amount += receiveList.get(i).getGiftr_amount();
                        }
                        if (giftVO.getGift_buy_qty() == amount) {
                            viewHolder.less_amount.setEnabled(false);
                            Util.showMessage(CartActivity.this, "選購數量不得低於贈送數量");
                            return;
                        }
                    }
                    if (giftVO.getGift_buy_qty() < 2) {
                        viewHolder.less_amount.setEnabled(false);
                        Util.showMessage(CartActivity.this, "選購數量不得小於1");
                    } else {
                        giftVO.setGift_buy_qty(giftVO.getGift_buy_qty() - 1);
                        viewHolder.gift_amount.setText(giftVO.getGift_buy_qty().toString());
                        int lessprice = (giftVO.getGift_price() * giftVO.getGift_buy_qty());
                        viewHolder.gift_price.setText("$" + String.valueOf(lessprice));
                        for (int i = 0; i < giftDlist.size(); i++) {
                            if (giftVO.getGift_no().equals(giftDlist.get(i).getGift_no())) {
                                lessprice = (int) (lessprice * giftDlist.get(i).getGiftd_percent());
                                viewHolder.gift_price.setText("$" + String.valueOf(lessprice));
                            }
                        }
                        getPrice();
                    }
                }
            });

            //增加送禮對象
            viewHolder.btn_add_person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int amount = 0;
                    if (receiveList.size() != 0) {
                        for (int i = 0; i < receiveList.size(); i++) {
                            if (giftVO.getGift_no().equals(receiveList.get(i).getGift_no())) {
                                amount += receiveList.get(i).getGiftr_amount();
                            }
                        }
                    }
                    //取得送禮名單
                    if (receiveList.size() == 0 || (amount < giftVO.getGift_buy_qty())) {
                        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                        Type listType = new TypeToken<List<MemberVO>>() {
                        }.getType();
                        final List<MemberVO> List = new Gson().fromJson(pref.getString("sendList", "").toString(), listType);
                        final List<String> sendList = new ArrayList<>();
                        if (List != null) {
                            for (MemberVO member : List) {
                                sendList.add(member.getMem_name());
                            }
                            //製作下拉式送禮名單
                            final Spinner namespinner = new Spinner(CartActivity.this);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_item, sendList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            namespinner.setAdapter(adapter);
                            namespinner.setSelection(0, true);

                            //製作下拉式贈送數目
                            String[] number = {"請選擇", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
                            final Spinner numspinner = new Spinner(CartActivity.this);
                            ArrayAdapter<String> numadapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_item, number);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            numspinner.setAdapter(numadapter);
                            numspinner.setSelection(0, true);
                            numspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    int buy_amount = 0;
                                    if (position != 0) {
                                        for (int i = 0; i < receiveList.size(); i++) {
                                            if (giftVO.getGift_no().equals(receiveList.get(i).getGift_no())) {
                                                buy_amount += receiveList.get(i).getGiftr_amount();
                                            }
                                        }
                                        int amount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                                        if (amount > giftVO.getGift_buy_qty() || (buy_amount + amount) > giftVO.getGift_buy_qty()) {
                                            Util.showMessage(CartActivity.this, "不得超過購買數量");
                                            numspinner.setSelection(0, true);
                                            return;
                                        }
                                    }
                                    if (position != 0 && namespinner.getSelectedItemPosition() != 0) {
                                        Util.showMessage(CartActivity.this, "請勾選左邊空格確認");
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                            //移除按鈕
                            ImageView close = new ImageView(CartActivity.this);
                            close.setImageResource(R.drawable.ic_close);

                            //確認按鈕
                            final ImageView confirm = new ImageView(CartActivity.this);
                            confirm.setBackgroundResource(R.drawable.not_confirm);
                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (namespinner.getSelectedItemPosition() == 0 || numspinner.getSelectedItemPosition() == 0) {
                                        Util.showMessage(CartActivity.this, "請完成右邊選項");
                                    } else {
                                        int buy_amount = 0;
                                        for (int i = 0; i < receiveList.size(); i++) {
                                            if (giftVO.getGift_no().equals(receiveList.get(i).getGift_no())) {
                                                buy_amount += receiveList.get(i).getGiftr_amount();
                                            }
                                        }
                                        int amount = Integer.parseInt(numspinner.getSelectedItem().toString());
                                        if (amount > giftVO.getGift_buy_qty() || (buy_amount + amount) > giftVO.getGift_buy_qty()) {
                                            Util.showMessage(CartActivity.this, "不得超過購買數量");
                                            numspinner.setSelection(0, true);
                                            return;
                                        }
                                        confirm.setBackgroundResource(R.drawable.confirm);
                                        namespinner.setEnabled(false);
                                        numspinner.setEnabled(false);
                                        confirm.setEnabled(false);
                                        String mem_no_other = List.get(namespinner.getSelectedItemPosition()).getMem_no();
                                        addReceive(giftVO, mem_no_other, amount);
                                    }
                                }
                            });

                            //把剛剛新增的view放到這個新增的layout
                            final LinearLayout linearLayout = new LinearLayout(CartActivity.this);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            viewHolder.add_person_layout.addView(linearLayout);
                            linearLayout.addView(confirm);
                            linearLayout.addView(namespinner);
                            linearLayout.addView(numspinner);
                            linearLayout.addView(close);

                            //等layout設置好後，建立移除按鈕監聽器
                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (namespinner.getSelectedItemPosition() != 0 && numspinner.getSelectedItemPosition() != 0) {
                                        int amount = Integer.parseInt(numspinner.getSelectedItem().toString());
                                        String mem_no_other = List.get(namespinner.getSelectedItemPosition()).getMem_no();
                                        lessReceive(giftVO, mem_no_other, amount);
                                    }
                                    viewHolder.less_amount.setEnabled(true);
                                    linearLayout.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Util.showMessage(CartActivity.this, "請先登入");
                            Intent loginIntent = new Intent(CartActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
                    }
                }
            });

            //移除商品
            viewHolder.remove_gift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = "確定要移除此商品嗎?";
                    new AlertDialog.Builder(CartActivity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Iterator<GiftReceiveVO> iterator = receiveList.iterator();
                                            while (iterator.hasNext()) {
                                                if (giftVO.getGift_no().equals(iterator.next().getGift_no()))
                                                    iterator.remove();
                                            }
                                            Util.CART.remove(giftVO);
                                            Util.count--;
                                            total_amount.setText(String.valueOf(Util.count));
                                            getPrice();
                                            recyclerview_cart.getAdapter().notifyDataSetChanged();
                                        }
                                    })

                            .setNegativeButton("返回",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.cancel();
                                        }
                                    }).setCancelable(false).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return Util.CART.size();
        }
    }

    public void addReceive(GiftVO giftVO, String mem_no_other, int amount) {
        GiftReceiveVO giftReceiveVO = new GiftReceiveVO();
        giftReceiveVO.setGift_no(giftVO.getGift_no());
        giftReceiveVO.setMem_no_self(mem_no_self);
        giftReceiveVO.setMem_no_other(mem_no_other);
        int index = receiveList.indexOf(giftReceiveVO);
        //找不到為-1表示第一次加入
        if (index == -1) {
            giftReceiveVO.setGiftr_amount(amount);
            receiveList.add(giftReceiveVO);
        } else {
            GiftReceiveVO orderReceive = receiveList.get(index);
            int order_amount = orderReceive.getGiftr_amount();
            orderReceive.setGiftr_amount(order_amount + amount);
        }
    }

    public void lessReceive(GiftVO giftVO, String mem_no_other, int amount) {
        GiftReceiveVO giftReceiveVO = new GiftReceiveVO();
        giftReceiveVO.setGift_no(giftVO.getGift_no());
        giftReceiveVO.setMem_no_self(mem_no_self);
        giftReceiveVO.setMem_no_other(mem_no_other);
        int index = receiveList.indexOf(giftReceiveVO);
        if (index == -1)
            return;
        GiftReceiveVO orderReceive = receiveList.get(index);
        int order_amount = orderReceive.getGiftr_amount();
        if (order_amount - amount == 0) {
            receiveList.remove(orderReceive);
            return;
        }
        orderReceive.setGiftr_amount(order_amount - amount);
        Log.e("tag", "ReceiveVO=" + orderReceive.getGift_no());
        Log.e("tag", "ReceiveVO=" + orderReceive.getGiftr_amount());
        Log.e("tag", "ReceiveVO=" + orderReceive.getMem_no_self());
        Log.e("tag", "ReceiveVO=" + orderReceive.getMem_no_other());
    }

    public void showResult(String message, int i) {
        if (i == 1) {
            new AlertDialog.Builder(CartActivity.this)
                    .setIcon(R.drawable.cart)
                    .setTitle(message)
                    .setCancelable(false)
                    .setPositiveButton("繼續購物",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    Util.CART = new ArrayList<>();
                                    Util.count = 0;
                                    finish();
                                }
                            })
                    .setNegativeButton("返回首頁",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    Util.CART = new ArrayList<>();
                                    Util.count = 0;
                                    finish();
                                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            }).show();
        } else {
            new AlertDialog.Builder(CartActivity.this)
                    .setIcon(R.drawable.cart)
                    .setTitle("結帳失敗")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("返回結帳頁面",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    finish();
                                    Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            }).show();
        }
    }

    class MemberSelect extends AsyncTask<String, Void, MemberVO> {
        @Override
        protected MemberVO doInBackground(String... params) {
            MemberDAO_interface dao = new MemberDAO();
            return dao.getOneByAccount(params[0]);
        }
    }
}
