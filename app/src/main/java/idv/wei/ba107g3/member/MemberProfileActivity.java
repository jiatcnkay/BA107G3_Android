package idv.wei.ba107g3.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.activity.Gift;
import idv.wei.ba107g3.friends.FriendsListDAO;
import idv.wei.ba107g3.friends.FriendsListDAO_interface;
import idv.wei.ba107g3.main.Util;

public class MemberProfileActivity extends AppCompatActivity {
    private ImageView memPhoto;
    private TextView memAge, memGenger, memCounty, memIntro, memHeight, memWeight, memBloodType, memInterest, memContact, memEmotion;
    private MemberVO member,user;
    private CollapsingToolbarLayout toolbar_layout;
    private Toolbar toolbar;
    private FloatingActionButton addfab, giftfab, alreadyfab;
    private List<MemberVO> friendList = new ArrayList<>();
    private static final int REQUEST_LOGIN = 1;
    private String mem_no_self;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberprofile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        find();
        show();
        giftfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                if (!pref.getBoolean("login", false)) {
                    List<MemberVO> list = new ArrayList<>();
                    MemberVO noOne = new MemberVO();
                    noOne.setMem_name("請選擇");
                    list.add(noOne);
                    list.add(member);
                    pref.edit().putString("sendList",new Gson().toJson(list)).apply();
                }else {
                    String jsonSendList = pref.getString("sendList","");
                    Type listType = new TypeToken<List<MemberVO>>() {
                    }.getType();
                    List<MemberVO> sendList = new Gson().fromJson(jsonSendList.toString(),listType);
                    List<MemberVO> list = new ArrayList<>();
                    MemberVO noOne = new MemberVO();
                    noOne.setMem_name("請選擇");
                    list.add(noOne);
                    for(MemberVO memberVO : sendList){
                        list.add(memberVO);
                    }
                    if(!sendList.contains(member)){
                        list.add(member);
                    }
                    pref.edit().putString("sendList",new Gson().toJson(list)).apply();
                }
                Intent intent = new Intent(MemberProfileActivity.this, Gift.class);
                startActivity(intent);
            }
        });
        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                if (!pref.getBoolean("login", false)) {
                    Toast.makeText(MemberProfileActivity.this, "請先登入", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(MemberProfileActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent,REQUEST_LOGIN);
                } else {
                    String memJson = pref.getString("loginMem", "");
                    user = new Gson().fromJson(memJson.toString(), MemberVO.class);
                    mem_no_self = user.getMem_no();
                    InsertFriend insertFriend = new InsertFriend();
                    insertFriend.execute(mem_no_self, member.getMem_no());
                    addfab.setVisibility(View.INVISIBLE);
                    alreadyfab.setVisibility(View.VISIBLE);
                    Util.showMessage(MemberProfileActivity.this,getString(R.string.sendadd));
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "sendAdd");
                    jsonObject.addProperty("memSend", user.getMem_no());
                    jsonObject.addProperty("memGet", member.getMem_no());
                    String addJson = new Gson().toJson(jsonObject);
                    Util.memberWebSocketClient.send(addJson);
                }
            }
        });
        alreadyfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MemberProfileActivity.this, getString(R.string.alreadyadd), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
            if (pref.getBoolean("login", false)) {
                String memJson = pref.getString("loginMem", "");
                mem_no_self = new Gson().fromJson(memJson.toString(), MemberVO.class).getMem_no();
                HaveWait haveWait = new HaveWait();
                if (haveWait.execute(mem_no_self, member.getMem_no()).get()) {
                    addfab.setVisibility(View.INVISIBLE);
                    alreadyfab.setVisibility(View.VISIBLE);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void find() {
        memPhoto = findViewById(R.id.memPhoto);
        memAge = findViewById(R.id.memAge);
        memGenger = findViewById(R.id.memGender);
        memCounty = findViewById(R.id.memCounty);
        memIntro = findViewById(R.id.memIntro);
        memHeight = findViewById(R.id.memHeight);
        memWeight = findViewById(R.id.memWeight);
        memBloodType = findViewById(R.id.memBloodType);
        memInterest = findViewById(R.id.memInterest);
        memContact = findViewById(R.id.memContact);
        memEmotion = findViewById(R.id.memEmotion);
        toolbar_layout = findViewById(R.id.toolbar_layout);
        giftfab = findViewById(R.id.giftfab);
        addfab = findViewById(R.id.addfab);
        alreadyfab = findViewById(R.id.alreadyfab);
    }

    private void show() {
        Bundle bundle = getIntent().getExtras();
        member = (MemberVO) bundle.getSerializable("member");

        SharedPreferences pref = this.getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        MemberVO memberVO = new Gson().fromJson(pref.getString("loginMem", ""), MemberVO.class);
        if (memberVO != null) {
                SharedPreferences friendListpref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                String json = friendListpref.getString("friendsList", "");
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MemberVO>>() {
                }.getType();
                friendList = gson.fromJson(json.toString(), listType);
            if (friendList != null) {
                SharedPreferences sendpref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                sendpref.edit().putString("sendList", gson.toJson(friendList)).apply();
                for (int i = 0; i < friendList.size(); i++) {
                    if (friendList.get(i).getMem_account().equals(member.getMem_account())) {
                        addfab.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }

        byte[] photo = member.getMem_photo();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        memPhoto.setImageBitmap(bitmap);
        toolbar_layout.setTitle(member.getMem_name());
        memGenger.setText(member.getMem_gender());
        memAge.setText(Util.getAge(member.getMem_age()));
        memCounty.setText(member.getMem_county());
        memIntro.setText(member.getMem_intro());
        memHeight.setText(getString(R.string.memheight) + (member.getMem_height().toString()));
        memWeight.setText(getString(R.string.memWeight) + member.getMem_weight().toString());
        memBloodType.setText(getString(R.string.memBloodType) + member.getMem_bloodtype());
        memInterest.setText(getString(R.string.memInterest) + member.getMem_interest());
        memContact.setText(getString(R.string.memContact) + member.getMem_contact());
        memEmotion.setText(getString(R.string.memEmotion) + member.getMem_emotion());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_LOGIN){
            if(resultCode==RESULT_OK)
                show();
        }
    }

    class HaveWait extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            return dao.havewait(params[0], params[1]);
        }
    }

    class InsertFriend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            dao.insert(params[0], params[1]);
            return null;
        }
    }

    private byte[] bitmapToPNG(Bitmap srcBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 轉成PNG不會失真，所以quality參數值會被忽略
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}





