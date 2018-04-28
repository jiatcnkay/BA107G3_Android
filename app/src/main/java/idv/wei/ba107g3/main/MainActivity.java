package idv.wei.ba107g3.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.activity.Event;
import idv.wei.ba107g3.activity.Gift;
import idv.wei.ba107g3.activity.Giftbox;
import idv.wei.ba107g3.activity.Home;
import idv.wei.ba107g3.activity.Search;
import idv.wei.ba107g3.activity.Talk;
import idv.wei.ba107g3.friends.FriendsListDAO;
import idv.wei.ba107g3.friends.FriendsListDAO_interface;
import idv.wei.ba107g3.friends.FriendsListFragment;
import idv.wei.ba107g3.member.LoginActivity;
import idv.wei.ba107g3.member.MemberDAO;
import idv.wei.ba107g3.member.MemberDAO_interface;
import idv.wei.ba107g3.member.MemberProfileActivity;
import idv.wei.ba107g3.member.MemberVO;
import idv.wei.ba107g3.talk.TalkActivity;

import static idv.wei.ba107g3.friends.FriendsListFragment.recyclerView_friendList;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Menu toolBarMenu;
    private MenuItem btnlogout, btnlogin, notify;
    private static final int REQUEST_LOGIN = 1;
    private RelativeLayout navi_layout;
    private ImageView logo;
    private MemberVO memberVO;
    private LocalBroadcastManager broadcastManager;
    private Dialog dialog;
    private Button btnRefuse, btnAgree, btnMem,btnback,btnGiftBox;
    private TextView dialog_name,dialog_gift_name,dialog_giftr_amount;
    private ImageView dialog_photo;
    private String geter;
    private TextToSpeech tts;
    private static final int REQ_TTS_DATA_CHECK = 1;

    @Override
    // TextToSpeech engine初始完畢會呼叫此方法, status: SUCCESS or ERROR.
    public void onInit(int status) {
        if (status == TextToSpeech.ERROR) {
            return;
        }
        // 取得手機現行地區語言，並檢測是否有支援該語言
        int available = tts.isLanguageAvailable(Locale.getDefault());
        // TextToSpeech.LANG_AVAILABLE代表支援該語言
        // LANG_NOT_SUPPORTED代表不支援
        if (available == TextToSpeech.LANG_NOT_SUPPORTED) {
            return;
        }

        // 雖然大部份Android手機都支援TTS，但最好還是檢查一下是否有安裝TTS檔案
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQ_TTS_DATA_CHECK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find();
        setNavigation();
        setToolbar();
        changeFragment(new Home());
        broadcastManager = LocalBroadcastManager.getInstance(this);
        registerFriendStateReceiver();
        registerSendGiftReceiver();
        registerGetOnlineMemReceiver();
        if (tts == null) {
            tts = new TextToSpeech(this, this);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                }
                @Override
                public void onError(String utteranceId) {
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.this.invalidateOptionsMenu();
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        String memJson = pref.getString("loginMem", "");
        if (memJson.length() != 0) {
            memberVO = new Gson().fromJson(memJson.toString(), MemberVO.class);
            showMember(memberVO);
            //ws://13.115.254.39:8081
            Util.talkconnectServer(memberVO.getMem_no(), this,"ws://192.168.0.10:8081/BA107G3/FriendWS/");
            Util.memberconnectServer(memberVO.getMem_no(), this, "ws://192.168.0.10:8081/BA107G3/MemberWS/");
            Util.SendGiftconnectServer(memberVO.getMem_no(), this, "ws://192.168.0.10:8081/BA107G3/GiftOrderServer/");
            Util.UpdateGiftconnectServer(memberVO.getMem_no(),this, "ws://192.168.0.10:8081/BA107G3/GiftStatusServer/");
        }
    }

    public void find() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
    }

    public void setNavigation() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setCheckable(false);
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navi_item_home:
                        changeFragment(new Home());
                        toolbar.setTitle("Toast");
                        break;
                    case R.id.navi_item_search:
                        Intent searchIntent = new Intent(MainActivity.this, Search.class);
                        startActivity(searchIntent);
                        break;
                    case R.id.navi_item_chat:
                        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                        if (!pref.getBoolean("login", false)) {
                            Toast.makeText(MainActivity.this, "請先登入", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(loginIntent, REQUEST_LOGIN);
                        } else {
                            Intent talkIntent = new Intent(MainActivity.this, Talk.class);
                            startActivity(talkIntent);
                        }
                        break;
                    case R.id.navi_item_shop:
                        Intent giftIntent = new Intent(MainActivity.this, Gift.class);
                        startActivity(giftIntent);
                        break;
                    case R.id.navi_item_gift:
                        SharedPreferences giftboxpref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                        if (!giftboxpref.getBoolean("login", false)) {
                            Toast.makeText(MainActivity.this, "請先登入", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(loginIntent, REQUEST_LOGIN);
                        } else {
                            Intent giftboxIntent = new Intent(MainActivity.this, Giftbox.class);
                            startActivity(giftboxIntent);
                        }
                        break;
                    case R.id.navi_item_event:
                        SharedPreferences eventpref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                        if (!eventpref.getBoolean("login", false)) {
                            Toast.makeText(MainActivity.this, "請先登入", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(loginIntent, REQUEST_LOGIN);
                        } else {
                            Intent eventIntent = new Intent(MainActivity.this, Event.class);
                            startActivity(eventIntent);
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void changeFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerview, fragment).commit();
    }

    private void setToolbar() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.notify:
                        Toast.makeText(MainActivity.this, memberVO.getMem_name(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnlogin:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnlogout:
                        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                        pref.edit().putBoolean("login", false)
                                .remove("account")
                                .remove("password")
                                .remove("loginMem")
                                .remove("advanced")
                                .remove("friendsList")
                                .remove("sendList")
                                .remove("AllEvent")
                                .apply();
                        for (int i = 0; i < Util.TALK.size(); i++) {
                            pref.edit().remove(Util.TALK.get(i).getMem_no()).apply();
                        }
                        btnlogout.setVisible(false);
                        btnlogin.setVisible(true);
                        notify.setVisible(false);
                        navi_layout = navigationView.getHeaderView(0).findViewById(R.id.navi_layout);
                        navi_layout.setVisibility(View.INVISIBLE);
                        logo = navigationView.getHeaderView(0).findViewById(R.id.logo);
                        logo.setVisibility(View.VISIBLE);
                        memberVO = null;
                        Util.CART = new ArrayList<>();
                        Util.TALK = new ArrayList<>();
                        Util.count = 0;
                        Util.disconnectServer();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        toolBarMenu = menu;
        btnlogin = toolBarMenu.findItem(R.id.btnlogin);
        btnlogout = toolBarMenu.findItem(R.id.btnlogout);
        notify = toolBarMenu.findItem(R.id.notify);
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        if (pref.getBoolean("login", false)) {
            btnlogout.setVisible(true);
            btnlogin.setVisible(false);
            notify.setVisible(true);
        } else {
            btnlogout.setVisible(false);
            btnlogin.setVisible(true);
            notify.setVisible(false);
        }
        return true;
    }

    public void showMember(MemberVO memberVO) {
        ImageView logo = navigationView.getHeaderView(0).findViewById(R.id.logo);
        ImageView memPhoto = navigationView.getHeaderView(0).findViewById(R.id.memPhoto);
        TextView memName = navigationView.getHeaderView(0).findViewById(R.id.memName);
        TextView memDeposit = navigationView.getHeaderView(0).findViewById(R.id.memDeposit);
        RelativeLayout navi_layout = navigationView.getHeaderView(0).findViewById(R.id.navi_layout);
        navi_layout.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        byte[] photo = memberVO.getMem_photo();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        memPhoto.setImageBitmap(bitmap);
        memName.setText(memberVO.getMem_name());
        memDeposit.setText(memberVO.getMem_deposit().toString() + getString(R.string.dollar));
    }

    private void registerGetOnlineMemReceiver() {
        IntentFilter distanceSearchFilter = new IntentFilter("sendSelf_distanceSearch");
        OnlineReceiver onlineReceiver = new OnlineReceiver();
        broadcastManager.registerReceiver(onlineReceiver,distanceSearchFilter);
    }

    private class OnlineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            JsonObject jsonObject = new Gson().fromJson(message, JsonObject.class);
            Log.e(TAG,"onlineMemList="+jsonObject);
        }
    }

    private void registerFriendStateReceiver() {
        IntentFilter addFilter = new IntentFilter("requestAdd");
        IntentFilter agreeFilter = new IntentFilter("respondAdd");
        AddFriendReceiver addFriendReceiver = new AddFriendReceiver();
        broadcastManager.registerReceiver(addFriendReceiver, addFilter);
        broadcastManager.registerReceiver(addFriendReceiver, agreeFilter);

    }

    private class AddFriendReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            JsonObject jsonObject = new Gson().fromJson(message, JsonObject.class);
            String type = jsonObject.get("type").getAsString();
            Log.e("type=",type);
            switch (type) {
                case "requestAdd":
                    String requestAdd_memSend = jsonObject.get("memSend").getAsString();
                    MemberSelect memberSelect = new MemberSelect();
                    memberSelect.execute(requestAdd_memSend);
                    geter = jsonObject.get("memGet").getAsString();
                    break;
                case "respondAdd":
                    String respondAdd_memSend= jsonObject.get("memSend").getAsString();
                    String respondAdd = respondAdd_memSend +" 同意你的好友邀請囉 : )";
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(respondAdd)
                            .setPositiveButton("好喔",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                    break;
                default:
                    break;
            }
        }
    }

    private void registerSendGiftReceiver() {
        IntentFilter sendGiftFilter = new IntentFilter("sendGift");
        SendGiftReceiver sendGiftReceiver = new SendGiftReceiver();
        broadcastManager.registerReceiver(sendGiftReceiver, sendGiftFilter);
    }

    private class SendGiftReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            JsonArray jsonArray = new Gson().fromJson(message, JsonArray.class);
            JsonObject receive = jsonArray.get(3).getAsJsonObject();
            String mem_no_self = receive.get("mem_no_self").getAsString();
            String giftr_amount = receive.get("giftr_amount").getAsString();
            String giftr_message = receive.get("giftr_message").getAsString();
            JsonObject gift = jsonArray.get(1).getAsJsonObject();
            String gift_name = gift.get("gift_name").getAsString();
            SendMember sendMember = new SendMember();
            sendMember.execute(mem_no_self,giftr_amount,gift_name,giftr_message);
        }
    }

    class MemberSelect extends AsyncTask<String, Void, MemberVO> {
        @Override
        protected MemberVO doInBackground(String... params) {
            MemberDAO_interface dao = new MemberDAO();
            return dao.getOneByMemNo(params[0]);
        }

        @Override
        protected void onPostExecute(MemberVO memberVO) {
            super.onPostExecute(memberVO);
            showAddMem(memberVO);
        }
    }

    class SendMember extends AsyncTask<String, Void, MemberVO> {
        private String giftr_amount,gift_name,giftr_message;
        @Override
        protected MemberVO doInBackground(String... params) {
            MemberDAO_interface dao = new MemberDAO();
            this.giftr_amount = params[1];
            this.gift_name = params[2];
            this.giftr_message = params[3];
            return dao.getOneByMemNo(params[0]);
        }

        @Override
        protected void onPostExecute(MemberVO memberVO) {
            super.onPostExecute(memberVO);
            showSendMsg(memberVO,giftr_amount,gift_name,giftr_message);
        }
    }

    private void showSendMsg(final MemberVO memberVO,String giftr_amount,String gift_name,String giftr_message) {
        String sendMsg = memberVO.getMem_name()+"送你"+giftr_amount+"個"+gift_name+giftr_message;
        startTTS(sendMsg);
        dialog = new Dialog(MainActivity.this);
        dialog.setTitle("SendGift");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_sendgift);
        Window dw = dialog.getWindow();
        WindowManager.LayoutParams lp = dw.getAttributes();
        lp.alpha = 1.0f;
        lp.width = 1000;
        lp.height = 1350;
        dw.setAttributes(lp);
        dialog_photo = dialog.findViewById(R.id.dialog_photo);
        dialog_name = dialog.findViewById(R.id.dialog_name);
        dialog_gift_name = dialog.findViewById(R.id.dialog_gift_name);
        dialog_giftr_amount = dialog.findViewById(R.id.dialog_giftr_amount);

        byte[] photo = memberVO.getMem_photo();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        dialog_photo.setImageBitmap(bitmap);
        dialog_name.setText(memberVO.getMem_name());
        dialog_gift_name.setText(gift_name);
        dialog_giftr_amount.setText(String.valueOf(giftr_amount)+"個");

        btnMem = dialog.findViewById(R.id.btnMem);
        btnMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemberProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("member", memberVO);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnback = dialog.findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btnGiftBox = dialog.findViewById(R.id.btnGiftBox);
        btnGiftBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giftboxIntent = new Intent(MainActivity.this, Giftbox.class);
                startActivity(giftboxIntent);
            }
        });
        dialog.show();
    }

    public void showAddMem(final MemberVO member) {
        dialog = new Dialog(MainActivity.this);
        dialog.setTitle("AddFriend");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_addfriend);
        Window dw = dialog.getWindow();
        WindowManager.LayoutParams lp = dw.getAttributes();
        lp.alpha = 1.0f;
        lp.width = 1000;
        lp.height = 1350;
        dw.setAttributes(lp);
        dialog_photo = dialog.findViewById(R.id.dialog_photo);
        dialog_name = dialog.findViewById(R.id.dialog_name);
        byte[] photo = member.getMem_photo();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        dialog_photo.setImageBitmap(bitmap);
        dialog_name.setText(member.getMem_name());

        btnMem = dialog.findViewById(R.id.btnMem);
        btnMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemberProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("member", member);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnAgree = dialog.findViewById(R.id.btnAgree);
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgreeFriend agreeFriend = new AgreeFriend();
                agreeFriend.execute(member.getMem_no(), geter);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", "respondAdd");
                jsonObject.addProperty("memSend", memberVO.getMem_name());
                jsonObject.addProperty("memGet", member.getMem_no());
                String agreeJson = new Gson().toJson(jsonObject);
                Util.memberWebSocketClient.send(agreeJson);
                dialog.cancel();
            }
        });

        btnRefuse = dialog.findViewById(R.id.btnRefuse);
        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefuseFriend refuseFriend = new RefuseFriend();
                refuseFriend.execute(member.getMem_no(), geter);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    class RefuseFriend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            dao.delete(params[0], params[1]);
            return null;
        }
    }

    class AgreeFriend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            dao.add(params[0], params[1]);
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_TTS_DATA_CHECK:
                // 如果沒有安裝TTS檔案，就必須引導user安裝
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL) {
                    Intent installIntent = new Intent();
                    // 會引導user到Play商店安裝
                    installIntent
                            .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
                break;
        }
    }

    private void startTTS(String text) {
        // 可以指定聲音串流類型，方便user設定(例如：大小聲)
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM,
                AudioManager.STREAM_MUSIC);
        Log.e("speak",text);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text);
    }
}

