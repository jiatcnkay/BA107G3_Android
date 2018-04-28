package idv.wei.ba107g3.event_participants;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.event.EventVO;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberVO;

public class Event_map_activity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "Event_map_activity";
    private GoogleMap mMap;
    private MemberVO member;
    private EventVO event;
    private ImageView go;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_map);
        go = findViewById(R.id.go);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        member = new Gson().fromJson(pref.getString("loginMem", ""), MemberVO.class);
        Bundle bundle = getIntent().getExtras();
        event = (EventVO) bundle.getSerializable("event");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupSelf();
        setupEvent();
    }

    private void setupSelf() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(member.getMem_latitude(), member.getMem_longitude())).title("自己"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(member.getMem_latitude(), member.getMem_longitude())));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(member.getMem_latitude(), member.getMem_longitude()))
                .zoom(8)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }

    private void setupEvent() {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        int maxResults = 1;
        try {
            addressList = geocoder.getFromLocationName(event.getEve_site(), maxResults);
        } catch (IOException ie) {
            Log.e(TAG, ie.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            Util.showMessage(Event_map_activity.this, "找不到此地方");
        } else {
            // 因為當初限定只回傳1筆，所以只要取得第1個Address物件即可
            address = addressList.get(0);
            // Address物件可以取出緯經度並轉成LatLng物件
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            byte[] pic = event.getEve_pic();
            // 將地名或地址轉成位置後在地圖打上對應標記
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(event.getEve_name())
                    .icon(BitmapDescriptorFactory.fromBitmap(Util.getCircleBitmap(getImageScale(pic), 400))));
        }
    }

    private Bitmap getImageScale(byte[] photo) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = (float) 0.45;// 放大1~2之間的亂數倍數//縮小則為除的幾倍
        float scaleHeight = (float) 0.45;// 放大1~2之間的亂數倍數//縮小則為除的幾倍

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBitmap;
    }

    // 開啟Google地圖應用程式來完成導航要求
    private void direct(double fromLat, double fromLng, double toLat, double toLng) {
        // 設定欲前往的Uri，saddr-出發地緯經度；daddr-目的地緯經度
        String uriStr = String.format(Locale.TAIWAN,
                "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                fromLat, fromLng, toLat, toLng);
        Intent intent = new Intent();
        // 指定交由Google地圖應用程式接手
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        // ACTION_VIEW-呈現資料給使用者觀看
        intent.setAction(Intent.ACTION_VIEW);
        // 將Uri資訊附加到Intent物件上
        intent.setData(Uri.parse(uriStr));
        startActivity(intent);
    }

    public void btnGo(View view){
        direct(member.getMem_latitude(),member.getMem_longitude(),address.getLatitude(),address.getLongitude());
    }
}
