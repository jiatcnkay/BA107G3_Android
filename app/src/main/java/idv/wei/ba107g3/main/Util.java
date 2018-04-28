package idv.wei.ba107g3.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import idv.wei.ba107g3.gift.GiftVO;
import idv.wei.ba107g3.gift_discount.GiftDiscountVO;
import idv.wei.ba107g3.member.MemberVO;
import idv.wei.ba107g3.ws.ChatWebSocketClient;
import idv.wei.ba107g3.ws.DistanceSearchWebSocketClient;
import idv.wei.ba107g3.ws.MemberWebSocketClient;
import idv.wei.ba107g3.ws.SendGiftWebSocketClient;
import idv.wei.ba107g3.ws.UpdateGiftWebSocketClient;

import static android.content.Context.MODE_PRIVATE;

public class Util extends AppCompatActivity  {
   // public static String URL = "http://10.0.2.2:8081/BA107_G3/";
    //public static String URL = "http://10.120.38.5:8081/Android_BA107_G3/";
   public static String URL = "http://192.168.0.10:8081/BA107G3/";
    public final static String PREF_FILE = "preference";
    public static int count;
    public static ChatWebSocketClient chatWebSocketClient;
    public static MemberWebSocketClient memberWebSocketClient;
    public static SendGiftWebSocketClient sendGiftWebSocketClient;
    public static UpdateGiftWebSocketClient updateGiftWebSocketClient;
    private static final String TAG = "Util";
    public static int open;

    // 建立WebSocket連線
    public static void talkconnectServer(String mem_no, Context context ,String server_uri) {

        URI uri = null;
        try {
            uri = new URI(server_uri + mem_no);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        chatWebSocketClient = new ChatWebSocketClient(uri, context);
        chatWebSocketClient.connect();

    }

    public static void memberconnectServer(String mem_no, Context context ,String server_uri) {

        URI uri = null;
        try {
            uri = new URI(server_uri + mem_no);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        memberWebSocketClient = new MemberWebSocketClient(uri, context);
        memberWebSocketClient.connect();

    }

    public static void SendGiftconnectServer(String mem_no, Context context ,String server_uri) {

        URI uri = null;
        try {
            uri = new URI(server_uri + mem_no);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        sendGiftWebSocketClient = new SendGiftWebSocketClient(uri, context);
        sendGiftWebSocketClient.connect();

    }

    public static void UpdateGiftconnectServer(String mem_no,Context context ,String server_uri) {

        URI uri = null;
        try {
            uri = new URI(server_uri+ mem_no);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        updateGiftWebSocketClient = new UpdateGiftWebSocketClient(uri, context);
        updateGiftWebSocketClient.connect();

    }


    // 中斷WebSocket連線
    public static void disconnectServer() {
        if (chatWebSocketClient != null) {
            chatWebSocketClient.close();
            chatWebSocketClient = null;
        }
        if (memberWebSocketClient != null) {
            memberWebSocketClient.close();
            memberWebSocketClient = null;
        }

        if (sendGiftWebSocketClient != null) {
            sendGiftWebSocketClient.close();
            sendGiftWebSocketClient = null;
        }

        if (updateGiftWebSocketClient != null) {
            updateGiftWebSocketClient.close();
            updateGiftWebSocketClient = null;
        }

    }

    public static Bitmap getCircleBitmap(Bitmap bitmap, float roundPx)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getWidth());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static String getAge(String memAge){
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy");
        String formatDate = df.format(new java.util.Date());
        Integer year = Integer.parseInt(formatDate);
        int age = Integer.parseInt(memAge.substring(0,4));
        return String.valueOf(year-age);
    }

    public static ArrayList<GiftVO> CART = new ArrayList<>();

    public static List<MemberVO> TALK = new ArrayList<>();

    public static void showMessage(Context context,String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        if (newSize <= 50) {
            // 如果欲縮小的尺寸過小，就直接定為128
            newSize = 128;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        String text = "source image size = " + srcWidth + "x" + srcHeight;
        Log.d(TAG, text);
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
            System.gc();
            text = "\nscale = " + scale + "\nscaled image size = " +
                    srcBitmap.getWidth() + "x" + srcBitmap.getHeight();
            Log.d(TAG, text);
        }
        return srcBitmap;
    }

}
