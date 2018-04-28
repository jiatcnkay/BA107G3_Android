package idv.wei.ba107g3.member;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.main.Util;

public class DistanceSearchActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "DistanceSearchActivity";
    private GoogleMap mMap;
    private List<MemberVO> aLLMem = new ArrayList<>();
    private List<MemberVO> showMem = new ArrayList<>();
    private List<MemberVO> chooseMem = new ArrayList<>();
    private MemberVO member;
    private int chooseNum;
    private Spinner selectGender, selectEmotion;
    private Button btnsearch;
    private String getGender, getEmotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_search);
        selectGender = findViewById(R.id.selectGender);
        String[] gender = {getString(R.string.nochoose), getString(R.string.boy), getString(R.string.girl)};
        ArrayAdapter<String> genderadapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, gender);
        genderadapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        selectGender.setAdapter(genderadapter);
        selectGender.setSelection(0, true);
        selectGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getGender = parent.getItemAtPosition(position).toString();
                if (position == 0)
                    getGender = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        selectEmotion = findViewById(R.id.selectEmotion);
        String[] emotion = {getString(R.string.nochoose), getString(R.string.single), getString(R.string.love)
                , getString(R.string.couple), getString(R.string.breakup), getString(R.string.dontnohowtosay)
                , getString(R.string.secret)};
        ArrayAdapter<String> emotionadapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, emotion);
        emotionadapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        selectEmotion.setAdapter(emotionadapter);
        selectEmotion.setSelection(0, true);
        selectEmotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getEmotion = parent.getItemAtPosition(position).toString();
                if (position == 0)
                    getEmotion = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnsearch = findViewById(R.id.btnsearch);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showMem.size() != 0) {
                    if (getEmotion != null || getGender != null) {
                        chooseMem.clear();
                        mMap.clear();
                        setupSelf();
                        if(getEmotion != null && getGender != null) {
                            for (MemberVO memberVO : showMem) {
                                if (memberVO.getMem_gender().equals(getGender) && memberVO.getMem_emotion().equals(getEmotion))
                                    chooseMem.add(memberVO);
                            }
                        }else if(getEmotion != null && getGender == null){
                            for (MemberVO memberVO : showMem) {
                                if (memberVO.getMem_emotion().equals(getEmotion))
                                    chooseMem.add(memberVO);
                            }
                        }else if(getGender != null && getEmotion == null){
                            for (MemberVO memberVO : showMem) {
                                if (memberVO.getMem_gender().equals(getGender))
                                    chooseMem.add(memberVO);
                            }
                        }
                } else {
                        setupMap();
                        return;
                    }
                addMarkersToMap(chooseMem);
            }
        }
    });
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
    aLLMem =new Gson().fromJson(pref.getString("allMem", "").toString(), new TypeToken<List<MemberVO>>(){}.getType());
    member =new Gson().fromJson(pref.getString("loginMem",""),MemberVO.class);
    Bundle bundle = getIntent().getExtras();
    chooseNum =(int)bundle.getInt("chooseNum");
}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupSelf();
        setupMap();
    }

    private void setupSelf() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(member.getMem_latitude(), member.getMem_longitude())).title("自己"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(member.getMem_latitude(), member.getMem_longitude())));
        mMap.addCircle(new CircleOptions()
                // 必須設定圓心，因為沒有預設值
                .center(new LatLng(member.getMem_latitude(), member.getMem_longitude()))
                // 半徑長度(公尺)
                .radius(chooseNum * 1000)
                // 設定外框線的粗細(像素)，預設為10像素
                .strokeWidth(5)
                // 顏色為TRANSPARENT代表完全透明
                .strokeColor(Color.TRANSPARENT)
                // 設定填充的顏色(ARGB)，預設為黑色
                .fillColor(Color.argb(100, 0, 0, 100)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(member.getMem_latitude(), member.getMem_longitude()))
                .zoom(10 - (chooseNum / 100))
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }

    private void setupMap() {
        for (MemberVO memberVO : aLLMem) {
            if (!memberVO.getMem_no().equals(member.getMem_no())) {
                float[] results = new float[1];
                Location.distanceBetween(member.getMem_latitude(), member.getMem_longitude(),
                        memberVO.getMem_latitude(), memberVO.getMem_longitude(), results);
                String result = NumberFormat.getInstance().format(results[0]);
                StringBuilder sb = new StringBuilder();
                String[] tokens = result.split(",");
                for (int i = 0; i < tokens.length; i++) {
                    sb.append(tokens[i]);
                }
                Double distance = Double.parseDouble(sb.toString());
                if (distance < chooseNum * 1000) {
                    showMem.add(memberVO);
                }
            }
        }
        addMarkersToMap(showMem);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        MyMarkerListener listener = new MyMarkerListener();
        mMap.setOnMarkerClickListener(listener);
        mMap.setOnInfoWindowClickListener(listener);

    }

    // 在地圖上加入多個標記
    private void addMarkersToMap(List<MemberVO> showMem) {
        if (showMem.size() == 0)
            return;
        for (MemberVO memberVO : showMem) {
            byte[] photo = memberVO.getMem_photo();
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(memberVO.getMem_latitude(), memberVO.getMem_longitude()))
                    .title(memberVO.getMem_name())
                    .icon(BitmapDescriptorFactory.fromBitmap(Util.getCircleBitmap(getImageScale(photo), 400))));
        }
    }

    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View infoWindow;

        MyInfoWindowAdapter() {
            infoWindow = LayoutInflater.from(DistanceSearchActivity.this)
                    .inflate(R.layout.distance_infowindow, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            TextView mem_name = infoWindow.findViewById(R.id.mem_name);
            mem_name.setText(marker.getTitle());
            TextView btnMem = infoWindow.findViewById(R.id.btnMem);
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

private class MyMarkerListener implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {
    @Override
    // 點擊地圖上的標記
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    // 點擊標記的訊息視窗
    public void onInfoWindowClick(Marker marker) {
        if(marker.getTitle().equals("自己"))
            return;
        MemberVO member = new MemberVO();
        for(MemberVO memberVO : aLLMem){
            if(memberVO.getMem_name().equals(marker.getTitle()))
                member = memberVO;
        }
        Intent intent = new Intent(DistanceSearchActivity.this,MemberProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("member", member);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

    private Bitmap getImageScale(byte[] photo) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
// 計算缩放比例
        float scaleWidth = (float) 0.45;// 放大1~2之間的亂數倍數//縮小則為除的幾倍
        float scaleHeight = (float) 0.45;// 放大1~2之間的亂數倍數//縮小則為除的幾倍
// 取得想要缩放的matrix參數
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
// 得到新的圖片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBitmap;
    }
}
