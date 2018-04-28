package idv.wei.ba107g3.talk;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberVO;

public class TalkActivity extends AppCompatActivity {
    private static final String TAG = "TalkActivity";
    private static final int REQUEST_TAKE_PICTURE_LARGE = 0;
    private static final int REQUEST_PICK_PICTURE = 1;
    private static final int REQ_PERMISSIONS_STORAGE = 101;
    private static final int MAX_IMAGE_WIDTH = 640;
    private static final int MAX_IMAGE_HEIGHT = 1200;
    private LocalBroadcastManager broadcastManager;
    private EditText etMessage;
    private ScrollView scrollView;
    private LinearLayout layout;
    private ImageView ivCamera, ivPicture, ivSend,bigPicture;
    private Uri contentUri, croppedImageUri;
    private MemberVO geter, user;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;
    private TextView friend_name;
    private Dialog dialog;
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        find();
        //取得接收者
        Bundle bundle = getIntent().getExtras();
        geter = (MemberVO) bundle.getSerializable("member");
        friend_name.setText(geter.getMem_name());
        //取得傳送者
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        user = new Gson().fromJson(pref.getString("loginMem", ""), MemberVO.class);
        //取得歷史紀錄
        GetTalk getTalk = new GetTalk();
        getTalk.execute(user.getMem_no(), geter.getMem_no());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(TalkActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TalkActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void find() {
        etMessage = findViewById(R.id.etMessage);
        scrollView = findViewById(R.id.scrollView);
        friend_name = findViewById(R.id.friend_name);
        layout = findViewById(R.id.layout);
        ivCamera = findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                Uri contentUri = FileProvider.getUriForFile(TalkActivity.this, getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if (isIntentAvailable(TalkActivity.this, intent)) {
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE_LARGE);
                }
            }
        });
        ivPicture = findViewById(R.id.ivPicture);
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_PICTURE);
            }
        });
        ivSend = findViewById(R.id.ivSend);
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                if (message.trim().isEmpty()) {
                    Util.showMessage(TalkActivity.this, "填個訊息吧 :)");
                    return;
                }
                showMessage(message, false);
                // 將輸入的訊息清空
                etMessage.setText(null);
                // 將欲傳送的對話訊息轉成JSON後送出
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", "sendMessage");
                jsonObject.addProperty("memSend", user.getMem_no());
                jsonObject.addProperty("memGet", geter.getMem_no());
                java.text.DateFormat df = new java.text.SimpleDateFormat();
                String formatDate = df.format(new java.util.Date());
                jsonObject.addProperty("date", formatDate);
                jsonObject.addProperty("message", message);
                jsonObject.addProperty("sendType", "text");
                String chatMessageJson = new Gson().toJson(jsonObject);
                Util.chatWebSocketClient.send(chatMessageJson);
            }
        });
    }

    private class GetTalk extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TalkActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            TalkDAO_interface dao = new TalkDAO();
            return dao.findTalkByFriends(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.cancel();
            if (!result.equals("no message")) {
                JsonArray jsonArray = new Gson().fromJson(result, JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    String memSend = jsonObject.get("memSend").getAsString();
                    String sendType = jsonObject.get("sendType").getAsString();
                    if("text".equals(sendType)) {
                        String message = jsonObject.get("message").getAsString();
                        if (memSend.equals(user.getMem_no())) {
                            showMessage(message, false);
                        } else {
                            showMessage(message, true);
                        }
                    }else if("pic".equals(sendType)){
                        String message = jsonObject.get("message").getAsString();
                        byte[] photo = Base64.decode(message, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(photo,0,photo.length);
                        if (memSend.equals(user.getMem_no())) {
                            showImage(bitmap, false);
                        } else {
                            showImage(bitmap, true);
                        }
                    }
                }
            }
            //取得監聽器
            broadcastManager = LocalBroadcastManager.getInstance(TalkActivity.this);
            registerFriendStateReceiver();
        }
    }

    private void registerFriendStateReceiver() {
        IntentFilter getOneTalkFilter = new IntentFilter("getOneTalk");
        IntentFilter sendMessageFilter = new IntentFilter("getNewMessage");
        IntentFilter sendImgFilter = new IntentFilter("getNewImg");
        TalkReceiver talkReceiver = new TalkReceiver();
        broadcastManager.registerReceiver(talkReceiver, getOneTalkFilter);
        broadcastManager.registerReceiver(talkReceiver, sendMessageFilter);
        broadcastManager.registerReceiver(talkReceiver, sendImgFilter);
    }


    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bitmap picture = null;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE_LARGE:
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    // inSampleSize值即為縮放的倍數 (數字越大縮越多)
                    opt.inSampleSize = getImageScale(file.getPath());
                    picture = BitmapFactory.decodeFile(file.getPath(), opt);
                    showImage(picture,false);
                    break;
                case REQUEST_PICK_PICTURE:
                    // 從媒體庫裡選取圖片
                    Uri uri = intent.getData();
                    String[] columns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, columns,
                            null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String imagePath = cursor.getString(0);
                        cursor.close();
                        BitmapFactory.Options opt2 = new BitmapFactory.Options();
                        opt2.inSampleSize = getImageScale(imagePath);
                        picture = BitmapFactory.decodeFile(imagePath, opt2);
                        showImage(picture,false);
                    }
                    break;
            }
            String message = Base64.encodeToString(bitmapToPNG(picture), Base64.DEFAULT);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "sendImg");
            jsonObject.addProperty("memSend", user.getMem_no());
            jsonObject.addProperty("memGet", geter.getMem_no());
            java.text.DateFormat df = new java.text.SimpleDateFormat();
            String formatDate = df.format(new java.util.Date());
            jsonObject.addProperty("date", formatDate);
            jsonObject.addProperty("message", message);
            jsonObject.addProperty("sendType", "pic");
            String chatMessageJson = new Gson().toJson(jsonObject);
            Util.chatWebSocketClient.send(chatMessageJson);
            Log.d(TAG, "output: " + chatMessageJson);
        }
    }

    /*
 * options.inJustDecodeBounds取得原始圖片寬度與高度資訊 (但不會在記憶體裡建立實體)
 * 當輸出寬與高超過自訂邊長邊寬最大值，scale設為2 (寬變1/2，高變1/2)
 */
    private int getImageScale(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int scale = 1;
        while (options.outWidth / scale >= MAX_IMAGE_WIDTH ||
                options.outHeight / scale >= MAX_IMAGE_HEIGHT) {
            scale *= 2;
        }
        return scale;
    }

    private byte[] bitmapToPNG(Bitmap srcBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 轉成PNG不會失真，所以quality參數值會被忽略
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // 接收到聊天訊息會在TextView呈現
    private class TalkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonMsg = intent.getStringExtra("message");
            JsonObject jsonObject = new Gson().fromJson(jsonMsg, JsonObject.class);
            String type = jsonObject.get("type").getAsString();
            if("getNewMessage".equals(type)) {
                String memSend = jsonObject.get("memSend").getAsString();
                String message = jsonObject.get("message").getAsString();
                // 接收到聊天訊息，若發送者與目前聊天對象相同，就顯示訊息
                if (memSend.equals(geter.getMem_no())) {
                    showMessage(message, true);
                }
            }
            if("getNewImg".equals(type)){
                String memSend = jsonObject.get("memSend").getAsString();
                String message = jsonObject.get("message").getAsString();
                 byte[] photo = Base64.decode(message, Base64.DEFAULT);
                 Bitmap bitmap = BitmapFactory.decodeByteArray(photo,0,photo.length);
                // 接收到聊天訊息，若發送者與目前聊天對象相同，就顯示訊息
                if (memSend.equals(geter.getMem_no())) {
                    showImage(bitmap, true);
                }
            }
        }
    }

    /**
     * 將文字訊息呈現在畫面上
     * <p>
     * //@param sender  發訊者
     *
     * @param message 訊息內容
     * @param// left    true代表訊息要貼在左邊(發訊者為他人)，false代表右邊(發訊者為自己)
     */
    private void showMessage(String message, Boolean left) {
        String text = message;
        View view;
        if (left) {
            view = View.inflate(this, R.layout.message_left, null);
            ImageView friendPhoto = view.findViewById(R.id.friendPhoto);
            byte[] photo = geter.getMem_photo();
            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            friendPhoto.setImageBitmap(Util.getCircleBitmap(bitmap, 300));
        } else {
            view = View.inflate(this, R.layout.message_right, null);
        }
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(text);
        view.setPadding(0, 15, 0, 15);
        layout.addView(view);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void showImage(final Bitmap bitmap, boolean left) {
        View view;
        if (left) {
            view = View.inflate(this, R.layout.image_left, null);
            ImageView friendPhoto = view.findViewById(R.id.friendPhoto);
            byte[] photo = geter.getMem_photo();
            Bitmap bitmap_photo = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            friendPhoto.setImageBitmap(Util.getCircleBitmap(bitmap_photo, 300));
        } else {
            view = View.inflate(this, R.layout.image_right, null);
        }
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        view.setPadding(0, 15, 0, 15);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(TalkActivity.this);
                dialog.setTitle("showImg");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_image);
                Window dw = dialog.getWindow();
                WindowManager.LayoutParams lp = dw.getAttributes();
                lp.alpha = 1.0f;
                lp.width = 1000;
                lp.height = 1000;
                dw.setAttributes(lp);
                bigPicture = dialog.findViewById(R.id.bigPicture);
                bigPicture.setImageBitmap(bitmap);
                dialog.show();
            }
        });
        layout.addView(view);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ivCamera.setEnabled(true);
                    ivPicture.setEnabled(true);
                } else {
                    ivCamera.setEnabled(false);
                    ivPicture.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

}
